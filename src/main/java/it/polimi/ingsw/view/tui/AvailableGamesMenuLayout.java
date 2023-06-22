package it.polimi.ingsw.view.tui;

import it.polimi.ingsw.controller.Response;
import it.polimi.ingsw.event.NetworkEventTransceiver;
import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.data.VoidEventData;
import it.polimi.ingsw.event.data.client.CreateNewGameEventData;
import it.polimi.ingsw.event.data.client.LogoutEventData;
import it.polimi.ingsw.event.data.client.PlayerHasJoinMenuEventData;
import it.polimi.ingsw.event.data.game.GameHasBeenCreatedEventData;
import it.polimi.ingsw.event.data.game.GameIsNoLongerAvailableEventData;
import it.polimi.ingsw.event.data.internal.PlayerDisconnectedInternalEventData;
import it.polimi.ingsw.event.receiver.EventListener;
import it.polimi.ingsw.event.receiver.EventReceiver;
import it.polimi.ingsw.networking.DisconnectedException;
import it.polimi.ingsw.view.tui.terminal.drawable.*;
import it.polimi.ingsw.view.tui.terminal.drawable.app.AppLayout;
import it.polimi.ingsw.view.tui.terminal.drawable.app.AppLayoutData;
import it.polimi.ingsw.view.tui.terminal.drawable.menu.Button;
import it.polimi.ingsw.view.tui.terminal.drawable.menu.value.TextBox;
import it.polimi.ingsw.view.tui.terminal.drawable.menu.value.ValueMenuEntry;
import it.polimi.ingsw.view.tui.terminal.drawable.orientedlayout.FullyResizableOrientedLayout;
import it.polimi.ingsw.view.tui.terminal.drawable.orientedlayout.FullyResizableOrientedLayoutElement;
import it.polimi.ingsw.view.tui.terminal.drawable.orientedlayout.OrientedLayout;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.Color;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.PrimitiveSymbol;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * {@link AppLayout} to display the game menu where the users can see all the available games that they can join
 * and where they can create new games.
 *
 * @author Cristiano Migali
 */
public class AvailableGamesMenuLayout extends AppLayout {
    /**
     * Unique name of the available games menu layout.
     */
    public static final String NAME = "AVAILABLE_GAMES_MENU";

    // Layout:

    /**
     * {@link TextBox} which displays the username of the user in the upper left corner of the layout.
     */
    private final TextBox usernameTextBox = new TextBox().unfocusable();

    /**
     * Full layout built around the {@link AvailableGamesMenuLayout#usernameTextBox} to decorate it.
     */
    private final Drawable usernameDisplayDrawable = new OrientedLayout(Orientation.HORIZONTAL,
        new TextBox().text("Logged in as: ").unfocusable().weight(1),
        usernameTextBox.color(Color.RED).weight(1)
    ).center().crop().fixSize(new DrawableSize(3, 35));

    /**
     * {@link TextBox} displayed at the center of the layout when there is no available game.
     */
    private final Drawable noAvailableGamesTextBox = new TextBox().text("There aren't available games!")
        .unfocusable().center();

    /**
     * Drawable representing an available game entry in the list. In particular it displays the name of the game
     * and the buttons to join the lobby (or directly join the game).
     */
    private static class AvailableGameDrawable extends FixedLayoutDrawable<Drawable> {
        /**
         * {@link TextBox} displaying the name of the game.
         */
        private final TextBox nameTextBox = new TextBox();

        /**
         * {@link Button} which when pressed allows to directly join the game.
         */
        private final Button joinGameButton = new Button("Join game");

        /**
         * {@link Button} which when pressed allows to join the game lobby.
         */
        private final Button joinLobbyButton = new Button("Join lobby");

        /**
         * Layout element associated to the join game button, it allows to set the weight of the button to 0, hiding it.
         */
        private final FullyResizableOrientedLayoutElement joinGameButtonLayoutElement =
            joinGameButton.center().crop().weight(0);

        /**
         * Layout element associated to the join lobby button, it allows to set the weight of the button to 0,
         * hiding it.
         */
        private final FullyResizableOrientedLayoutElement joinLobbyButtonLayoutElement =
            joinLobbyButton.center().crop().weight(0);

        /**
         * Constructor of the class. It initializes the layout of game entry.
         */
        private AvailableGameDrawable() {
            setLayout(new FullyResizableOrientedLayout(Orientation.HORIZONTAL,
                nameTextBox.unfocusable().center().crop().weight(1),
                joinGameButtonLayoutElement,
                joinLobbyButtonLayoutElement
            ).fixSize(new DrawableSize(5, 80)).addBorderBox());
        }
    }

