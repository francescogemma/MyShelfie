package it.polimi.ingsw.view.tui;

import it.polimi.ingsw.Client;
import it.polimi.ingsw.controller.Response;
import it.polimi.ingsw.event.MockNetworkEventTransceiver;
import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.data.LoginEventData;
import it.polimi.ingsw.view.tui.terminal.drawable.DrawableSize;
import it.polimi.ingsw.view.tui.terminal.drawable.Fill;
import it.polimi.ingsw.view.tui.terminal.drawable.Orientation;
import it.polimi.ingsw.view.tui.terminal.drawable.app.App;
import it.polimi.ingsw.view.tui.terminal.drawable.app.AppLayout;
import it.polimi.ingsw.view.tui.terminal.drawable.app.AppLayoutData;
import it.polimi.ingsw.view.tui.terminal.drawable.menu.Button;
import it.polimi.ingsw.view.tui.terminal.drawable.menu.value.TextBox;
import it.polimi.ingsw.view.tui.terminal.drawable.menu.value.ValueMenuEntry;
import it.polimi.ingsw.view.tui.terminal.drawable.orientedlayout.OrientedLayout;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.PrimitiveSymbol;
import it.polimi.ingsw.view.tui.terminal.drawable.twolayers.TwoLayersDrawable;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class LoginMenuLayout extends AppLayout {
    public static final String NAME = "LOGIN_MENU";

    private final ValueMenuEntry<String> usernameEntry = new ValueMenuEntry<>("Username",
        new TextBox());
    private final ValueMenuEntry<String> passwordEntry = new ValueMenuEntry<>("Password",
        new TextBox().hideText());
    private final Button loginButton = new Button("Login");
    private final Button exitButton = new Button("Exit");

    private final TextBox popUpTextBox = new TextBox().unfocusable();

    private final TwoLayersDrawable twoLayers = new TwoLayersDrawable(new OrientedLayout(Orientation.VERTICAL,
            usernameEntry.center().weight(33),
            passwordEntry.center().weight(33),
            new OrientedLayout(Orientation.HORIZONTAL,
                new Fill(PrimitiveSymbol.EMPTY).weight(25),
                loginButton.center().weight(25),
                exitButton.center().weight(25),
                new Fill(PrimitiveSymbol.EMPTY).weight(25)
            ).weight(34)
        ).center().scrollable().alignUpLeft().crop(),

        popUpTextBox.center().crop().fixSize(new DrawableSize(5, 30))
            .addBorderBox().center().crop()
    );

    public LoginMenuLayout() {
        setLayout(twoLayers);

        setData(new AppLayoutData(
            Map.of(
                "username", usernameEntry::getValue,
                "password", passwordEntry::getValue
            )
        ));

        loginButton.onpress(() -> {
            Requester<Response, LoginEventData> loginRequester = Response.requester(transceiver, transceiver);

            Response response = loginRequester
                .request(new LoginEventData(usernameEntry.getValue(), passwordEntry.getValue()));

            if (response.isOk()) {
                switchAppLayout(AvailableGamesMenuLayout.NAME);
            } else {
                popUpTextBox.text(response.getMessage());

                twoLayers.showForeground(true);

                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        synchronized (getLock()) {
                            twoLayers.hideForeground();
                        }
                    }
                }, 2000);
            }
        });
        exitButton.onpress(this::mustExit);
    }

    private String connectionType;
    private String serverIp;
    private int serverPort;

    private MockNetworkEventTransceiver transceiver;

    @Override
    public void setup(String previousLayoutName) {
        if (previousLayoutName.equals(App.START_NAME)) {
            connectionType = appDataProvider.getString(App.START_NAME, "connection");
            serverIp = appDataProvider.getString(App.START_NAME, "ipaddress");
            serverPort = appDataProvider.getInt(App.START_NAME, "port");

            // Getting the transceiver
            transceiver = Client.getTransceiver();
        }
    }

    @Override
    public String getName() {
        return NAME;
    }
}
