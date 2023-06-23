package it.polimi.ingsw.view.tui;

import it.polimi.ingsw.controller.Response;
import it.polimi.ingsw.event.NetworkEventTransceiver;
import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.data.VoidEventData;
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

/**
 * {@link AppLayout} which displays the current players in the game lobby and allows the owner
 * to start or resume a game.
 *
 * @author Cristiano Migali
 */
public class LobbyLayout extends AppLayout {
    /**
     * Unique name of the lobby layout required to allow other AppLayouts to tell
     * the {@link it.polimi.ingsw.view.tui.terminal.drawable.app.App} to switch to this layout.
     *
     * @see AppLayout
     */
    public static final String NAME = "LOBBY";

    // Layout:

    /**
     * It is the {@link TextBox} which displays the name of the game associated with the lobby.
     */
    private final TextBox gameNameTextBox = new TextBox().unfocusable();

    /**
     * It is a {@link Drawable} which allows to display a player in the lobby list.
     * Every player is characterized by its username surrounded by a border box.
     */
    private static class PlayerInLobbyDrawable extends FixedLayoutDrawable<Drawable> {
        /**
         * It is the {@link TextBox} which displays the name of the player.
         */
        private final TextBox nameTextBox = new TextBox().hideCursor();

        /**
         * Constructor of the class.
         * It adds a border box the {@link PlayerInLobbyDrawable#nameTextBox}.
         */
        private PlayerInLobbyDrawable() {
            setLayout(new OrientedLayout(Orientation.HORIZONTAL,
                        new TextBox().text("Player: ").unfocusable().center().weight(1),
                        nameTextBox.center().weight(1)
                    ).center().crop().fixSize(new DrawableSize(6, 50))
                    .addBorderBox()
            );
        }
    }

    /**
     * {@link RecyclerDrawable} used to display the list of players in the lobby. It is populated
     * dynamically every time a player joins or leaves the lobby.
     */
    private final RecyclerDrawable<PlayerInLobbyDrawable, String> playersInLobbyListRecyclerDrawable =
        new RecyclerDrawable<>(Orientation.VERTICAL,
            PlayerInLobbyDrawable::new,
            (playerInLobbyDrawable, player) -> playerInLobbyDrawable.nameTextBox.text(player));

    /**
     * It is the {@link Button} which allows the owner to start the game.
     */
    private final Button startButton = new Button("Start game");

    /**
     * It is the {@link Button} which allows the owner to resume a game that had been stopped.
     */
    private final Button restartButton = new Button("Resume game");

    /**
     * It is the layout element associated with the start button. It allows to hide it by setting
     * its weight to 0 if the game has been stopped and hence it should be resumed and not started.
     */
    private final OrientedLayoutElement startButtonLayoutElement = startButton.center().weight(0);

    /**
     * It is the layout element associated with the restart button. It allows to hide it by setting its
     * weight to 0 if the game has never been stopped and hence it should be started and not resumed.
     */
    private final OrientedLayoutElement restartButtonLayoutElement = restartButton.center().weight(1);

    /**
     * It is the {@link Button} which allows the user to get back to {@link AvailableGamesMenuLayout}.
     */
    private final Button backButton = new Button("Back");

    /**
     * Populates the players list of the lobby and hides the start or resume button accordingly if the
     * game has been stopped or not.
     */
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

    /**
     * {@link NetworkEventTransceiver} which allows to receive events from the server.
     */
    private NetworkEventTransceiver transceiver = null;

    /**
     * It is the list of names of the players in the lobby.
     */
    private List<String> playersInLobbyNames;

    // Utilities:

    /**
     * {@link Requester} which allows the user to perform a synchronous request to join the lobby.
     * This request is performed on layout setup and ensures that the server doesn't broadcast any
     * update relevant to the lobby layout before it has set the corresponding receivers.
     */
    private Requester<Response<VoidEventData>, JoinLobbyEventData> joinLobbyRequester = null;

    /**
     * {@link Requester} which allows the owner to perform a synchronous request to the server
     * in order to start the game.
     */
    private Requester<Response<VoidEventData>, StartGameEventData> startGameRequester = null;

    /**
     * {@link Requester} which allows the owner to perform a synchronous request to the server
     * in order to resume the game.
     */
    private Requester<Response<VoidEventData>, RestartGameEventData> restartGameRequester = null;

    /**
     * {@link Requester} which allows the user to perform a synchronous request to the server
     * in order to quit from the game.
     */
    private Requester<Response<VoidEventData>, ExitLobbyEventData> exitLobbyRequester = null;

    /**
     * {@link EventReceiver} which gets notified by the server when another player joins the lobby.
     */
    private EventReceiver<PlayerHasJoinLobbyEventData> playerHasJoinLobbyReceiver = null;

    /**
     * {@link EventReceiver} which gets notified by the server when the owner has started or
     * resumed the game.
     */
    private EventReceiver<GameHasStartedEventData> gameHasStartedReceiver = null;

    /**
     * {@link EventReceiver} which gets notified by the server when a player has exited from the lobby.
     */
    private EventReceiver<PlayerHasExitLobbyEventData> playerHasExitLobbyReceiver = null;

    // Listeners:

    /**
     * {@link EventListener} invoked when the server notifies that another player has joined the lobby.
     */
    private final EventListener<PlayerHasJoinLobbyEventData> playerHasJoinLobbyListener = data -> {
        playersInLobbyNames.add(data.getUsername());

        populate();
    };

    /**
     * {@link EventListener} invoked when the server notifies that the owner has started or resumed the game.
     */
    private final EventListener<GameHasStartedEventData> gameHasStartedListener = data -> {
        switchAppLayout(GameLayout.NAME);
    };

    /**
     * {@link EventListener} invoked when the server notifies that a player has exited from the lobby.
     */
    private final EventListener<PlayerHasExitLobbyEventData> playerHasExitLobbyListener = data -> {
        playersInLobbyNames.remove(data.username());

        populate();
    };

    /**
     * Constructor of the class.
     * It initializes the layout in which all the elements are arranged and sets the required
     * {@link Button}s callbacks.
     */
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
                displayServerResponse(Response.failure("Disconnected!"));
            }
        });

        restartButton.onpress(() -> {
            try {
                displayServerResponse(restartGameRequester.request(new RestartGameEventData()));
            } catch (DisconnectedException e) {
                displayServerResponse(Response.failure("Disconnected!"));
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
                displayServerResponse(Response.failure("Disconnected!"));

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
            displayServerResponse(Response.failure("Disconnected!"));
        }
    }

    @Override
    public void beforeSwitch() {
        if (transceiver != null) {
            joinLobbyRequester.unregisterAllListeners();
            startGameRequester.unregisterAllListeners();
            exitLobbyRequester.unregisterAllListeners();
            restartGameRequester.unregisterAllListeners();

            playerHasJoinLobbyReceiver.unregisterListener(playerHasJoinLobbyListener);
            gameHasStartedReceiver.unregisterListener(gameHasStartedListener);
            playerHasExitLobbyReceiver.unregisterListener(playerHasExitLobbyListener);
        }
    }

    @Override
    public String getName() {
        return NAME;
    }
}
