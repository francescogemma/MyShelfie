package it.polimi.ingsw.view.gui.controller;

import it.polimi.ingsw.view.gui.LoaderException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.util.Objects;

public class UserLoginMenuController {
    @FXML private TextField usernameTextField;
    @FXML private TextField passwordTextField;
    @FXML private Button userLoginNextButton;

    @FXML
    private void switchLayout() {
        usernameTextField.getScene().getProperties().put("username", usernameTextField.getText());

        Parent nextMenu;
        try {
            nextMenu = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/AvailableGamesMenuLayout.fxml")));
        } catch (IOException e) {
            throw new LoaderException("couldn't load resource", e);
        }

        Label loggedUsernameLabel = (Label) nextMenu.lookup("#loggedUsername");
        loggedUsernameLabel.setText(usernameTextField.getText());

        userLoginNextButton.getScene().setRoot(nextMenu);
    }

    @FXML
    private void exit() {
        Platform.exit();
    }
}
