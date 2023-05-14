package it.polimi.ingsw.view.gui.controller;

import it.polimi.ingsw.controller.Response;
import it.polimi.ingsw.view.gui.LoaderException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public abstract class Controller {
    protected static final String START_NAME = "Start";
    private String previousLayoutName = START_NAME;
    private boolean isCurrentLayout = true;
    private static Scene scene;
    private Timer timerStatusBar;
    @FXML private Label statusBar;

    private static final Map<String, Controller> controllers = Map.of(
            AvailableGamesMenuController.NAME, new AvailableGamesMenuController(),
            GameLobbyMenuController.NAME, new GameLobbyMenuController(),
            ScoreboardMenuController.NAME, new ScoreboardMenuController(),
            UserLoginMenuController.NAME, new UserLoginMenuController(),
            ConnectionMenuController.NAME, new ConnectionMenuController(),
            GameController.NAME, new GameController()
    );

    protected Controller() {}

    public void switchLayout(String nextLayoutName) {
        Platform.runLater(() -> {
            beforeSwitch();

            controllers.get(nextLayoutName).previousLayoutName = getName();
            controllers.get(nextLayoutName).isCurrentLayout = true;
            isCurrentLayout = false;

            Parent nextLayout;
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/" + nextLayoutName + "Layout.fxml"));
                loader.setController(controllers.get(nextLayoutName));
                nextLayout = loader.load();
            } catch (IOException e) {
                throw new LoaderException("Could not load " + nextLayoutName + "Layout", e);
            }
            scene.setRoot(nextLayout);
        });
    }

    public abstract String getName();

    public abstract void beforeSwitch();

    public String getPreviousLayoutName() {
        return previousLayoutName;
    }

    protected void showMessageStatusBar (String message) {
        Objects.requireNonNull(message);

        Platform.runLater(() -> {
            if (timerStatusBar != null) {
                timerStatusBar.cancel();
                timerStatusBar = null;
            }

            this.timerStatusBar = new Timer();

            this.statusBar.setText(message);
            this.statusBar.setVisible(true);

            this.timerStatusBar.schedule(new TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(() -> {
                        statusBar.setVisible(false);
                    });
                }
            }, 2000);
        });
    }

    protected void showResponse(Response response) {
        Objects.requireNonNull(response);
        showMessageStatusBar("%s: %s".formatted(response.status(), response.message()));
    }

    public static Controller getConnectionMenuControllerInstance() {
        return controllers.get(ConnectionMenuController.NAME);
    }

    public void setProperty(String property, Object value) {
        Platform.runLater(() -> {
            getScene().getProperties().put(property, value);
        });
    }


    public <T> T getValue(String property) {
        return (T) getScene().getProperties().get(property);
    }

    public boolean isCurrentLayout() {
        return isCurrentLayout;
    }

    public static void setScene(Scene scene) {
        Controller.scene = scene;
    }

    protected Scene getScene() {
        return scene;
    }
}
