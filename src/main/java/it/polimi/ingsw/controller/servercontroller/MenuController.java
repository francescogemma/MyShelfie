package it.polimi.ingsw.controller.servercontroller;

import it.polimi.ingsw.controller.Response;
import it.polimi.ingsw.controller.ResponseStatus;
import it.polimi.ingsw.controller.User;
import it.polimi.ingsw.controller.db.DBManager;
import it.polimi.ingsw.controller.db.IdentifiableNotFoundException;
import it.polimi.ingsw.event.data.EventData;
import it.polimi.ingsw.event.data.game.GameHasBeenCreatedEventData;
import it.polimi.ingsw.event.data.game.GameIsNoLongerAvailableEventData;
import it.polimi.ingsw.event.data.internal.GameOverInternalEventData;
import it.polimi.ingsw.event.receiver.EventListener;
import it.polimi.ingsw.event.transmitter.EventTransmitter;
import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.model.game.GameView;
import it.polimi.ingsw.utils.Logger;
import it.polimi.ingsw.utils.Pair;

import java.util.*;
import java.util.function.Function;

public class MenuController {
    private static final MenuController INSTANCE;
    private final List<GameController> gameControllerList = new ArrayList<>();

    private final List<EventTransmitter> authenticated;
    private final Set<User> users;

    static {
        INSTANCE = new MenuController();

        List<Game> allGame;

        try {
            allGame = DBManager.getGamesDBManager().loadAllInFolder();
        } catch (IdentifiableNotFoundException e) {
            throw new RuntimeException(e);
        }

        allGame.stream().filter(g -> !g.isOver())
                .forEach(g -> {
                    g.forceStop();
                    GameController gameController = new GameController(g);
                    INSTANCE.gameControllerList.add(gameController);
                    INSTANCE.syncToGameController(gameController);
                });
    }

    private MenuController() {
        users = new HashSet<>();
        authenticated = new ArrayList<>();
    }

    public static MenuController getInstance() {
        return INSTANCE;
    }

    public Object getLock() {
        return this;
    }

    /**
     * password is required in case of player not existing
     * */
    private User getUser (String username, String password) {
        User user;

        synchronized (this) {
            for (User u: users) {
                if (u.getName().equals(username))
                    return u;
            }

            DBManager<User> userDBManager = DBManager.getUsersDBManager();

            try {
                user = userDBManager.load(username);

                Logger.writeMessage("[%s] has been loaded".formatted(user.getName()));
            } catch (IdentifiableNotFoundException e) {
                user = new User(username, password);
                userDBManager.save(user);

                Logger.writeMessage("Created new user: [%s]".formatted(user.getName()));
            }

            users.add(user);
        }

        return user;
    }

    private synchronized void forEachAuthenticated(EventData eventData) {
        Objects.requireNonNull(eventData);

        authenticated.forEach(v -> v.broadcast(eventData));
    }

    public synchronized Response exitLobby(GameController gameController, String username) {
        Objects.requireNonNull(username);

        if (gameController == null) return Response.failure("User not in lobby");

        return removeFromLobbyIfNecessary(gameController, username, game -> game.exitLobby(username));
    }

    public synchronized Response exitGame(GameController gameController, String username) {
        Objects.requireNonNull(gameController);
        Objects.requireNonNull(username);

        return gameController.exitGame(username);
    }

    public Response startGame(GameController gameController, String username) {
        Objects.requireNonNull(username);

        if (gameController == null) {
            return new Response("User not in lobby", ResponseStatus.FAILURE);
        }

        Response response = gameController.startGame(username);

        if (response.isOk()) {
            // we need to notify the other player that this game is no longer available
            forEachAuthenticated(new GameIsNoLongerAvailableEventData(gameController.getGameView().getName()));
        }

        return response;
    }

    private synchronized Optional<GameController> getGameController(String gameName) {
        for (GameController controller: gameControllerList) {
            if (controller.gameName().equals(gameName))
                return Optional.of(controller);
        }

        return Optional.empty();
    }

    public void joinMenu(EventTransmitter eventTransmitter) {
        Objects.requireNonNull(eventTransmitter);

        synchronized (this) {
            if (authenticated.contains(eventTransmitter))
                throw new IllegalArgumentException("Already present in authenticated or notAuthenticated");
        }
    }

    public synchronized void playerHasJoinMenu (EventTransmitter transceiver, String username) {
        transceiver.broadcast(
                new GameHasBeenCreatedEventData(
                        gameControllerList.stream()
                                .filter(g -> g.isAvailableForJoin(username))
                                .map(GameController::getGameView)
                                .map(GameHasBeenCreatedEventData.AvailableGame::new)
                                .toList()
                )
        );
    }

