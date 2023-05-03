package it.polimi.ingsw.view.tui;

import it.polimi.ingsw.controller.Response;
import it.polimi.ingsw.controller.ResponseStatus;
import it.polimi.ingsw.event.NetworkEventTransceiver;
import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.data.client.LoginEventData;
import it.polimi.ingsw.event.data.internal.PlayerDisconnectedInternalEventData;
import it.polimi.ingsw.networking.DisconnectedException;
import it.polimi.ingsw.view.tui.terminal.drawable.BlurrableDrawable;
import it.polimi.ingsw.view.tui.terminal.drawable.DrawableSize;
import it.polimi.ingsw.view.tui.terminal.drawable.Fill;
import it.polimi.ingsw.view.tui.terminal.drawable.Orientation;
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
    private final BlurrableDrawable blurrableBackground = new OrientedLayout(Orientation.VERTICAL,
            usernameEntry.center().weight(1),
            passwordEntry.center().weight(1),
            new OrientedLayout(Orientation.HORIZONTAL,
                new Fill(PrimitiveSymbol.EMPTY).weight(2),
                loginButton.center().weight(1),
                exitButton.center().weight(1),
                new Fill(PrimitiveSymbol.EMPTY).weight(2)
            ).weight(1)
        ).center().scrollable().blurrable();

    private final TextBox popUpTextBox = new TextBox().unfocusable();

    private final TwoLayersDrawable twoLayers = new TwoLayersDrawable(
        blurrableBackground,
        popUpTextBox.center().crop().fixSize(new DrawableSize(5, 30))
            .addBorderBox().center()
    );

    public LoginMenuLayout() {
        setLayout(twoLayers.alignUpLeft().crop());

        setData(new AppLayoutData(
            Map.of(
                "username", usernameEntry::getValue,
                "password", passwordEntry::getValue
            )
        ));

        loginButton.onpress(() -> {
            Response response;
            try {
                response = loginRequester
                    .request(new LoginEventData(usernameEntry.getValue(), passwordEntry.getValue()));
            } catch (DisconnectedException e) {
                displayServerResponse(new Response("Disconnected!", ResponseStatus.FAILURE));
                return;
            }

            displayServerResponse(response);

            if (response.isOk()) {
                switchAppLayout(AvailableGamesMenuLayout.NAME);
            } else {
                popUpTextBox.text(response.message());

                blurrableBackground.blur(true);
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
        });

        exitButton.onpress(() -> {
            transceiver.disconnect();
            mustExit();
        });
    }

    private NetworkEventTransceiver transceiver = null;
    private Requester<Response, LoginEventData> loginRequester = null;

    @Override
    public void setup(String previousLayoutName) {
        if (previousLayoutName.equals(ConnectionMenuLayout.NAME)) {
            transceiver = (NetworkEventTransceiver)
                appDataProvider.get(ConnectionMenuLayout.NAME, "transceiver");

            loginRequester = Response.requester(transceiver, transceiver, getLock());

            PlayerDisconnectedInternalEventData.castEventReceiver(transceiver).registerListener(data -> {
                transceiver = null;
                loginRequester = null;

                if (isCurrentLayout()) {
                    switchAppLayout(ConnectionMenuLayout.NAME);
                }
            });
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
