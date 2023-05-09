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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class MenuController {
    private static final MenuController INSTANCE;
    private final List<GameController> gameControllerList = new ArrayList<>();

    private final List<EventTransmitter> authenticated;
    private final List<EventTransmitter> notAuthenticated;
    private final List<User> users;

    static {
        INSTANCE = new MenuController();

        List<Game> allGame;

        try {
            allGame = DBManager.getGamesDBManager().loadAllInFolder();
        } catch (IdentifiableNotFoundException e) {
            throw new RuntimeException(e);
        }

        for (Game game: allGame) {
            if (!game.isOver()) {
                game.forceStop();
                GameController gameController = new GameController(game);
                GameOverInternalEventData.castEventReceiver(gameController.getInternalTransceiver()).registerListener(INSTANCE.listenerGameOver);
                INSTANCE.gameControllerList.add(gameController);
            }
        }
    }

    private MenuController() {
        notAuthenticated = new ArrayList<>();
        users = new ArrayList<>();
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

                Logger.writeMessage("User has been loaded" + user.getName());
            } catch (IdentifiableNotFoundException e) {
                user = new User(username, password);
                userDBManager.save(user);

                Logger.writeMessage("Created new user: " + user.getName());
            }

            users.add(user);
        }

        return user;
    }

    private synchronized boolean authenticate (EventTransmitter transmitter, String username, String password) {
        User user = this.getUser(username, password);

        if (user.passwordMatches(password)) {
            if (user.isConnected())
                return false;

            user.setConnected(true);

            notAuthenticated.remove(transmitter);
            authenticated.add(transmitter);

            Logger.writeMessage(user.getName() + " authenticated correctly");

            return true;
        } else {
            return false;
        }
    }

    private void forEachInMenu(EventData eventData) {
        Objects.requireNonNull(eventData);

        authenticated
                .forEach(v -> v.broadcast(eventData));
    }

    public synchronized Response exitLobby(GameController gameController, String username) {
        Objects.requireNonNull(username);
        Response response;

        if (gameController == null) return Response.failure("User not in lobby");

        synchronized (gameController.getLock()) {
            boolean wasFull = gameController.isFull();
            response = gameController.exitLobby(username);

            if (response.isOk() && wasFull && !gameController.getGameView().isStarted()) {
                // we need to notify the player in the menu that a new game is available
                GameHasBeenCreatedEventData event = new GameHasBeenCreatedEventData(
                    gameController.getGameView()
                );

                forEachInMenu(event);
            }
        }

        return response;
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
            forEachInMenu(new GameIsNoLongerAvailableEventData(gameController.getGameView().getName()));
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
        if (eventTransmitter == null)
            throw new NullPointerException();

        synchronized (this) {
            if (authenticated.contains(eventTransmitter))
                throw new IllegalArgumentException("Already present in authenticated");
            if (notAuthenticated.contains(eventTransmitter))
                throw new IllegalArgumentException("Already present in notAuthenticated");
            notAuthenticated.add(eventTransmitter);
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
        return this.authenticate(transmitter, username, password) ?
            new Response("You are log in", ResponseStatus.SUCCESS) :
            new Response("Bad credentials", ResponseStatus.FAILURE);
    }

    /**
     * Let's assume that the player is not in any game.
     */
    public synchronized Response logout (EventTransmitter eventTransmitter, String username) {
        assert authenticated.contains(eventTransmitter);
        assert !notAuthenticated.contains(eventTransmitter);

        authenticated.remove(eventTransmitter);
        notAuthenticated.add(eventTransmitter);

        for (User user : users) {
            if (user.getName().equals(username))
                user.setConnected(false);
        }

        return new Response("You are now logout", ResponseStatus.SUCCESS);
    }

    public synchronized void forceDisconnect(EventTransmitter transmitter, GameController gameController, String username) {
        authenticated.remove(transmitter);
        notAuthenticated.remove(transmitter);

        for (User user : users) {
            if (user.getName().equals(username))
                user.setConnected(false);
        }

        if (gameController != null) {
            assert username != null;

            synchronized (gameController.getLock()) {
                gameController.disconnect(username);
                GameView currentView = gameController.getGameView();

                // we need to notify the player that this game is available if !game.isStarted()
                if (!currentView.isStarted()) {
                    forEachInMenu(new GameHasBeenCreatedEventData(currentView));
                }
            }
        }
    }

    private final EventListener<GameOverInternalEventData> listenerGameOver = eventData -> {
        synchronized (this) {
            gameControllerList.remove(eventData.getGameController());
            eventData.getGameController().getInternalTransceiver().unregisterAllListeners();
            forEachInMenu(new GameIsNoLongerAvailableEventData(eventData.getGameController().gameName()));
        }
    };

    public Response createNewGame(String gameName, String username) {
        Objects.requireNonNull(gameName);
        Objects.requireNonNull(username);

        if (gameName.length() < 2)
            return new Response("Game name is too short", ResponseStatus.FAILURE);

        Game game = new Game(gameName, username);
        GameController controller = new GameController(game);

        synchronized (this) {
            GameOverInternalEventData.castEventReceiver(controller.getInternalTransceiver()).registerListener(this.listenerGameOver);

            Optional<GameController> gameController = this.getGameController(gameName);
            if (gameController.isPresent()) {
                return new Response("This game already exists", ResponseStatus.FAILURE);
            }

            this.gameControllerList.add(controller);

            forEachInMenu(new GameHasBeenCreatedEventData(game));

            return new Response("Game: [%s] has been created".formatted(gameName), ResponseStatus.SUCCESS);
        }
    }

    public Pair<Response, GameController> joinLobby(EventTransmitter transmitter, String username, String gameName) {
        Optional<GameController> controller = this.getGameController(gameName);

        if (controller.isPresent()) {
            synchronized (controller.get().getLock()) {
                Response response = controller.get().joinLobby(transmitter, username);

                if (response.isOk()) {
                    if (controller.get().isFull())
                        forEachInMenu(new GameIsNoLongerAvailableEventData(controller.get().getGameView().getName()));
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
