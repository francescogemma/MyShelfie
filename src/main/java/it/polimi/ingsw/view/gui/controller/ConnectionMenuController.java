package it.polimi.ingsw.view.gui.controller;

import it.polimi.ingsw.event.NetworkEventTransceiver;
import it.polimi.ingsw.networking.BadHostException;
import it.polimi.ingsw.networking.BadPortException;
import it.polimi.ingsw.networking.Connection;
import it.polimi.ingsw.networking.RMI.RMIConnection;
import it.polimi.ingsw.networking.ServerNotFoundException;
import it.polimi.ingsw.networking.TCP.SocketCreationException;
import it.polimi.ingsw.networking.TCP.TCPConnection;
import it.polimi.ingsw.view.popup.PopUp;
import it.polimi.ingsw.view.popup.PopUpQueue;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

import java.util.function.UnaryOperator;

public class ConnectionMenuController extends Controller {
    @FXML private TextField serverIPTextField;
    @FXML private TextField serverPortTextField;
    @FXML private Button connectionMenuNextButton;

    public static final String NAME = "ConnectionMenu";

    private PopUpQueue popUpQueue;

    @FXML
    private void initialize() {
        setServerPortTextFieldToDecimalOnly();

        popUpQueue = new PopUpQueue(
                text -> System.out.println(text),
                () -> System.out.println("removing pop up"),
                new Object()
        );

        if(!getPreviousLayoutName().equals(Controller.START_NAME)) {
            popUpQueue.add(
                    "You have been disconnected from the server",
                    PopUp.hideAfter(2000),
                    p -> {}
            );
        }
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
    private void connect() {
        popUpQueue.add(
                "Trying to connect to the server...",
                "CONNECTING",
                popUp -> {
                    Task<Void> connect = new Task<>() {
                        @Override
                        protected Void call() {
                            Connection connection = null;

                            try {
                                String ipAddress = serverIPTextField.getText();
                                int port = Integer.parseInt(serverPortTextField.getText());

                                if (getScene().getProperties().get("connection").equals("TCP")) {
                                    connection = new TCPConnection(ipAddress, port);
                                } else {
                                    connection = new RMIConnection(ipAddress, port);
                                }
                            } catch (NumberFormatException | BadPortException e) {
                                popUpQueue.add(
                                        "Invalid port",
                                        PopUp.hideAfter(2000),
                                        p -> {}
                                );
                            } catch (BadHostException e) {
                                popUpQueue.add(
                                        "Invalid IP address",
                                        PopUp.hideAfter(2000),
                                        p -> {}
                                );
                            } catch (ServerNotFoundException | SocketCreationException e) {
                                popUpQueue.add(
                                        "Server not found",
                                        PopUp.hideAfter(2000),
                                        p -> {}
                                );
                            } finally {
                                popUp.askToHide();
                                if(connection != null) {
                                    getScene().getProperties().put("transceiver", new NetworkEventTransceiver(connection, new Object()));
                                    switchLayout(UserLoginMenuController.NAME);
                                }
                            }
                            return null;
                        }
                    };

                    new Thread(connect).start();
                },
                p -> {}
        );
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
    public void beforeSwitch() {
        popUpQueue.disable();
    }
}
