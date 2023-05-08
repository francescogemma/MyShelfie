package it.polimi.ingsw.view.tui;

import it.polimi.ingsw.controller.Response;
import it.polimi.ingsw.controller.ResponseStatus;
import it.polimi.ingsw.event.NetworkEventTransceiver;
import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.data.client.CreateNewGameEventData;
import it.polimi.ingsw.event.data.client.LogoutEventData;
import it.polimi.ingsw.event.data.client.PlayerHasJoinMenu;
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

public class AvailableGamesMenuLayout extends AppLayout {
    public static final String NAME = "AVAILABLE_GAMES_MENU";

    // Layout:
    private final TextBox usernameTextBox = new TextBox().unfocusable();
    private final Drawable usernameDisplayDrawable = new OrientedLayout(Orientation.HORIZONTAL,
        new TextBox().text("Logged in as: ").unfocusable().weight(1),
        usernameTextBox.color(Color.RED).weight(1)
    ).center().crop().fixSize(new DrawableSize(3, 25));
    private final Drawable noAvailableGamesTextBox = new TextBox().text("There aren't available games!")
        .unfocusable().center();

    private static class AvailableGameDrawable extends FixedLayoutDrawable<Drawable> {
        private final TextBox nameTextBox = new TextBox();
        private final Button joinGameButton = new Button("Join game");
        private final Button joinLobbyButton = new Button("Join lobby");
        private final FullyResizableOrientedLayoutElement joinGameButtonLayoutElement =
            joinGameButton.center().crop().weight(0);
        private final FullyResizableOrientedLayoutElement joinLobbyButtonLayoutElement =
            joinLobbyButton.center().crop().weight(0);

        private AvailableGameDrawable() {
            setLayout(new FullyResizableOrientedLayout(Orientation.HORIZONTAL,
                nameTextBox.unfocusable().center().crop().weight(1),
                joinGameButtonLayoutElement,
                joinLobbyButtonLayoutElement
            ).fixSize(new DrawableSize(5, 80)).addBorderBox());
        }
    }

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
    private final AlternativeDrawable alternativeDrawable = new AlternativeDrawable(noAvailableGamesTextBox,
        gamesListRecyclerDrawable.center().scrollable());

    private final ValueMenuEntry<String> gameNameEntry = new ValueMenuEntry<>("New game's name", new TextBox());
    private final Button createNewGameButton = new Button("Create new game");

    private final Button backToLoginButton = new Button("Back to login");
    private final Button exitButton = new Button("Exit");

    // Data:
    private NetworkEventTransceiver transceiver = null;
    private List<GameHasBeenCreatedEventData.AvailableGame> availableGames;
    private String selectedGameName;
    private String selectedGameOwner;
    private boolean isSelectedGameStopped;

    // Utilities:
    private Requester<Response, CreateNewGameEventData> createGameRequester = null;
    private Requester<Response, LogoutEventData> logoutRequester = null;

    private EventReceiver<GameHasBeenCreatedEventData> gameHasBeenCreatedReceiver = null;
    private EventReceiver<GameIsNoLongerAvailableEventData> gameIsNoLongerAvailableReceiver = null;

    // Listeners:
    private final EventListener<GameHasBeenCreatedEventData> gameHasBeenCreatedListener = data -> {
        availableGames.addAll(data.getNames());

        if (availableGames.isEmpty()) {
            alternativeDrawable.first();
        } else {
            gamesListRecyclerDrawable.populate(availableGames);

            alternativeDrawable.second();
        }
    };

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
                displayServerResponse(new Response("Disconnected!", ResponseStatus.FAILURE));
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

        transceiver.broadcast(new PlayerHasJoinMenu());
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
