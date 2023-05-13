package it.polimi.ingsw.view.gui.controller;

import it.polimi.ingsw.controller.Response;
import it.polimi.ingsw.event.NetworkEventTransceiver;
import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.data.client.LoginEventData;
import it.polimi.ingsw.event.data.internal.PlayerDisconnectedInternalEventData;
import it.polimi.ingsw.networking.DisconnectedException;
import it.polimi.ingsw.view.gui.LoaderException;
import it.polimi.ingsw.view.popup.PopUp;
import it.polimi.ingsw.view.popup.PopUpQueue;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.util.Objects;

public class UserLoginMenuController extends Controller {
    @FXML private TextField usernameTextField;
    @FXML private TextField passwordTextField;
    @FXML private Button userLoginNextButton;

    public static final String NAME = "UserLoginMenu";

    // Data:
    private NetworkEventTransceiver transceiver = null;

    // Utilities:
    private Requester<Response, LoginEventData> loginRequester = null;

    private PopUpQueue popUpQueue;

    @FXML
    private void initialize() {
        if(transceiver == null) {
            transceiver = (NetworkEventTransceiver) getScene().getProperties().get("transceiver");
            loginRequester = Response.requester(transceiver, transceiver, new Object());

            PlayerDisconnectedInternalEventData.castEventReceiver(transceiver).registerListener(data -> {
                transceiver = null;
                loginRequester = null;
                if(isCurrentLayout()) {
                    Platform.runLater(() -> switchLayout(ConnectionMenuController.NAME));
                }
            });
        }

        loginRequester.registerAllListeners();

        popUpQueue = new PopUpQueue(
                text -> System.out.println(text),
                () -> System.out.println("removing pop up"),
                new Object()
        );
    }

    @FXML
    private void login() {
        Response response;

        try {
            response = loginRequester.request(new LoginEventData(usernameTextField.getText(), passwordTextField.getText()));
        } catch (DisconnectedException e) {
            // TODO: print status bar (disconnected)
            return;
        }
        // TODO: print status bar (response)

        if(response.isOk()) {
            getScene().getProperties().put("username", usernameTextField.getText());
            switchLayout(AvailableGamesMenuController.NAME);
        } else {
            popUpQueue.add(
                    response.message(),
                    PopUp.hideAfter(2000),
                    p -> {}
            );
        }
    }

    @FXML
    private void exit() {
        Platform.exit();
        System.exit(0);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void beforeSwitch() {
        if(transceiver != null) {
            loginRequester.unregisterAllListeners();
        }

        popUpQueue.disable();
        popUpQueue = null;
    }
}
