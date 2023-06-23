package it.polimi.ingsw.view.tui;

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
import it.polimi.ingsw.view.tui.terminal.drawable.*;
import it.polimi.ingsw.view.tui.terminal.drawable.app.App;
import it.polimi.ingsw.view.tui.terminal.drawable.app.AppLayout;
import it.polimi.ingsw.view.tui.terminal.drawable.app.AppLayoutData;
import it.polimi.ingsw.view.tui.terminal.drawable.menu.Button;
import it.polimi.ingsw.view.tui.terminal.drawable.menu.value.IntTextBox;
import it.polimi.ingsw.view.tui.terminal.drawable.menu.value.TextBox;
import it.polimi.ingsw.view.tui.terminal.drawable.menu.value.ValueMenuEntry;
import it.polimi.ingsw.view.tui.terminal.drawable.orientedlayout.OrientedLayout;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.PrimitiveSymbol;

import java.util.Map;

/**
 * {@link AppLayout} which allows user to enter the server IP and port and connect to it.
 *
 * @author Cristiano Migali
 */
public class ConnectionMenuLayout extends AppLayout {
    /**
     * Unique name of the connection menu layout required to allow other AppLayouts to tell
     * the {@link it.polimi.ingsw.view.tui.terminal.drawable.app.App} to switch to this layout.
     *
     * @see AppLayout
     */
    public static final String NAME = "CONNECTION_MENU";

    // Layout:

    /**
     * {@link ValueMenuEntry} which allows users to enter the server IP.
     */
    private final ValueMenuEntry<String> ipAddressEntry = new ValueMenuEntry<>("Server IP",
        new TextBox().text("10.0.0.4"));

    /**
     * {@link ValueMenuEntry} which allows users to enter the server port.
     */
    private final ValueMenuEntry<Integer> portEntry = new ValueMenuEntry<>("Server port",
        new IntTextBox().integer(8080));

    /**
     * {@link Button} which allows the user to try to connect to the server.
     */
    private final Button nextButton = new Button("Next");

    /**
     * {@link Button} which allows the user to exit from the game.
     */
    private final Button exitButton = new Button("Exit");

    /**
     * It is the layout of the menu. It stacks vertically the IP entry, the port entry and both the buttons, which
     * are stacked horizontally.
     */
    private final Drawable background = new OrientedLayout(Orientation.VERTICAL,
            ipAddressEntry.center().weight(1),
            portEntry.center().weight(1),
            new OrientedLayout(Orientation.HORIZONTAL,
                new Fill(PrimitiveSymbol.EMPTY).weight(2),
                nextButton.center().weight(1),
                exitButton.center().weight(1),
                new Fill(PrimitiveSymbol.EMPTY).weight(2)
            ).weight(1)
        ).center().scrollable();

    /**
     * {@link PopUpDrawable} used to notify the user if the connection to the server has failed.
     */
    private final PopUpDrawable popUpDrawable = new PopUpDrawable(background);

    // Data:

    /**
     * {@link NetworkEventTransceiver} which allows to receive events from the server.
     */
    private NetworkEventTransceiver transceiver;

    // Utilities:

    /**
     * {@link PopUpQueue} used to manage the pop-ups needed to notify the users if the connection to the server has
     * failed or if they have been disconnected.
     */
    private PopUpQueue popUpQueue;

    /**
     * Constructor of the class.
     * It initializes the layout of the connection menu.
     */
    public ConnectionMenuLayout() {
        setLayout(popUpDrawable.alignUpLeft().crop());

        setData(new AppLayoutData(
            Map.of(
                "ipaddress", ipAddressEntry::getValue,
                "port", portEntry::getValue,
                "transceiver", () -> transceiver
            )
        ));

        nextButton.onpress(() -> {
            String ipAddress = ipAddressEntry.getValue();
            int port = portEntry.getValue();

            popUpQueue.add("Trying to connect to the server...", "CONNECTING-..........",
                popUp -> {
                    Connection connection = null;

                    try {
                        if (appDataProvider.getString(App.START_NAME, "connection")
                            .equals("TCP")) {
                            connection = new TCPConnection(ipAddress, port);
                        } else {
                            connection = new RMIConnection(ipAddress, port);
                        }
                    } catch (BadHostException e) {
                        popUpQueue.add("The IP address that you have entered is invalid",
                            PopUp.hideAfter(2000), p -> {});
                    } catch (BadPortException e) {
                        popUpQueue.add("The port number that you have entered is invalid",
                            PopUp.hideAfter(2000), p -> {});
                    } catch (ServerNotFoundException | SocketCreationException e) {
                        popUpQueue.add("Cannot reach the server",
                            PopUp.hideAfter(2000), p -> {});
                    } finally {
                        popUp.askToHide();

                        if (connection != null) {
                            synchronized (getLock()) {
                                transceiver = new NetworkEventTransceiver(connection, getLock());
                                switchAppLayout(LoginMenuLayout.NAME);
                            }
                        }
                    }
                },
                popUp -> {

                });
        });

        exitButton.onpress(this::mustExit);
    }

    @Override
    public void setup(String previousLayoutName) {
        popUpQueue = new PopUpQueue(
            text -> {
                synchronized (getLock()) {
                    popUpDrawable.displayPopUp(text);
                }
            },
            () -> {
                synchronized (getLock()) {
                    popUpDrawable.hidePopUp();
                }
            }, getLock());
        popUpDrawable.hidePopUp();

        if (!previousLayoutName.equals(App.START_NAME)) {
            popUpQueue.add("You disconnected from the server", PopUp.hideAfter(2000), p -> {});
        }
    }

    @Override
    public void beforeSwitch() {
        popUpQueue.disable();
        popUpQueue = null;
    }

    @Override
    public String getName() {
        return NAME;
    }
}
