package it.polimi.ingsw.view.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.Objects;

public class GuiApplication extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Font.loadFont(Objects.requireNonNull(getClass().getResource("/ESKARGOT.ttf")).toExternalForm(), 10);

        Parent connectionMenu = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/ConnectionMenuLayout.fxml")));
        Scene scene = new Scene(connectionMenu);

        primaryStage.setTitle("My Shelfie");
        primaryStage.getIcons().add(new Image("/Publisher material/Icon 50x50px.png"));
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);

        primaryStage.show();
    }
}
