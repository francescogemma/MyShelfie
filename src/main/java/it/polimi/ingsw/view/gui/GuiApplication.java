package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.view.gui.controller.Controller;
import it.polimi.ingsw.view.gui.controller.ImageProvider;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.Objects;

/**
 * GUI application entry point.
 * It extends {@link Application} and overrides the {@link Application#start(Stage)} method
 * to make JavaFX work.
 *
 * @author Francesco Gemma
 */
public class GuiApplication extends Application {
    /**
     * Main that starts the GUI application.
     *
     * @param args optional arguments for the GUI application,
     *             here an argument, passed by the {@link it.polimi.ingsw.Client},
     *             is a String that represents the connection type.
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load custom font
        Font.loadFont(Objects.requireNonNull(getClass().getResource("/ESKARGOT.ttf")).toExternalForm(), 10);

        // Load the first scene from the FXML file
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/ConnectionMenuLayout.fxml"));
        loader.setController(Controller.getConnectionMenuControllerInstance());
        Parent connectionMenu = loader.load();
        Scene scene = new Scene(connectionMenu);
        Controller.setScene(scene);

        // Set the stage
        primaryStage.setTitle("My Shelfie");
        primaryStage.getIcons().add(new Image("/Publisher material/Icon 50x50px.png"));
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.setMinHeight(400);
        primaryStage.setMinWidth(600);

        /*
         * Save connection type.
         * The connection type is passed as an argument to the GUI application.
         * Save it in the scene properties to make it available to all the controllers.
         */
        String connectionType = getParameters().getRaw().get(0);
        primaryStage.getScene().getProperties().put("connection", connectionType);

        // Set the stage to close the application when the window is closed
        primaryStage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });

        // Show the stage
        primaryStage.show();

        new Thread(ImageProvider::getInstance).start();
    }
}
