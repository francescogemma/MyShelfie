package it.polimi.ingsw.view.gui.controller;

import it.polimi.ingsw.view.gui.LoaderException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;

import java.io.IOException;
import java.util.Objects;

public class GameLobbyMenuController {
    @FXML private Button backToAvailableGamesButton;

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
}
