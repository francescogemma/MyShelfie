package it.polimi.ingsw.view.tui;

import it.polimi.ingsw.controller.Response;
import it.polimi.ingsw.controller.ResponseStatus;
import it.polimi.ingsw.event.NetworkEventTransceiver;
import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.data.client.LoginEventData;
import it.polimi.ingsw.event.data.internal.PlayerDisconnectedInternalEventData;
import it.polimi.ingsw.networking.DisconnectedException;
import it.polimi.ingsw.view.popup.PopUp;
import it.polimi.ingsw.view.popup.PopUpQueue;
import it.polimi.ingsw.view.tui.terminal.drawable.*;
import it.polimi.ingsw.view.tui.terminal.drawable.app.AppLayout;
import it.polimi.ingsw.view.tui.terminal.drawable.app.AppLayoutData;
import it.polimi.ingsw.view.tui.terminal.drawable.menu.Button;
import it.polimi.ingsw.view.tui.terminal.drawable.menu.value.TextBox;
import it.polimi.ingsw.view.tui.terminal.drawable.menu.value.ValueMenuEntry;
import it.polimi.ingsw.view.tui.terminal.drawable.orientedlayout.OrientedLayout;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.PrimitiveSymbol;

import java.util.Map;

public class LoginMenuLayout extends AppLayout {
    public static final String NAME = "LOGIN_MENU";

    // Layout:
    private final ValueMenuEntry<String> usernameEntry = new ValueMenuEntry<>("Username",
        new TextBox());
    private final ValueMenuEntry<String> passwordEntry = new ValueMenuEntry<>("Password",
        new TextBox().hideText());
    private final Button loginButton = new Button("Login");
    private final Button exitButton = new Button("Exit");
    private final Drawable background = new OrientedLayout(Orientation.VERTICAL,
            usernameEntry.center().weight(1),
            passwordEntry.center().weight(1),
            new OrientedLayout(Orientation.HORIZONTAL,
                new Fill(PrimitiveSymbol.EMPTY).weight(2),
                loginButton.center().weight(1),
                exitButton.center().weight(1),
                new Fill(PrimitiveSymbol.EMPTY).weight(2)
            ).weight(1)
        ).center().scrollable();

    private final PopUpDrawable popUpDrawable = new PopUpDrawable(background);

    // Data:
    private NetworkEventTransceiver transceiver = null;

    // Utilities:
    private Requester<Response, LoginEventData> loginRequester = null;

    private PopUpQueue popUpQueue;

    public LoginMenuLayout() {
        setLayout(popUpDrawable.alignUpLeft().crop());

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
                popUpQueue.add(response.message(), PopUp.hideAfter(2000), p -> {});
            }
        });

        exitButton.onpress(() -> {
            transceiver.disconnect();
            mustExit();
        });
    }

    @Override
    public void setup(String previousLayoutName) {
        if (!previousLayoutName.equals(ConnectionMenuLayout.NAME) &&
            !previousLayoutName.equals(AvailableGamesMenuLayout.NAME)) {
            throw new IllegalStateException("You can reach LoginMenuLayout only from ConnectionMenuLayout" +
                " or AvailableGamesMenuLayout");
        }

        if (transceiver == null) {
            transceiver = (NetworkEventTransceiver) appDataProvider.get(ConnectionMenuLayout.NAME,
                "transceiver");

            loginRequester = Response.requester(transceiver, transceiver, getLock());

            PlayerDisconnectedInternalEventData.castEventReceiver(transceiver).registerListener(data -> {
                transceiver = null;
                loginRequester = null;

                if (isCurrentLayout()) {
                    switchAppLayout(ConnectionMenuLayout.NAME);
                }
            });
        }

        loginRequester.registerAllListeners();

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
    }

    @Override
    public void beforeSwitch() {
        if (transceiver != null) {
            loginRequester.unregisterAllListeners();
        }

        popUpQueue.disable();
        popUpQueue = null;
    }

    @Override
    public String getName() {
        return NAME;
    }
}
