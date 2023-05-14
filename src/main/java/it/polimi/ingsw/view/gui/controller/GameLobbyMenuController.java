package it.polimi.ingsw.view.gui.controller;

import it.polimi.ingsw.controller.Response;
import it.polimi.ingsw.event.data.client.JoinLobbyEventData;
import it.polimi.ingsw.networking.DisconnectedException;
import it.polimi.ingsw.view.gui.LoaderException;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;

import java.io.IOException;
import java.util.Objects;

public class GameLobbyMenuController extends Controller {
    @FXML private Button backToAvailableGamesButton;

    public static final String NAME = "GameLobbyMenu";

    /*
    private void tryEnterLobby(String gameName) {
        new Thread(new Task<Void>() {
            @Override
            protected Void call() {
                Response response;

                try {
                    response = responseJoinLobbyEventDataRequester.request(new JoinLobbyEventData(gameName));
                } catch (DisconnectedException e) {
                    // TODO: print status bar (disconnected)
                    return null;
                }

                if (response.isOk()) {
                    switchLayout(GameLobbyMenuController.NAME);
                } else {
                    showResponse(response);
                }
                return null;
            }
        });
    }
    */

    @FXML
    private void backToAvailableGames() {
        Parent nextMenu;
        try {
            nextMenu = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/AvailableGamesMenuLayout.fxml")));
        } catch (IOException e) {
            throw new LoaderException("couldn't load resource", e);
        }
        backToAvailableGamesButton.getScene().setRoot(nextMenu);
    }

    @FXML
    private void exit() {
        Platform.exit();
        System.exit(0);
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void beforeSwitch() {

    }
}
