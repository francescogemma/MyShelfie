package it.polimi.ingsw.view.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class GuiApplication extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Font.loadFont(getClass().getResource("/ESKARGOT.ttf").toExternalForm(), 10);

        Parent connectionMenu = FXMLLoader.load(getClass().getResource("/ConnectionMenuLayout.fxml"));
        Scene scene = new Scene(connectionMenu);
        scene.getStylesheets().add(getClass().getResource("/ConnectionMenuStyleSheet.css").toExternalForm());

        primaryStage.setTitle("My Shelfie");
        primaryStage.setMaximized(true);
        primaryStage.setScene(scene);

        primaryStage.show();
    }
}
