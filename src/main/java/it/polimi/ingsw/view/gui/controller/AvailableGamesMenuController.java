package it.polimi.ingsw.view.gui.controller;

import it.polimi.ingsw.view.gui.LoaderException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.util.Callback;

import java.io.IOException;
import java.util.Objects;

public class AvailableGamesMenuController extends Controller {
    @FXML private Label loggedUsername;
    @FXML private Button createNewGameButton;
    @FXML private TextField gameNameTextField;
    @FXML private Button backToLoginButton;
    @FXML private ListView<String> availableGamesListView;

    public static final String NAME = "AvailableGamesMenu";

    @FXML
    private void initialize() {
        loggedUsername.setText((String) getScene().getProperties().get("username"));

        availableGamesListView.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> stringListView) {
                return new ListCell<String>() {
                    @Override
                    protected void updateItem(String s, boolean b) {
                        super.updateItem(s, b);

                        if (s != null) {
                            HBox gameHBox = new HBox();
                            Label gameNameLabel = new Label(s);
                            HBox labelHBox = new HBox(gameNameLabel);
                            HBox.setHgrow(labelHBox, Priority.ALWAYS);
                            labelHBox.setAlignment(Pos.CENTER);
                            Button joinGameButton = new Button("Join");
                            gameHBox.getChildren().addAll(labelHBox, joinGameButton);
                            gameHBox.setPadding(new Insets(5, 5, 5, 5));
                            gameHBox.setStyle("-fx-background-color: #ddc99b; -fx-border-radius: 30; -fx-effect: innershadow(three-pass-box, dark-grey, 7, 0, 0, 0);");
                            setGraphic(gameHBox);
                        }
                    }
                };
            }
        });

        availableGamesListView.getItems().add("Game 1");
        availableGamesListView.getItems().add("Game 2");
        availableGamesListView.getItems().add("Game 3");
        availableGamesListView.getItems().add("Game 4");
        availableGamesListView.getItems().add("Game 5");
        availableGamesListView.getItems().add("Game 1");
        availableGamesListView.getItems().add("Game 2");
        availableGamesListView.getItems().add("Game 3");
        availableGamesListView.getItems().add("Game 4");
        availableGamesListView.getItems().add("Game 5");
        availableGamesListView.getItems().add("Game 1");
        availableGamesListView.getItems().add("Game 2");
        availableGamesListView.getItems().add("Game 3");
        availableGamesListView.getItems().add("Game 4");
        availableGamesListView.getItems().add("Game 5");
        availableGamesListView.getItems().add("Game 1");
        availableGamesListView.getItems().add("Game 2");
        availableGamesListView.getItems().add("Game 3");
        availableGamesListView.getItems().add("Game 4");
        availableGamesListView.getItems().add("Game 5");
        availableGamesListView.getItems().add("Game 1");
        availableGamesListView.getItems().add("Game 2");
        availableGamesListView.getItems().add("Game 3");
        availableGamesListView.getItems().add("Game 4");
        availableGamesListView.getItems().add("Game 5");
        availableGamesListView.getItems().add("Game 1");
        availableGamesListView.getItems().add("Game 2");
        availableGamesListView.getItems().add("Game 3");
        availableGamesListView.getItems().add("Game 4");
        availableGamesListView.getItems().add("Game 5");
        availableGamesListView.getItems().add("Game 1");
        availableGamesListView.getItems().add("Game 2");
        availableGamesListView.getItems().add("Game 3");
        availableGamesListView.getItems().add("Game 4");
        availableGamesListView.getItems().add("Game 5");
    }

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