    /**
     * {@link RecyclerDrawable} for the list of available games which is populated dynamically.
     */
    private final RecyclerDrawable<AvailableGameDrawable, GameHasBeenCreatedEventData.AvailableGame>
        gamesListRecyclerDrawable = new RecyclerDrawable<>(Orientation.VERTICAL,
            AvailableGameDrawable::new,
            (availableGameDrawable, availableGame) -> {
                availableGameDrawable.nameTextBox.text(availableGame.name());

                availableGameDrawable.joinGameButton.onpress(() -> {
                    selectedGameName = availableGame.name();
                    selectedGameOwner = availableGame.owner();
                    isSelectedGameStopped = availableGame.isStopped();

                    switchAppLayout(GameLayout.NAME);
                });

                availableGameDrawable.joinLobbyButton.onpress(() -> {
                    selectedGameName = availableGame.name();
                    selectedGameOwner = availableGame.owner();
                    isSelectedGameStopped = availableGame.isStopped();

                    switchAppLayout(LobbyLayout.NAME);
                });

                availableGameDrawable.joinGameButtonLayoutElement.setWeight(0);
                availableGameDrawable.joinLobbyButtonLayoutElement.setWeight(0);

                if (availableGame.isStarted() && !availableGame.isStopped()) {
                    availableGameDrawable.joinGameButtonLayoutElement.setWeight(1);
                } else {
                    availableGameDrawable.joinLobbyButtonLayoutElement.setWeight(1);
                }
            });

    /**
     * {@link AlternativeDrawable} which displays the list of available games or a text box if there is no available
     * game.
     */
    private final AlternativeDrawable alternativeDrawable = new AlternativeDrawable(noAvailableGamesTextBox,
        gamesListRecyclerDrawable.center().scrollable());

    /**
     * {@link ValueMenuEntry} which allows the users to specify the name of the game that they want to create.
     */
    private final ValueMenuEntry<String> gameNameEntry = new ValueMenuEntry<>("New game's name", new TextBox());

    /**
     * {@link Button} which allows the users to create a new game.
     */
    private final Button createNewGameButton = new Button("Create new game");

    /**
     * {@link Button} which allows the users to get back to the login menu.
     */
    private final Button backToLoginButton = new Button("Back to login");

    /**
     * {@link Button} which allows the users to quit from the game.
     */
    private final Button exitButton = new Button("Exit");

    // Data:
    /**
     * {@link NetworkEventTransceiver} which allows to receive events from the server.
     */
    private NetworkEventTransceiver transceiver = null;

    /**
     * List of currently available games, constantly updated by the server.
     */
    private List<GameHasBeenCreatedEventData.AvailableGame> availableGames;

    /**
     * Name of the game that the user has joined.
     */
    private String selectedGameName;

    /**
     * Name of the owner of the game that the user has joined.
     */
    private String selectedGameOwner;

    /**
     * It is true iff the game that the user has selected has been stopped.
     */
    private boolean isSelectedGameStopped;

    // Utilities:
    /**
     * {@link Requester} which allows to perform a synchronous request to create a new game with the specified name.
     */
    private Requester<Response<VoidEventData>, CreateNewGameEventData> createGameRequester = null;

    /**
     * {@link Requester} which allows to perform a synchronous request to logout.
     */
    private Requester<Response<VoidEventData>, LogoutEventData> logoutRequester = null;

    /**
     * {@link EventReceiver} which gets notified by the server when new games have been created or when
     * the user joins the menu for the first time; in this case the user receives all the currently available game.
     */
    private EventReceiver<GameHasBeenCreatedEventData> gameHasBeenCreatedReceiver = null;

    /**
     * {@link EventReceiver} which gets notified by the server when a game in the menu is no longer available (for example
     * when the game has been started by other players).
     */
    private EventReceiver<GameIsNoLongerAvailableEventData> gameIsNoLongerAvailableReceiver = null;

    // Listeners:
    /**
     * {@link EventListener} invoked when the server notifies that a new game has been created.
     * It adds the game to the displayed list.
     */
    private final EventListener<GameHasBeenCreatedEventData> gameHasBeenCreatedListener = data -> {
        availableGames.addAll(data.getNames());

        if (availableGames.isEmpty()) {
            alternativeDrawable.first();
        } else {
            gamesListRecyclerDrawable.populate(availableGames);

            alternativeDrawable.second();
        }
    };

