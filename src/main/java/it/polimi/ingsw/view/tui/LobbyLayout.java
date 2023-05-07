package it.polimi.ingsw.view.tui;

import it.polimi.ingsw.controller.Response;
import it.polimi.ingsw.controller.ResponseStatus;
import it.polimi.ingsw.event.NetworkEventTransceiver;
import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.data.client.*;
import it.polimi.ingsw.event.data.game.*;
import it.polimi.ingsw.event.data.internal.PlayerDisconnectedInternalEventData;
import it.polimi.ingsw.event.receiver.EventListener;
import it.polimi.ingsw.event.receiver.EventReceiver;
import it.polimi.ingsw.networking.DisconnectedException;
import it.polimi.ingsw.view.tui.terminal.drawable.*;
import it.polimi.ingsw.view.tui.terminal.drawable.app.AppLayout;
import it.polimi.ingsw.view.tui.terminal.drawable.app.AppLayoutData;
import it.polimi.ingsw.view.tui.terminal.drawable.menu.Button;
import it.polimi.ingsw.view.tui.terminal.drawable.menu.value.TextBox;
import it.polimi.ingsw.view.tui.terminal.drawable.orientedlayout.OrientedLayout;
import it.polimi.ingsw.view.tui.terminal.drawable.orientedlayout.OrientedLayoutElement;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.PrimitiveSymbol;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LobbyLayout extends AppLayout {
    public static final String NAME = "LOBBY";

    // Layout:
    private final TextBox gameNameTextBox = new TextBox().unfocusable();

    private static class PlayerInLobbyDrawable extends FixedLayoutDrawable<Drawable> {
        private final TextBox nameTextBox = new TextBox().hideCursor();

        private PlayerInLobbyDrawable() {
            setLayout(new OrientedLayout(Orientation.HORIZONTAL,
                        new TextBox().text("Player: ").unfocusable().center().weight(1),
                        nameTextBox.center().weight(1)
                    ).center().crop().fixSize(new DrawableSize(6, 50))
                    .addBorderBox()
            );
        }
    }

    private final RecyclerDrawable<PlayerInLobbyDrawable, String> playersInLobbyListRecyclerDrawable =
        new RecyclerDrawable<>(Orientation.VERTICAL,
            PlayerInLobbyDrawable::new,
            (playerInLobbyDrawable, player) -> playerInLobbyDrawable.nameTextBox.text(player));

    private final Button startButton = new Button("Start game");
    private final Button restartButton = new Button("Restart game");
    private final OrientedLayoutElement startButtonLayoutElement = startButton.center().weight(0);
    private final OrientedLayoutElement restartButtonLayoutElement = restartButton.center().weight(1);

    private final Button backButton = new Button("Back");

    private void populate() {
        playersInLobbyListRecyclerDrawable.populate(playersInLobbyNames);

        boolean isOwner = appDataProvider.getString(AvailableGamesMenuLayout.NAME, "selectedgameowner")
            .equals(appDataProvider.getString(LoginMenuLayout.NAME, "username"));

        startButtonLayoutElement.setWeight(0);
        restartButtonLayoutElement.setWeight(0);

        if (isOwner) {
            if (appDataProvider.getBoolean(AvailableGamesMenuLayout.NAME, "isselectedgamestopped")) {
                restartButtonLayoutElement.setWeight(1);
                restartButton.focusable(playersInLobbyNames.size() >= 2);
            } else {
                startButtonLayoutElement.setWeight(1);
                startButton.focusable(playersInLobbyNames.size() >= 2);
            }
        }
    }

    // Data:
    private NetworkEventTransceiver transceiver = null;
    private List<String> playersInLobbyNames;

    // Utilities:
    private Requester<Response, JoinLobbyEventData> joinLobbyRequester = null;
    private Requester<Response, StartGameEventData> startGameRequester = null;
    private Requester<Response, RestartGameEventData> restartGameRequester = null;
    private Requester<Response, ExitLobbyEventData> exitLobbyRequester = null;

    private EventReceiver<PlayerHasJoinLobbyEventData> playerHasJoinLobbyReceiver = null;
    private EventReceiver<GameHasStartedEventData> gameHasStartedReceiver = null;
    private EventReceiver<PlayerHasExitLobbyEventData> playerHasExitLobbyReceiver = null;

    // Listeners:
    private final EventListener<PlayerHasJoinLobbyEventData> playerHasJoinLobbyListener = data -> {
        playersInLobbyNames.add(data.getUsername());

        populate();
    };

    private final EventListener<GameHasStartedEventData> gameHasStartedListener = data -> {
        switchAppLayout(GameLayout.NAME);
    };

    private final EventListener<PlayerHasExitLobbyEventData> playerHasExitLobbyListener = data -> {
        playersInLobbyNames.remove(data.username());

        populate();
    };

    public LobbyLayout() {
        setLayout(new OrientedLayout(Orientation.VERTICAL,
            new OrientedLayout(Orientation.HORIZONTAL,
                new TextBox().text("Game: ").unfocusable().weight(1),
                gameNameTextBox.weight(1)
            ).center().crop().fixSize(new DrawableSize(3, 40)).center().weight(1),
            playersInLobbyListRecyclerDrawable.center().scrollable().weight(4),
            new OrientedLayout(Orientation.HORIZONTAL,
                new Fill(PrimitiveSymbol.EMPTY).weight(1),
                startButtonLayoutElement,
                restartButtonLayoutElement,
                backButton.center().weight(1),
                new Fill(PrimitiveSymbol.EMPTY).weight(1)
            ).weight(1)
        ).center().crop());

        setData(new AppLayoutData(
            Map.of(
            )
        ));

        startButton.onpress(() -> {
            try {
                displayServerResponse(startGameRequester.request(new StartGameEventData()));
            } catch (DisconnectedException e) {
                displayServerResponse(new Response("Disconnected!", ResponseStatus.FAILURE));
            }
        });

        restartButton.onpress(() -> {
            try {
                displayServerResponse(restartGameRequester.request(new RestartGameEventData()));
            } catch (DisconnectedException e) {
                displayServerResponse(new Response("Disconnected!", ResponseStatus.FAILURE));
            }
        });

        backButton.onpress(() -> {
            try {
                Response response = exitLobbyRequester.request(new ExitLobbyEventData());
                displayServerResponse(response);

                if (!response.isOk()) {
                    return;
                }
            } catch (DisconnectedException e) {
                displayServerResponse(new Response("Disconnected!", ResponseStatus.FAILURE));

                return;
            }

            switchAppLayout(AvailableGamesMenuLayout.NAME);
        });
    }

    @Override
    public void setup(String previousLayoutName) {
        if (!previousLayoutName.equals(AvailableGamesMenuLayout.NAME)) {
            throw new IllegalStateException("You can reach LobbyLayout only from AvailableGamesMenuLayout");
        }

        playersInLobbyNames = new ArrayList<>();

        gameNameTextBox.text(appDataProvider.getString(AvailableGamesMenuLayout.NAME, "selectedgame"));

        populate();

        if (transceiver == null) {
            transceiver = (NetworkEventTransceiver) appDataProvider.get(ConnectionMenuLayout.NAME,
                "transceiver");

            joinLobbyRequester = Response.requester(transceiver, transceiver, getLock());
            startGameRequester = Response.requester(transceiver, transceiver, getLock());
            exitLobbyRequester = Response.requester(transceiver, transceiver, getLock());
            restartGameRequester = Response.requester(transceiver, transceiver, getLock());

            playerHasJoinLobbyReceiver = PlayerHasJoinLobbyEventData.castEventReceiver(transceiver);
            gameHasStartedReceiver = GameHasStartedEventData.castEventReceiver(transceiver);
            playerHasExitLobbyReceiver = PlayerHasExitLobbyEventData.castEventReceiver(transceiver);

            PlayerDisconnectedInternalEventData.castEventReceiver(transceiver).registerListener(data -> {
                transceiver = null;

                joinLobbyRequester = null;
                startGameRequester = null;
                exitLobbyRequester = null;
                restartGameRequester = null;

                playerHasJoinLobbyReceiver = null;
                gameHasStartedReceiver = null;
                playerHasExitLobbyReceiver = null;

                if (isCurrentLayout()) {
                    switchAppLayout(ConnectionMenuLayout.NAME);
                }
            });
        }

        joinLobbyRequester.registerAllListeners();
        startGameRequester.registerAllListeners();
        exitLobbyRequester.registerAllListeners();
        restartGameRequester.registerAllListeners();

        playerHasJoinLobbyReceiver.registerListener(playerHasJoinLobbyListener);
        gameHasStartedReceiver.registerListener(gameHasStartedListener);
        playerHasExitLobbyReceiver.registerListener(playerHasExitLobbyListener);

        try {
            Response response = joinLobbyRequester.request(
                new JoinLobbyEventData(appDataProvider.getString(AvailableGamesMenuLayout.NAME,
                    "selectedgame")
                )
            );

            displayServerResponse(response);

            if (!response.isOk()) {
                switchAppLayout(AvailableGamesMenuLayout.NAME);
            }
        } catch (DisconnectedException e) {
            displayServerResponse(new Response("Disconnected!", ResponseStatus.FAILURE));
        }
    }

    @Override
    public void beforeSwitch() {
        joinLobbyRequester.unregisterAllListeners();
        startGameRequester.unregisterAllListeners();
        exitLobbyRequester.unregisterAllListeners();
        restartGameRequester.unregisterAllListeners();

        playerHasJoinLobbyReceiver.unregisterListener(playerHasJoinLobbyListener);
        gameHasStartedReceiver.unregisterListener(gameHasStartedListener);
        playerHasExitLobbyReceiver.unregisterListener(playerHasExitLobbyListener);
    }

    @Override
    public String getName() {
        return NAME;
    }
}
