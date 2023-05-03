package it.polimi.ingsw.view.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

import java.io.IOException;
import java.util.Objects;
import java.util.function.UnaryOperator;

public class UserLoginMenuLayoutController {
    @FXML private TextField usernameTextField;
    @FXML private TextField passwordTextField;
    @FXML private Button userLoginNextButton;
    @FXML
    private void switchLayout() {
        Parent nextMenu;
        try {
            nextMenu = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/AvailableGamesMenuLayout.fxml")));
        } catch (IOException e) {
            throw new LoaderException(e.getMessage());
        }
        userLoginNextButton.getScene().setRoot(nextMenu);
    }
}