    /**
     * {@link EventListener} invoked when the server notifies that a game is no longer available.
     * The game is removed from the list.
     */
    private final EventListener<GameIsNoLongerAvailableEventData> gameIsNoLongerAvailableListener = data -> {
        for (int i = 0; i < availableGames.size(); i++) {
            if (availableGames.get(i).name().equals(data.gameName())) {
                availableGames.remove(i);
                break;
            }
        }

        if (availableGames.isEmpty()) {
            alternativeDrawable.first();
        } else {
            gamesListRecyclerDrawable.populate(availableGames);

            alternativeDrawable.second();
        }
    };

    /**
     * Constructor of the class.
     * It initializes the layout.
     */
    public AvailableGamesMenuLayout() {
        setLayout(new OrientedLayout(Orientation.VERTICAL,
            usernameDisplayDrawable.alignUpLeft().weight(1),
            alternativeDrawable.center().weight(7),
            new OrientedLayout(Orientation.HORIZONTAL,
                new Fill(PrimitiveSymbol.EMPTY).weight(1),
                gameNameEntry.center().weight(1),
                createNewGameButton.center().weight(1),
                new Fill(PrimitiveSymbol.EMPTY).weight(1)
                ).weight(2),
            new OrientedLayout(Orientation.HORIZONTAL,
                new Fill(PrimitiveSymbol.EMPTY).weight(2),
                backToLoginButton.center().weight(1),
                exitButton.center().weight(1),
                new Fill(PrimitiveSymbol.EMPTY).weight(2)
            ).center().weight(2)
        ).center().crop());

        setData(new AppLayoutData(Map.of(
            "selectedgame", () -> selectedGameName,
            "selectedgameowner", () -> selectedGameOwner,
            "isselectedgamestopped", () -> isSelectedGameStopped
        )));

        createNewGameButton.onpress(() -> {
            try {
                displayServerResponse(createGameRequester.request(new CreateNewGameEventData(gameNameEntry.getValue())));
            } catch (DisconnectedException e) {
                displayServerResponse(Response.failure("Disconnected!"));
            }
        });

        backToLoginButton.onpress(() -> {
            try {
                Response response = logoutRequester.request(new LogoutEventData());

                displayServerResponse(response);

                if (!response.isOk()) {
                    return;
                }
            } catch (DisconnectedException e) {
                displayServerResponse(Response.failure("Disconnected!"));

                return;
            }

            switchAppLayout(LoginMenuLayout.NAME);
        });

        exitButton.onpress(() -> {
            transceiver.disconnect();
            mustExit();
        });
    }

    @Override
    public void setup(String previousLayoutName) {
        if (previousLayoutName.equals(ConnectionMenuLayout.NAME)) {
            throw new IllegalStateException("You can't switch to available games menu from connection menu");
        }

        usernameTextBox.text(appDataProvider.getString(LoginMenuLayout.NAME, "username"));

        availableGames = new ArrayList<>();

        alternativeDrawable.first();

        if (transceiver == null) {
            transceiver = (NetworkEventTransceiver) appDataProvider.get(ConnectionMenuLayout.NAME,
            "transceiver");

            createGameRequester = Response.requester(transceiver, transceiver, getLock());
            logoutRequester = Response.requester(transceiver, transceiver, getLock());

            gameHasBeenCreatedReceiver = GameHasBeenCreatedEventData.castEventReceiver(transceiver);
            gameIsNoLongerAvailableReceiver = GameIsNoLongerAvailableEventData.castEventReceiver(transceiver);

            PlayerDisconnectedInternalEventData.castEventReceiver(transceiver).registerListener(data -> {
                transceiver = null;

                createGameRequester = null;
                logoutRequester = null;

                gameHasBeenCreatedReceiver = null;
                gameIsNoLongerAvailableReceiver = null;

                if (isCurrentLayout()) {
                    switchAppLayout(ConnectionMenuLayout.NAME);
                }
            });
        }

        createGameRequester.registerAllListeners();
        logoutRequester.registerAllListeners();

        gameHasBeenCreatedReceiver.registerListener(gameHasBeenCreatedListener);
        gameIsNoLongerAvailableReceiver.registerListener(gameIsNoLongerAvailableListener);

        transceiver.broadcast(new PlayerHasJoinMenuEventData());
    }

    @Override
    public void beforeSwitch() {
        if (transceiver != null) {
            createGameRequester.unregisterAllListeners();
            logoutRequester.unregisterAllListeners();

            gameHasBeenCreatedReceiver.unregisterListener(gameHasBeenCreatedListener);
            gameIsNoLongerAvailableReceiver.unregisterListener(gameIsNoLongerAvailableListener);
        }
    }

    @Override
    public String getName() {
        return NAME;
    }
}