    public Response authenticated(EventTransmitter transmitter, String username, String password) {
        Objects.requireNonNull(transmitter);
        Objects.requireNonNull(username);
        Objects.requireNonNull(password);

        User user = this.getUser(username, password);

        if (user.isConnected())
            return Response.failure("User login in other connection");

        if (!user.passwordMatches(password))
            return Response.failure("Bad credentials");

        user.setConnected(true);

        authenticated.add(transmitter);

        Logger.writeMessage("%s authenticated correctly".formatted(username));

        return Response.success("Ok");
    }

    /**
     * Let's assume that the player is not in any game.
     */
    public synchronized Response logout (EventTransmitter eventTransmitter, String username) {
        assert authenticated.contains(eventTransmitter);

        authenticated.remove(eventTransmitter);

        setConnectUser(username, false);

        return new Response("You are now logout", ResponseStatus.SUCCESS);
    }

    private synchronized void setConnectUser(String username, boolean connected) {
        for (User user : users) {
            if (user.is(username)) {
                user.setConnected(connected);
            }
        }
    }

    private Response removeFromLobbyIfNecessary(GameController gameController, String username, Function<GameController, Response> executor) {
        Objects.requireNonNull(gameController);
        Objects.requireNonNull(username);

        Response response;

        synchronized (gameController.getLock()) {
            final boolean wasFull = gameController.isFull();

            response = executor.apply(gameController);
            if (response.isOk() && wasFull){
                GameView currentView = gameController.getGameView();

                // we need to notify the player that this game is available if !game.isStarted()
                if (!currentView.isStarted()) {
                    forEachAuthenticated(new GameHasBeenCreatedEventData(currentView));
                }
            }
        }

        return response;
    }

    public synchronized void forceDisconnect(EventTransmitter transmitter, GameController gameController, String username) {
        authenticated.remove(transmitter);

        if (username == null) return;

        setConnectUser(username, false);

        if (gameController != null) {
            assert username != null;

            removeFromLobbyIfNecessary(gameController, username, game -> {
                game.disconnect(username);
                return Response.success("");
            });
        }
    }

    private final EventListener<GameOverInternalEventData> listenerGameOver = eventData -> {
        synchronized (this) {
            gameControllerList.remove(eventData.getGameController());
            eventData.getGameController().getInternalTransceiver().unregisterAllListeners();
            forEachAuthenticated(new GameIsNoLongerAvailableEventData(eventData.getGameController().gameName()));
        }
    };

    private void syncToGameController(GameController gameController) {
        Objects.requireNonNull(gameController);

        GameOverInternalEventData.castEventReceiver(gameController.getInternalTransceiver()).registerListener(this.listenerGameOver);
    }

    public Response createNewGame(String gameName, String username) {
        Objects.requireNonNull(gameName);
        Objects.requireNonNull(username);

        if (gameName.length() < 2)
            return new Response("Game name is too short", ResponseStatus.FAILURE);

        Game game = new Game(gameName, username);
        GameController controller = new GameController(game);

        synchronized (this) {
            if (getGameController(gameName).isPresent()) {
                return new Response("This game already exists", ResponseStatus.FAILURE);
            }

            syncToGameController(controller);

            this.gameControllerList.add(controller);

            forEachAuthenticated(new GameHasBeenCreatedEventData(game));

            return new Response("Game: [%s] has been created".formatted(gameName), ResponseStatus.SUCCESS);
        }
    }

    public synchronized Pair<Response, GameController> joinLobby(EventTransmitter transmitter, String username, String gameName) {
        Optional<GameController> controller = this.getGameController(gameName);

        if (controller.isPresent()) {
            synchronized (controller.get().getLock()) {
                Response response = controller.get().joinLobby(transmitter, username);

                if (response.isOk()) {
                    if (controller.get().isFull())
                        forEachAuthenticated(new GameIsNoLongerAvailableEventData(controller.get().getGameView().getName()));
                    return Pair.of(response, controller.get());
                } else {
                    return Pair.of(response, null);
                }
            }
        }

        return Pair.of(new Response("There is no game with this name...", ResponseStatus.FAILURE), null);
    }

    public Pair<Response, GameController> joinGame(EventTransmitter eventTransmitter, String username, String gameName) {
        Optional<GameController> controller = this.getGameController(gameName);

        if (controller.isPresent()) {
            Response response = controller.get().rejoinGame(username, eventTransmitter);

            if (response.isOk()) {
                return Pair.of(response, controller.get());
            } else {
                return Pair.of(response, null);
            }
        }

        return Pair.of(new Response("There is no game with this name...", ResponseStatus.FAILURE), null);
    }
}
