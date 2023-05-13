package it.polimi.ingsw.view.gui.controller;

import it.polimi.ingsw.view.gui.LoaderException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;
import java.util.Map;

public abstract class Controller {
    protected static final String START_NAME = "Start";
    private String previousLayoutName = START_NAME;
    private boolean isCurrentLayout = true;
    private static Scene scene;
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
    }

    public abstract String getName();

    public abstract void beforeSwitch();

    public String getPreviousLayoutName() {
        return previousLayoutName;
    }

    public static Controller getConnectionMenuControllerInstance() {
        return controllers.get(ConnectionMenuController.NAME);
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
