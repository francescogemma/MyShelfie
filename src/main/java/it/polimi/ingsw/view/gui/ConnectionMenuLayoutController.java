package it.polimi.ingsw.view.gui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class ConnectionMenuLayoutController {
    public Button connectionMenuNextButton = null;
    public TextField serverPortTextField = null;

    public void initialize() {
        serverPortTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    serverPortTextField.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
    }
}
