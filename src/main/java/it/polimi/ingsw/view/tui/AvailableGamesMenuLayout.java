package it.polimi.ingsw.view.tui;

import it.polimi.ingsw.controller.Response;
import it.polimi.ingsw.controller.ResponseStatus;
import it.polimi.ingsw.event.NetworkEventTransceiver;
import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.data.client.CreateNewGameEventData;
import it.polimi.ingsw.event.data.client.LogoutEventData;
import it.polimi.ingsw.event.data.client.PlayerHasJoinMenu;
import it.polimi.ingsw.event.data.game.GameHasBeenCreatedEventData;
import it.polimi.ingsw.event.data.internal.PlayerDisconnectedInternalEventData;
import it.polimi.ingsw.networking.DisconnectedException;
import it.polimi.ingsw.view.tui.terminal.drawable.*;
import it.polimi.ingsw.view.tui.terminal.drawable.app.AppLayout;
import it.polimi.ingsw.view.tui.terminal.drawable.app.AppLayoutData;
import it.polimi.ingsw.view.tui.terminal.drawable.menu.Button;
import it.polimi.ingsw.view.tui.terminal.drawable.menu.value.TextBox;
import it.polimi.ingsw.view.tui.terminal.drawable.menu.value.ValueMenuEntry;
import it.polimi.ingsw.view.tui.terminal.drawable.orientedlayout.FullyResizableOrientedLayout;
import it.polimi.ingsw.view.tui.terminal.drawable.orientedlayout.OrientedLayout;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.Color;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.PrimitiveSymbol;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AvailableGamesMenuLayout extends AppLayout {
    public static final String NAME = "AVAILABLE_GAMES_MENU";

    private final TextBox usernameTextBox = new TextBox().unfocusable();
    private final Drawable displayUsername = new OrientedLayout(Orientation.HORIZONTAL,
        new TextBox().text("Logged in as: ").unfocusable().weight(1),
        usernameTextBox.color(Color.RED).weight(1)
    ).center().crop().fixSize(new DrawableSize(3, 25));
    private final Drawable noAvailableGamesTextBox = new TextBox().text("There aren't available games!")
        .unfocusable().center();
    private static class JoinableGameDrawable extends FixedLayoutDrawable<Drawable> {
        private final TextBox textBox = new TextBox();
        private final Button button = new Button("Join");

        public JoinableGameDrawable() {
            setLayout(new FullyResizableOrientedLayout(Orientation.HORIZONTAL,
                textBox.unfocusable().center().crop().weight(1),
                button.center().crop().weight(1)
            ).fixSize(new DrawableSize(5, 80)).addBorderBox());
        }
    }
    private final RecyclerDrawable<JoinableGameDrawable, String> recyclerGamesList = new RecyclerDrawable<>(Orientation.VERTICAL,
        JoinableGameDrawable::new, (joinableGameDrawable, name) -> {
                    joinableGameDrawable.textBox.text(name);
                    joinableGameDrawable.button.onpress(() -> {
                        selectedGameName = name;
                        switchAppLayout(LobbyLayout.NAME);
                    });
                });
    private final AlternativeDrawable alternative = new AlternativeDrawable(noAvailableGamesTextBox, recyclerGamesList.center()
                .scrollable());
    private final ValueMenuEntry<String> gameNameEntry = new ValueMenuEntry<>("New game's name", new TextBox());
    private final Button createNewGameButton = new Button("Create new game");
    private final Button backToLoginButton = new Button("Back to login");
    private final Button exitButton = new Button("Exit");

    private List<String> availableGames;
    private String selectedGameName;

    public AvailableGamesMenuLayout() {
        setLayout(new OrientedLayout(Orientation.VERTICAL,
            displayUsername.alignUpLeft().weight(1),
            alternative.center().weight(7),
            new OrientedLayout(Orientation.HORIZONTAL,
                new Fill(PrimitiveSymbol.EMPTY).weight(1),
                gameNameEntry.center().weight(1),
                createNewGameButton.center().weight(1),
                backToLoginButton.center().weight(1),
                exitButton.center().weight(1),
                new Fill(PrimitiveSymbol.EMPTY).weight(1)
            ).center().weight(2)
        ).center().crop());

        setData(new AppLayoutData(Map.of(
            "selectedgame", () -> selectedGameName
        )));

        createNewGameButton.onpress(() -> {
            try {
                displayServerResponse(createGameRequester.request(new CreateNewGameEventData(gameNameEntry.getValue())));
            } catch (DisconnectedException e) {
                displayServerResponse(new Response("Disconnected!", ResponseStatus.FAILURE));
            }
        });

        backToLoginButton.onpress(() -> {
            try {
                displayServerResponse(logoutRequester.request(new LogoutEventData()));
            } catch (DisconnectedException e) {
                displayServerResponse(new Response("Disconnected!", ResponseStatus.FAILURE));
                return;
            }

            switchAppLayout(LoginMenuLayout.NAME);
        });

        exitButton.onpress(() -> {
            transceiver.disconnect();
            mustExit();
        });
    }

    private NetworkEventTransceiver transceiver = null;
    private Requester<Response, CreateNewGameEventData> createGameRequester = null;
    private Requester<Response, LogoutEventData> logoutRequester = null;

    @Override
    public void setup(String previousLayoutName) {
        if (previousLayoutName.equals(LoginMenuLayout.NAME)) {
            availableGames = new ArrayList<>();

            usernameTextBox.text(appDataProvider.getString(LoginMenuLayout.NAME, "username"));

            transceiver = (NetworkEventTransceiver) appDataProvider.get(ConnectionMenuLayout.NAME,
                "transceiver");
            createGameRequester = Response.requester(transceiver, transceiver, getLock());
            logoutRequester = Response.requester(transceiver, transceiver, getLock());

            GameHasBeenCreatedEventData.castEventReceiver(transceiver).registerListener(data -> {
                availableGames.addAll(data.getNames());

                if (availableGames.size() > 0) {
                    recyclerGamesList.populate(availableGames);

                    alternative.second();
                } else {
                    alternative.first();
                }
            });

            PlayerDisconnectedInternalEventData.castEventReceiver(transceiver).registerListener(data -> {
                transceiver = null;
                createGameRequester = null;
                logoutRequester = null;

                if (isCurrentLayout()) {
                    switchAppLayout(ConnectionMenuLayout.NAME);
                }
            });

            transceiver.broadcast(new PlayerHasJoinMenu());
        }
    }

    @Override
    public String getName() {
        return NAME;
    }
}
