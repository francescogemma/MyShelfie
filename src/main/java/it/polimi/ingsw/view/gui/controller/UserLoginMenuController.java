package it.polimi.ingsw.view.gui.controller;

import it.polimi.ingsw.controller.Response;
import it.polimi.ingsw.event.NetworkEventTransceiver;
import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.data.client.LoginEventData;
import it.polimi.ingsw.event.data.client.UsernameEventData;
import it.polimi.ingsw.event.data.internal.PlayerDisconnectedInternalEventData;
import it.polimi.ingsw.networking.DisconnectedException;
import it.polimi.ingsw.view.popup.PopUp;
import it.polimi.ingsw.view.popup.PopUpQueue;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * This menu is part of the game's graphical use interface. It consists of two text fields, that should hold the user's
 * username and password, respectively.
 */
public class UserLoginMenuController extends Controller {
    /**
     * A text field that holds the user's name.
     */
    @FXML private TextField usernameTextField;

    /**
     * A text field that holds the user's password.
     */
    @FXML private TextField passwordTextField;

    /**
     * A button to submit the provided information, to attempt a login.
     */
    @FXML private Button userLoginNextButton;

    /**
     * A pane used to temporarily stop interaction with the menu's background, and move attention towards the foreground,
     * where a popup message may be visible.
     */
    @FXML private Pane userLoginBackgroundBlurPane;

    /**
     * A box holding the interface style for a pop up message. It contains a text field with relevant information addressed
     * to the user.
     */
    @FXML private VBox userLoginPopUpMessageBackground;

    /**
     * A label that only gets set to visible for a short period of time, in order to implement a sort of pop up window,
     * containing a message for the user.
     */
    @FXML private Label userLoginPopUpLabel;

    /**
     * Utility class attribute that stores this menu's name. This attribute is often used by other layouts, to switch to
     * the next menu without needing to rewrite and consequentially expose its name.
     */
    public static final String NAME = "UserLoginMenu";

    // Data:

    /**
     * The current active transceiver is stored here. This object is initialized in the "initialize" method within this
     * class.
     */
    private NetworkEventTransceiver transceiver = null;

    // Utilities:

    /**
     * Used to get a response from the server upon requesting a new login.
     */
    private Requester<Response<UsernameEventData>, LoginEventData> loginRequester = null;

    /**
     * Used to handle multiple popup requests. In this particular case, all popups are relative to login state
     * messages.
     */
    private PopUpQueue popUpQueue;

    /**
     * Boot up the requester, and set up simple behaviour for the popup queue object.
     */
    @FXML
    private void initialize() {
        if(transceiver == null) {
            transceiver = (NetworkEventTransceiver) getScene().getProperties().get("transceiver");
            loginRequester = Response.requester(transceiver, transceiver, UsernameEventData.ID, new Object());

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
                text -> Platform.runLater(() -> {
                    userLoginPopUpLabel.setText(text);
                    userLoginBackgroundBlurPane.setVisible(true);
                    userLoginPopUpMessageBackground.setVisible(true);
                }),
                () -> Platform.runLater(() -> {
                    userLoginBackgroundBlurPane.setVisible(false);
                    userLoginPopUpMessageBackground.setVisible(false);
                }),
                new Object()
        );
    }

    /**
     * This method gets called once the user submits its information to the server through the userLoginNextButton,
     * It sends a request to the server, and checks if the response is valid.
     */
    @FXML
    private void login() {
        new Thread(new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Response<UsernameEventData> response;

                try {
                    response = loginRequester.request(new LoginEventData(usernameTextField.getText(), passwordTextField.getText()));
                } catch (DisconnectedException e) {
                    showResponse(Response.failure("Disconnected!"));
                    return null;
                }
                showResponse(response);

                if(response.isOk()) {
                    getScene().getProperties().put("username", response.getWrappedData().username());
                    switchLayout(AvailableGamesMenuController.NAME);
                } else {
                    popUpQueue.add(
                            response.message(),
                            PopUp.hideAfter(2000),
                            p -> {}
                    );
                }

                return null;
            }
        }).start();
    }

    /**
     * This method is called if the user requests to close the window in any way, and ends the process.
     */
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
