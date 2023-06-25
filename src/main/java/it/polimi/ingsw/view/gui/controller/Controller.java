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

/**
 * Abstract class that handles the basic functionalities of a controller.
 * It is extended by all the controllers.
 * <p>
 * It handles switching between scenes, status bar
 * and properties to be saved in the scene.
 */
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
     * since all controllers technically exist simultaneously. We need to determine which one is the active one.
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

    /**
     * All controllers are stored in this map, to easily access them using their string names.
     * For example, when the next controller is needed, we can load the next layout's name, and
     * look for the actual object within this map.
     */
    private static final Map<String, Controller> controllers = Map.of(
            AvailableGamesMenuController.NAME, new AvailableGamesMenuController(),
            GameLobbyMenuController.NAME, new GameLobbyMenuController(),
            ScoreboardMenuController.NAME, new ScoreboardMenuController(),
            UserLoginMenuController.NAME, new UserLoginMenuController(),
            ConnectionMenuController.NAME, new ConnectionMenuController(),
            GameController.NAME, new GameController()
    );

    /**
     * This is a constructor to this class. However, this class is intended to be subclassed.
     */
    protected Controller() {}

    /**
     * Lower the flag marking this layout as "current", and load the one provided as a parameter.
     * @param nextLayoutName is the next layout, that will replace the current one, and get rendered on screen.
     */
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

    /**
     * Getter method for controller's name.
     * @return the name of this controller.
     */
    public abstract String getName();

    /**
     * This method contains a list of customizable actions to perform right before a switch from the current layout to
     * the next layout.
     */
    public abstract void beforeSwitch();

    /**
     * Getter method for controller's previous layout name. This method is usually needed to code the "back" button,
     * to navigate the interface backwards.
     *
     * @return a string representing the previous layout's name.
     */
    public String getPreviousLayoutName() {
        return previousLayoutName;
    }

    /**
     * Add a message to the status bar, boot the timer so that it's shown for a short time frame, and hide the status bar.
     * @param message is a string containing a message to show the user for a short time frame.
     */
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

    /**
     * Wrapper method that reads a response's status and message, and renders it on screen, through the status bar.
     * @param response is a response object that will be unpacked and rendered on screen on the status bar.
     */
    protected void showResponse(Response response) {
        Objects.requireNonNull(response);
        showMessageStatusBar("%s: %s".formatted(response.status(), response.message()));
    }

    /**
     * Getter function for the connection menu's controller instance.
     * @return the instance of the connection menu's controller.
     */
    public static Controller getConnectionMenuControllerInstance() {
        return controllers.get(ConnectionMenuController.NAME);
    }

    /**
     * Sets a JavaFX scene property, on this controller's layout's scene.
     * @param property is the JavaFX property's name.
     * @param value is the value assigned to the JavaFX property.
     */
    public void setProperty(String property, Object value) {
        Platform.runLater(() -> getScene().getProperties().put(property, value));
    }

    /**
     * Getter method for a JavaFX scene property, relative to this controller's scene.
     * @param property is the property's name.
     * @return the requested property's value.
     * @param <T> is the property's type.
     */
    public <T> T getValue(String property) {
        return (T) getScene().getProperties().get(property);
    }

    /**
     * Use this to check if this layout coincides with the one being rendered on screen.
     * @return True if this controller contains the active layout.
     */
    public boolean isCurrentLayout() {
        return isCurrentLayout;
    }

    /**
     * Sets this layout's scene.
     * Do not use this method to change the layout, use SwitchLayout instead.
     * @param scene will be assigned to this layout.
     */
    public static void setScene(Scene scene) {
        Controller.scene = scene;
    }

    /**
     * Getter method for this controller's layout's scene.
     * @return the current controller's scene.
     */
    protected Scene getScene() {
        return scene;
    }
}
