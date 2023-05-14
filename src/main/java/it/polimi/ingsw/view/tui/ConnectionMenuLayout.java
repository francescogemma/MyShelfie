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

public class ConnectionMenuLayout extends AppLayout {
    public static final String NAME = "CONNECTION_MENU";

    // Layout:
    private final ValueMenuEntry<String> ipAddressEntry = new ValueMenuEntry<>("Server IP",
        new TextBox().text("10.0.0.2"));
    private final ValueMenuEntry<Integer> portEntry = new ValueMenuEntry<>("Server port",
        new IntTextBox().integer(8080));

    private final Button nextButton = new Button("Next");
    private final Button exitButton = new Button("Exit");

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

    private final PopUpDrawable popUpDrawable = new PopUpDrawable(background);

    // Data:
    private NetworkEventTransceiver transceiver;

    // Utilities:
    private PopUpQueue popUpQueue;

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
