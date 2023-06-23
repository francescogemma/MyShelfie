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
 * This is a controller corresponding to the connection menu.
 * This menu is part of the game's graphical use interface. If features two text fields, allowing the user to set a server IP
 * address and port to connect to.
 */
public class ConnectionMenuController extends Controller {
    /**
     * Used to contain a string (provided by the user) corresponding to the host server's IP address.
     */
    @FXML private TextField serverIPTextField;

    /**
     * Used to contain a string (provided by th user) corresponding to the host server's port address.
     */
    @FXML private TextField serverPortTextField;

    /**
     * Used to submit the current information contained in the text fields, and request a new connection.
     */
    @FXML private Button connectionMenuNextButton;

    /**
     * An invisible pane, rendered visible as soon as the main menu objects need to go out of focus, to leave
     * space to an overlay message.
     */
    @FXML private Pane connectionBackgroundBlurPane;

    /**
     * This vertical box represents an area of space used to contain a label.
     * This label will contain a message to the user, and, upon activation, will put the other interface elements out of
     * focus.
     */
    @FXML private VBox connectionPopUpMessageBackground;

    /**
     * Informational label, containing a message to the user. It is contained within an invisible object by default, and
     * will not be visible.
     */
    @FXML private Label connectionPopUpLabel;

    /**
     * Utility class attribute that stores this menu's name. This attribute is often used by other layouts, to switch to
     * the next menu without needing to rewrite and consequentially expose its name.
     */
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
