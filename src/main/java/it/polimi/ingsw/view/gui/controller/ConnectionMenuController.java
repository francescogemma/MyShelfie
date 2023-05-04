package it.polimi.ingsw.view.gui.controller;

import it.polimi.ingsw.networking.BadHostException;
import it.polimi.ingsw.networking.BadPortException;
import it.polimi.ingsw.networking.Connection;
import it.polimi.ingsw.networking.RMI.RMIConnection;
import it.polimi.ingsw.networking.ServerNotFoundException;
import it.polimi.ingsw.networking.TCP.SocketCreationException;
import it.polimi.ingsw.networking.TCP.TCPConnection;
import it.polimi.ingsw.view.gui.LoaderException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.util.Objects;
import java.util.function.UnaryOperator;

public class ConnectionMenuController {
    @FXML private TextField serverIPTextField;
    @FXML private TextField serverPortTextField;
    @FXML private Button connectionMenuNextButton;

    @FXML
    private void initialize() {
        setServerPortTextFieldToDecimalOnly();
    }

    private void setServerPortTextFieldToDecimalOnly() {
        UnaryOperator<TextFormatter.Change> integerFilter = change -> {
            String input = change.getText();
            if (input.matches("\\d*")) {
                return change;
            }
            return null;
        };

        serverPortTextField.setTextFormatter(new TextFormatter<>(integerFilter));
    }

    @FXML
    private void switchLayout() {
        // Connection connection = null;

        try {
            /*String ipAddress = serverIPTextField.getText();
            int port = Integer.parseInt(serverPortTextField.getText());

            if (serverIPTextField.getScene().getProperties().get("connection").equals("TCP")) {
                connection = new TCPConnection(ipAddress, port);
            } else {
                connection = new RMIConnection(ipAddress, port);
            }*/

            Parent nextMenu = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/UserLoginMenuLayout.fxml")));
            connectionMenuNextButton.getScene().setRoot(nextMenu);
        } /*catch (NumberFormatException | BadPortException e) {
        } catch (BadHostException e) {
        } catch (ServerNotFoundException | SocketCreationException e) {
        }*/ catch (IOException e) {
            throw new LoaderException("Couldn't load resource", e);
        }
    }

    @FXML
    private void exit() {
        Platform.exit();
        System.exit(0);
    }
}
