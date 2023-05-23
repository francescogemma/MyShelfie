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
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.util.function.UnaryOperator;

/**
 * Controller for the connection menu layout.
 */
public class ConnectionMenuController extends Controller {
    @FXML private TextField serverIPTextField;
    @FXML private TextField serverPortTextField;
    @FXML private Button connectionMenuNextButton;
    @FXML private Pane connectionBackgroundBlurPane;
    @FXML private VBox connectionPopUpMessageBackground;
    @FXML private Label connectionPopUpLabel;

    public static final String NAME = "ConnectionMenu";

    private PopUpQueue popUpQueue;

    @FXML
    private void initialize() {
        // set the server port text field to accept only decimal numbers
        setServerPortTextFieldToDecimalOnly();

        // initialize the PopUpQueue
        popUpQueue = new PopUpQueue(
                text -> Platform.runLater(() -> {
                    connectionPopUpLabel.setText(text);
                    connectionBackgroundBlurPane.setVisible(true);
                    connectionPopUpMessageBackground.setVisible(true);
                }),
                () -> Platform.runLater(() -> {
                    connectionBackgroundBlurPane.setVisible(false);
                    connectionPopUpMessageBackground.setVisible(false);
                }),
                new Object()
        );

        // if we return to this layout because of a disconnection, show a disconnected pop up message
        if(!getPreviousLayoutName().equals(Controller.START_NAME)) {
            popUpQueue.add(
                    "You have been disconnected from the server",
                    PopUp.hideAfter(2000),
                    p -> {}
            );
        }
    }

    /**
     * Set the server port text field to accept only decimal numbers
     */
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

    /**
     * callback for the "Next" button.
     * It checks if the IP address and the port are valid and then tries to connect to the server.
     * If the connection is successful, it switches to the next layout.
     * If the connection fails, it shows a pop up message.
     */
    @FXML
    private void connect() {
        String ipAddress = serverIPTextField.getText();
        int port = Integer.parseInt(serverPortTextField.getText());

        popUpQueue.add(
                "Trying to connect to the server...",
                "CONNECTING",
                popUp -> {
                    // create a new task to connect to the server
                    Task<Void> connect = new Task<>() {
                        @Override
                        protected Void call() {
                            Connection connection = null;

                            try {
                                /* retrieve the connection type from the scene properties
                                 * and create the connection
                                 */
                                if (getScene().getProperties().get("connection").equals("TCP")) {
                                    connection = new TCPConnection(ipAddress, port);
                                } else {
                                    connection = new RMIConnection(ipAddress, port);
                                }
                            } catch (NumberFormatException | BadPortException e) {
                                popUpQueue.add(
                                        "Invalid port number",
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
                                    // if a connection has been created, set it as the transceiver and switch layout
                                    setProperty("transceiver", new NetworkEventTransceiver(connection, new Object()));
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

    /**
     * callback for the "Exit" button.
     * It closes the application.
     */
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
