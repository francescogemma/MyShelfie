package it.polimi.ingsw.view.tui;

import it.polimi.ingsw.event.NetworkEventTransceiver;
import it.polimi.ingsw.networking.Connection;
import it.polimi.ingsw.networking.RMI.RMIConnection;
import it.polimi.ingsw.networking.TCP.TCPConnection;
import it.polimi.ingsw.view.tui.terminal.drawable.Orientation;
import it.polimi.ingsw.view.tui.terminal.drawable.app.App;
import it.polimi.ingsw.view.tui.terminal.drawable.app.AppLayout;
import it.polimi.ingsw.view.tui.terminal.drawable.app.AppLayoutData;
import it.polimi.ingsw.view.tui.terminal.drawable.menu.Button;
import it.polimi.ingsw.view.tui.terminal.drawable.menu.value.IntTextBox;
import it.polimi.ingsw.view.tui.terminal.drawable.menu.value.TextBox;
import it.polimi.ingsw.view.tui.terminal.drawable.menu.value.ValueMenuEntry;
import it.polimi.ingsw.view.tui.terminal.drawable.orientedlayout.OrientedLayout;

import java.util.Map;

public class ConnectionMenuLayout extends AppLayout {
    public static final String NAME = "CONNECTION_MENU";

    private final ValueMenuEntry<String> ipAddressEntry = new ValueMenuEntry<>("Server IP",
        new TextBox().text("10.0.0.2"));
    private final ValueMenuEntry<Integer> portEntry = new ValueMenuEntry<>("Server port",
        new IntTextBox().integer(8080));
    private final Button nextButton = new Button("Next");

    private NetworkEventTransceiver transceiver;

    public ConnectionMenuLayout() {
        setLayout(new OrientedLayout(Orientation.VERTICAL,
            ipAddressEntry.center().weight(1),
            portEntry.center().weight(1),
            nextButton.center().weight(1)
        ).center().scrollable().alignUpLeft().crop());

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

            Connection connection;

            try {
                if (appDataProvider.getString(App.START_NAME, "connection").equals("TCP")) {
                    connection = new TCPConnection(ipAddress, port);
                } else {
                    connection = new RMIConnection(ipAddress, port);
                }
            } catch (Exception e) {
                // TODO: Handle connection problems
                System.exit(1);
                return;
            }

            transceiver = new NetworkEventTransceiver(connection, getLock());

            switchAppLayout(LoginMenuLayout.NAME);
        });
    }

    private String connectionType;

    @Override
    public void setup(String previousLayoutName) {
        if (previousLayoutName.equals(App.START_NAME)) {
            connectionType = appDataProvider.getString(App.START_NAME, "connection");
        }
    }

    @Override
    public String getName() {
        return NAME;
    }
}
