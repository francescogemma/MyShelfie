package it.polimi.ingsw.view.tui;

import it.polimi.ingsw.controller.Response;
import it.polimi.ingsw.event.NetworkEventTransceiver;
import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.data.client.LoginEventData;
import it.polimi.ingsw.event.data.client.UsernameEventData;
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

/**
 * {@link AppLayout} which allows the user to enter its username and password and perform a login.
 *
 * @author Cristiano Migali
 */
public class LoginMenuLayout extends AppLayout {
    /**
     * Unique name of the login menu layout required to allow other AppLayouts to tell
     * the {@link it.polimi.ingsw.view.tui.terminal.drawable.app.App} to switch to this layout.
     *
     * @see AppLayout
     */
    public static final String NAME = "LOGIN_MENU";

    // Layout:

    /**
     * {@link ValueMenuEntry} which allows the user to enter its username in a {@link TextBox}.
     */
    private final ValueMenuEntry<String> usernameEntry = new ValueMenuEntry<>("Username",
        new TextBox());

    /**
     * {@link ValueMenuEntry} which allows the user to enter its password in a {@link TextBox}.
     */
    private final ValueMenuEntry<String> passwordEntry = new ValueMenuEntry<>("Password",
        new TextBox().hideText());

    /**
     * It is the {@link Button} which allows the user to perform the login synchronous request to the
     * server.
     */
    private final Button loginButton = new Button("Login");

    /**
     * It is the {@link Button} which allows the user to quit from the game.
     */
    private final Button exitButton = new Button("Exit");

    /**
     * Background layout in which all the menu entries and buttons are arranged.
     */
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

    /**
     * {@link PopUpDrawable} displayed on top of the background layout to inform the user
     * that the login has failed.
     */
    private final PopUpDrawable popUpDrawable = new PopUpDrawable(background);

    // Data:

    /**
     * {@link NetworkEventTransceiver} which allows to receive events from the server.
     */
    private NetworkEventTransceiver transceiver = null;

    /**
     * It is the username entered by the user.
     */
    private String username;

    // Utilities:

    /**
     * {@link Requester} which allows the user to perform a synchronous login request to the server.
     */
    private Requester<Response<UsernameEventData>, LoginEventData> loginRequester = null;

    /**
     * {@link PopUpQueue} used to manage the pop-ups that inform the user if a login has failed.
     */
    private PopUpQueue popUpQueue;

    /**
     * Constructor of the class.
     * It initializes the layout in which all the elements are arranged and sets the required
     * {@link Button}s callbacks.
     */
    public LoginMenuLayout() {
        setLayout(popUpDrawable.alignUpLeft().crop());

        setData(new AppLayoutData(
            Map.of(
                "username", () -> username,
                "password", passwordEntry::getValue
            )
        ));

        loginButton.onpress(() -> {
            Response<UsernameEventData> response;
            try {
                response = loginRequester
                    .request(new LoginEventData(usernameEntry.getValue(), passwordEntry.getValue()));
            } catch (DisconnectedException e) {
                displayServerResponse(Response.failure("Disconnected!"));

                return;
            }

            displayServerResponse(response);

            if (response.isOk()) {
                username = response.getWrappedData().username();
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

            loginRequester = Response.requester(transceiver, transceiver, UsernameEventData.ID, getLock());

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
