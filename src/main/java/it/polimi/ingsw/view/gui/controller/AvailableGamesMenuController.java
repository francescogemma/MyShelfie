package it.polimi.ingsw.view.gui.controller;

import it.polimi.ingsw.view.gui.LoaderException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Objects;

public class AvailableGamesMenuController extends Controller {
    @FXML private Button createNewGameButton;
    @FXML private TextField gameNameTextField;
    @FXML private Button backToLoginButton;
    @FXML private VBox availableGamesList;

    public static final String NAME = "AvailableGamesMenu";

    @FXML
    private void backToLogin() {
        Parent nextMenu;
        try {
            nextMenu = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/UserLoginMenuLayout.fxml")));
        } catch (IOException e) {
            throw new LoaderException("couldn't load resource", e);
        }
        backToLoginButton.getScene().setRoot(nextMenu);
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
    public void beforeSwitch() {}
}
