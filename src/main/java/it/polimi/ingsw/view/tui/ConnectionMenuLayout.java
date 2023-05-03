package it.polimi.ingsw.view.tui;

import it.polimi.ingsw.event.NetworkEventTransceiver;
import it.polimi.ingsw.networking.BadHostException;
import it.polimi.ingsw.networking.BadPortException;
import it.polimi.ingsw.networking.Connection;
import it.polimi.ingsw.networking.RMI.RMIConnection;
import it.polimi.ingsw.networking.ServerNotFoundException;
import it.polimi.ingsw.networking.TCP.SocketCreationException;
import it.polimi.ingsw.networking.TCP.TCPConnection;
import it.polimi.ingsw.view.tui.terminal.drawable.BlurrableDrawable;
import it.polimi.ingsw.view.tui.terminal.drawable.DrawableSize;
import it.polimi.ingsw.view.tui.terminal.drawable.Fill;
import it.polimi.ingsw.view.tui.terminal.drawable.Orientation;
import it.polimi.ingsw.view.tui.terminal.drawable.app.App;
import it.polimi.ingsw.view.tui.terminal.drawable.app.AppLayout;
import it.polimi.ingsw.view.tui.terminal.drawable.app.AppLayoutData;
import it.polimi.ingsw.view.tui.terminal.drawable.menu.Button;
import it.polimi.ingsw.view.tui.terminal.drawable.menu.value.IntTextBox;
import it.polimi.ingsw.view.tui.terminal.drawable.menu.value.TextBox;
import it.polimi.ingsw.view.tui.terminal.drawable.menu.value.ValueMenuEntry;
import it.polimi.ingsw.view.tui.terminal.drawable.orientedlayout.OrientedLayout;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.PrimitiveSymbol;
import it.polimi.ingsw.view.tui.terminal.drawable.twolayers.TwoLayersDrawable;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class ConnectionMenuLayout extends AppLayout {
    public static final String NAME = "CONNECTION_MENU";

    private final ValueMenuEntry<String> ipAddressEntry = new ValueMenuEntry<>("Server IP",
        new TextBox().text("10.0.0.2"));
    private final ValueMenuEntry<Integer> portEntry = new ValueMenuEntry<>("Server port",
        new IntTextBox().integer(8080));
    private final Button nextButton = new Button("Next");
    private final Button exitButton = new Button("Exit");

    private final TextBox popUpTextBox = new TextBox().unfocusable();

    private final BlurrableDrawable blurrableBackground = new OrientedLayout(Orientation.VERTICAL,
            ipAddressEntry.center().weight(1),
            portEntry.center().weight(1),
            new OrientedLayout(Orientation.HORIZONTAL,
                new Fill(PrimitiveSymbol.EMPTY).weight(2),
                nextButton.center().weight(1),
                exitButton.center().weight(1),
                new Fill(PrimitiveSymbol.EMPTY).weight(2)
            ).weight(1)
        ).center().scrollable().blurrable();

    private final TwoLayersDrawable twoLayers = new TwoLayersDrawable(
        blurrableBackground,
        popUpTextBox.center().crop().fixSize(new DrawableSize(5, 30))
            .addBorderBox().center()
    );

    private NetworkEventTransceiver transceiver;

    public ConnectionMenuLayout() {
        setLayout(twoLayers.alignUpLeft().crop());

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

            Connection connection = null;

            try {
                if (appDataProvider.getString(App.START_NAME, "connection").equals("TCP")) {
                    connection = new TCPConnection(ipAddress, port);
                } else {
                    connection = new RMIConnection(ipAddress, port);
                }
            } catch (BadHostException e) {
                blurrableBackground.blur(true);
                popUpTextBox.text("The IP address that\nyou have entered\nis invalid");
                twoLayers.showForeground();
            } catch (BadPortException e) {
                blurrableBackground.blur(true);
                popUpTextBox.text("The port number that\nyou have entered\nis invalid");
                twoLayers.showForeground();
            } catch (ServerNotFoundException | SocketCreationException e) {
                blurrableBackground.blur(true);
                popUpTextBox.text("Cannot reach\nthe server");
                twoLayers.showForeground();
            } finally {
                if (connection == null) {
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            synchronized (getLock()) {
                                blurrableBackground.blur(false);
                                twoLayers.hideForeground();
                            }
                        }
                    }, 2000);
                } else {
                    transceiver = new NetworkEventTransceiver(connection, getLock());

                    switchAppLayout(LoginMenuLayout.NAME);
                }
            }
        });

        exitButton.onpress(this::mustExit);
    }

    private String connectionType;

    @Override
    public void setup(String previousLayoutName) {
        if (previousLayoutName.equals(App.START_NAME)) {
            connectionType = appDataProvider.getString(App.START_NAME, "connection");
        } else {
            blurrableBackground.blur(true);
            popUpTextBox.text("You disconnected\nfrom the server");
            twoLayers.showForeground();
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    synchronized (getLock()) {
                        blurrableBackground.blur(false);
                        twoLayers.hideForeground();
                    }
                }
            }, 2000);
        }
    }

    @Override
    public void beforeSwitch() {

    }

    @Override
    public String getName() {
        return NAME;
    }
}
