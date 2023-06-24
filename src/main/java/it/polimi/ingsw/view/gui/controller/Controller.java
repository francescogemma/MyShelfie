package it.polimi.ingsw.view.gui.controller;

import it.polimi.ingsw.controller.Response;
import it.polimi.ingsw.view.gui.LoaderException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public abstract class Controller {
    /**
     * Dummy string to assign the name of the previous layout. All actual implementations will use more appropriate names
     * to identify the previous layout.
     */
    protected static final String START_NAME = "Start";

    /**
     * The name of the previous layout, relative to the current one.
     * This attribute is usually needed to code the "back" button, to navigate the interface backwards.
     */
    private String previousLayoutName = START_NAME;

    /**
     * Simple flag to determine what layout is currently active, or being rendered to the user in general. This is useful
     * since all controllers technically exist simultaneously. We need to determine which one is the acive one.
     */
    private boolean isCurrentLayout = true;

    /**
     * Use this attribute to set a new JavaFX scene.
     */
    private static Scene scene;

    /**
     * Used to monitor the status bar's visibility state. The status bar stays visible for a short time frame, controlled by
     * this particular timer.
     */
    private Timer timerStatusBar;

    /**
     * Every Controller must have a status bar. This is an HBox containing it.
     */
    @FXML private HBox statusBarHBox;

    /**
     * This is a label contained within the status bar's HBox.
     */
    @FXML private Label statusBarLabel;

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

            this.statusBarLabel.setText(message);
            this.statusBarHBox.setVisible(true);

            this.timerStatusBar.schedule(new TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(() -> statusBarHBox.setVisible(false));
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
        Platform.runLater(() -> getScene().getProperties().put(property, value));
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
