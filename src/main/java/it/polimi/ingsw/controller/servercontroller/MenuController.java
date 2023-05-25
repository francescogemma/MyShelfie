package it.polimi.ingsw.controller.servercontroller;

import it.polimi.ingsw.controller.Response;
import it.polimi.ingsw.controller.ResponseStatus;
import it.polimi.ingsw.controller.User;
import it.polimi.ingsw.controller.db.DBManager;
import it.polimi.ingsw.controller.db.IdentifiableNotFoundException;
import it.polimi.ingsw.event.data.EventData;
import it.polimi.ingsw.event.data.VoidEventData;
import it.polimi.ingsw.event.data.client.UsernameEventData;
import it.polimi.ingsw.event.data.game.GameHasBeenCreatedEventData;
import it.polimi.ingsw.event.data.game.GameIsNoLongerAvailableEventData;
import it.polimi.ingsw.event.data.internal.GameHasBeenStoppedInternalEventData;
import it.polimi.ingsw.event.data.internal.GameOverInternalEventData;
import it.polimi.ingsw.event.receiver.EventListener;
import it.polimi.ingsw.event.transmitter.EventTransmitter;
import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.model.game.GameView;
import it.polimi.ingsw.utils.Logger;
import it.polimi.ingsw.utils.Pair;

import java.util.*;
import java.util.function.Function;

/**
 * Class for the management of ongoing games and accounts.
 *
 * @see EventTransmitter
 * @see it.polimi.ingsw.controller.VirtualView
 * @author Giacomo Groppi
 */
public class MenuController {
    /**
     * Private instance of MenuController.
     */
    private static final MenuController INSTANCE;

    /**
     * List of all ongoing games.
     */
    private final List<GameController> gameControllerList = new ArrayList<>();

    /**
     * List of all {@link EventTransmitter eventTransmitters} of all
     * authenticated users.
     */
    private final List<Pair<String, EventTransmitter>> authenticated = new ArrayList<>();

    /**
     * List of all users who have connected to the server at least once
     * or who have tried to log in without success.
     */
    private final Set<User> users;

    static {
        INSTANCE = new MenuController();

        List<Game> allGame;

        try {
            allGame = DBManager.getGamesDBManager().loadAllInFolder();
        } catch (IdentifiableNotFoundException e) {
            throw new RuntimeException(e);
        }

        allGame.stream()
                .filter(g -> !g.isOver())
                .forEach(g -> {
                    g.forceStop();
                    GameController gameController = new GameController(g);
                    INSTANCE.gameControllerList.add(gameController);
                    INSTANCE.syncToGameController(gameController);
                });
    }

    private MenuController() {
        users = new HashSet<>();
    }

    /**
     * This function returns the instance of the {@link MenuController MenuController} object.
     * @return MenuController instance
     */
    public static MenuController getInstance() {
        return INSTANCE;
    }

    /**
     * This method returns the user with the same username as "username".
     * If the user doesn't exist, the user is created and saved to disk with the provided password.
     *
     * @param username the username of the user to return
     * @param password the password of the user.
     */
    private User getUser (String username, String password) {
        User user;

        synchronized (this) {
            for (User u: users) {
                if (u.getName().equalsIgnoreCase(username))
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

    /**
     * The function iterates over all authenticated users and broadcasts the given event.
     *
     * @param eventData The event to broadcast.
     * @see EventTransmitter
     */
    private synchronized void forEachAuthenticated(EventData eventData) {
        Objects.requireNonNull(eventData);

        authenticated.forEach(v -> v.getValue().broadcast(eventData));
    }

    /**
     * This method is used to disconnect a user from the lobby. If the lobby is full, after removing the user, an event
     * will be broadcast to inform everyone that a new game has been created.
     *
     * @param gameController The possible game in which the user is located
     * @param username The username of the user who wants to leave the lobby
     *
     * @throws NullPointerException iff username is null
     * @return SUCCESS iff
     * <ul>
     *     <li>gameController has successfully removed the user from the lobby</li>
     * </ul>
     * FAILURE otherwise
     * 
     * @see GameController#exitLobby(String) 
     */
    public synchronized Response exitLobby(GameController gameController, String username) {
        Objects.requireNonNull(username);

        if (gameController == null) return Response.failure("User not in lobby");

        return removeFromLobbyIfNecessary(gameController, username, game -> game.exitLobby(username));
    }

    /**
     * This method removes a user from the game.
     * @param username The username of the user who wants to leave the game.
     * @param gameController The GameController of the user with username.
     * @return SUCCESS iff
     * <ul>
     *  <li> gameController has successfully removed the user from the game </li>
     * </ul>
     * FAILURE otherwise.
     *
     * @throws NullPointerException if gameController or username are null
     * @see GameController#exitGame(String)
     */
    public synchronized Response<VoidEventData> exitGame(GameController gameController, String username) {
        Objects.requireNonNull(gameController);
        Objects.requireNonNull(username);

        return gameController.exitGame(username);
    }

    /**
     * This method starts a game.
     * If the call succeeds, a message is sent to all users
     * that the game is no longer available, as it has started.
     *
     * @param gameController the GameController of the user with username
     * @param username the username of the user who wants to start the game
     *
     * @throws NullPointerException if username is null
     * @return FAILURE if
     * <ul>
     *  <li> gameController is null </li>
     *  <li> {@link GameController#startGame(String) startGame()} fails </li>
     * </ul>
     * @see GameController#startGame(String)
     */
    public Response<VoidEventData> startGame(GameController gameController, String username) {
        Objects.requireNonNull(username);

        if (gameController == null) {
            return Response.failure("User not in lobby");
        }

        Response<VoidEventData> response = gameController.startGame(username);

        if (response.isOk()) {
            // we need to notify the other player that this game is no longer available
            forEachAuthenticated(new GameIsNoLongerAvailableEventData(gameController.getGameView().getName()));
        }

        return response;
    }

    private synchronized Optional<GameController> getGameController(String gameName) {
        for (GameController controller: gameControllerList) {
            if (controller.gameName().equalsIgnoreCase(gameName))
                return Optional.of(controller);
        }

        return Optional.empty();
    }

    /**
     * This method sends to the user with username "username", using the provided transmitter,
     * all the available games that the user can access.
     *
     * @param username The username of the user who entered the menu and wants to receive available games.
     * @param transmitter The {@link EventTransmitter eventTransmitter} of the user with username "username".
     *
     * @throws NullPointerException if
     * <ul>
     *     <li> transmitter is null </li>
     *     <li> username is null </li>
     * </ul>
     *
     * @see EventTransmitter
     * @see it.polimi.ingsw.controller.VirtualView
     */
    public synchronized void playerHasJoinMenu (EventTransmitter transmitter, String username) {
        Objects.requireNonNull(transmitter);
        Objects.requireNonNull(username);

        assert authenticated.contains(Pair.of(username, transmitter));

        transmitter.broadcast(
                new GameHasBeenCreatedEventData(
                        gameControllerList.stream()
                                .filter(g -> g.isAvailableForJoin(username))
                                .map(GameController::getGameView)
                                .map(GameHasBeenCreatedEventData.AvailableGame::new)
                                .toList()
                )
        );
    }

    /**
     * This method authenticates the user with username "username" and password "password".
     * If the method succeeds, the transmitter is added to the list of connected players
     * and will receive all events for the games that are created and also for the games
     * that are no longer available.
     *
     * @return FAILURE iff
     * <ul>
     *     <li>username or password is empty</li>
     *     <li>username is already authenticated</li>
     *     <li>password is wrong for username</li>
     * </ul> SUCCESS otherwise
     *
     * @throws NullPointerException iff
     * <ul>
     *     <li>transmitter is null</li>
     *     <li>username is null</li>
     *     <li>password is null</li>
     * </ul>
     * 
     * @see MenuController#logout(EventTransmitter, String)
     */
    public Response<UsernameEventData> login(EventTransmitter transmitter, String username, String password) {
        Objects.requireNonNull(transmitter);
        Objects.requireNonNull(username);
        Objects.requireNonNull(password);

        if (username.isEmpty() || password.isEmpty()) {
            return new Response<>(
                            "Password or username too short",
                            ResponseStatus.FAILURE,
                            new UsernameEventData(""));
        }

        User user = this.getUser(username, password);

        if (user.isConnected())
            return new Response<>(
                            "User login in other connection",
                            ResponseStatus.FAILURE,
                            new UsernameEventData(""));

        if (!user.passwordMatches(password))
            return new Response<>(
                            "Bad credentials",
                            ResponseStatus.FAILURE,
                            new UsernameEventData(""));

        user.setConnected(true);

        authenticated.add(Pair.of(user.getUsername(), transmitter));

        Logger.writeMessage("%s authenticated correctly".formatted(username));

        return new Response<>(
                        "OK!",
                        ResponseStatus.SUCCESS,
                        new UsernameEventData(user.getUsername()));
    }

    /**
     * This method is used to log out the user with the given username.
     *
     * @param username the username of the user who wants to log out.
     * @throws NullPointerException if the eventTransmitter is null.
     * @return always returns SUCCESS.
     */
    public synchronized Response<VoidEventData> logout (EventTransmitter eventTransmitter, String username) {
        assert authenticated.contains(Pair.of(username, eventTransmitter));
        Objects.requireNonNull(eventTransmitter);

        authenticated.remove(Pair.of(username, eventTransmitter));

        assert !authenticated.contains(Pair.of(username, eventTransmitter));

        setConnectUser(username, false);

        return Response.success("You are now logout");
    }

    private synchronized void setConnectUser(String username, boolean connected) {
        for (User user : users) {
            if (user.is(username)) {
                user.setConnected(connected);
            }
        }
    }

    private Response<VoidEventData> removeFromLobbyIfNecessary(GameController gameController, String username,
                                                Function<GameController, Response<VoidEventData>> executor)
    {
        Objects.requireNonNull(gameController);
        Objects.requireNonNull(username);

        Response<VoidEventData> response;

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

    /**
     * This method marks the user with username as disconnected and makes them exit
     * from a game or lobby if they are currently in one.
     *
     * @param username the username of the user disconnecting
     * @param gameController the possible game the user is in
     * @param transmitter the user's event transmitter
     * @throws NullPointerException iff transmitter is null
     */
    public synchronized void forceDisconnect(EventTransmitter transmitter, GameController gameController,
                                             String username)
    {
        Objects.requireNonNull(transmitter);

        if (username == null) {
            assert authenticated.stream().noneMatch(p -> p.getValue().equals(transmitter));
            return;
        }

        authenticated.remove(Pair.of(username, transmitter));

        setConnectUser(username, false);

        if (gameController != null) {
            removeFromLobbyIfNecessary(gameController, username, game -> {
                game.disconnect(username);
                return Response.success("");
            });
        }
    }

    private final EventListener<GameHasBeenStoppedInternalEventData> listenerGameStop = eventData -> {
        synchronized (this) {
            authenticated.stream()
                    .filter(a -> eventData.getGameController().isAvailableForJoin(a.getKey()))
                    .forEach(a -> {
                        a.getValue()
                                .broadcast(new GameIsNoLongerAvailableEventData(eventData
                                        .getGameController()
                                        .gameName()
                                ));

                        a.getValue().broadcast(new GameHasBeenCreatedEventData(eventData
                                .getGameController()
                                .getGameView()));
                    });
        }
    };

    private final EventListener<GameOverInternalEventData> listenerGameOver = eventData -> {
        synchronized (this) {
            gameControllerList.remove(eventData.getGameController());
            eventData.getGameController().getInternalTransceiver().unregisterAllListeners();
            forEachAuthenticated(new GameIsNoLongerAvailableEventData(eventData.getGameController().gameName()));
        }
    };

    private void syncToGameController(GameController gameController) {
        Objects.requireNonNull(gameController);

        GameHasBeenStoppedInternalEventData
                .castEventReceiver(gameController.getInternalTransceiver())
                .registerListener(this.listenerGameStop);

        GameOverInternalEventData
                .castEventReceiver(gameController.getInternalTransceiver())
                .registerListener(this.listenerGameOver);
    }

    /**
     * This function creates a new game if and only if there is no other game with the same name.
     *
     * @param username the username of the user who wants to create the game.
     * @param gameName the name of the game to be created.
     *
     * @throws NullPointerException if
     * <ul>
     *     <li>username is null</li>
     *     <li>gameName is null</li>
     * </ul>
     *
     * @return SUCCESS iff there is no game with the same name
     */
    public Response<VoidEventData> createNewGame(String gameName, String username) {
        Objects.requireNonNull(gameName);
        Objects.requireNonNull(username);

        if (gameName.length() < 2)
            return Response.failure("Game name is too short");

        Game game = new Game(gameName, username);
        GameController controller = new GameController(game);

        synchronized (this) {
            if (getGameController(gameName).isPresent()) {
                return Response.failure("This game already exists");
            }

            syncToGameController(controller);

            this.gameControllerList.add(controller);

            forEachAuthenticated(new GameHasBeenCreatedEventData(game));

            return Response.success("Game: [%s] has been created".formatted(gameName));
        }
    }

    /**
     * This function allows the player with username "username" to enter the lobby of the game with the name "gameName".
     *
     * @param username The username of the user
     * @param gameName The name of the game where the user wants to enter
     * @param transmitter The eventTransmitter of the user username
     *
     * @throws NullPointerException if
     * <ul>
     *     <li>transmitter is null</li>
     *     <li>username is null</li>
     *     <li>gameName is null</li>
     * </ul>
     *
     * @return A pair with SUCCESS equal to true iff the user has successfully entered the lobby
     *  with the gameController of the game.
     */
    public synchronized Pair<Response<VoidEventData>, GameController> joinLobby(EventTransmitter transmitter,
                                                                                String username, String gameName)
    {
        Objects.requireNonNull(transmitter);
        Objects.requireNonNull(username);
        Objects.requireNonNull(gameName);

        Optional<GameController> controller = this.getGameController(gameName);

        if (controller.isPresent()) {
            synchronized (controller.get().getLock()) {
                Response<VoidEventData> response = controller.get().joinLobby(transmitter, username);

                if (response.isOk()) {
                    if (controller.get().isFull()){
                        forEachAuthenticated(new GameIsNoLongerAvailableEventData(
                                controller
                                        .get()
                                        .getGameView()
                                        .getName()
                        ));
                    }
                    return Pair.of(response, controller.get());
                } else {
                    return Pair.of(response, null);
                }
            }
        }

        return Pair.of(Response.failure("There is no game with this name..."), null);
    }

    /**
     * This method allows the user with the given username to enter the game with the
     * given name, bypassing the lobby.
     *
     * @param gameName The name of the game
     * @param username The username of the user
     * @param eventTransmitter The event transmitter of the user
     *
     * @throws NullPointerException if
     * <ul>
     *     <li>eventTransmitter is null</li>
     *     <li>username is null</li>
     *     <li>gameName is null</li>
     * </ul>
     *
     * @return a pair with SUCCESS equal to true iff the user managed to enter the
     * game and the gameController of the game.
     * */
    public Pair<Response<VoidEventData>, GameController> joinGame(EventTransmitter eventTransmitter,
                                                                  String username, String gameName)
    {
        Objects.requireNonNull(eventTransmitter);
        Objects.requireNonNull(username);
        Objects.requireNonNull(gameName);

        Optional<GameController> controller = this.getGameController(gameName);

        if (controller.isPresent()) {
            Response<VoidEventData> response = controller.get().rejoinGame(username, eventTransmitter);

            if (response.isOk()) {
                return Pair.of(response, controller.get());
            } else {
                return Pair.of(response, null);
            }
        }

        return Pair.of(Response.failure("There is no game with this name..."), null);
    }

    /**
     * This method is used to restart a game.
     * It notifies the players who previously participated in the game that the game has restarted,
     * and therefore they can join without going through the lobby.
     *
     * @param username il player che vuole restartare il game.
     * @param gameController il game che si vuole far ripartire
     *
     * @throws NullPointerException iff
     * <ul>
     *     <li> gameController is null </li>
     *     <li> username is null </li>
     * </ul>
     *
     * @return SUCCESS iff {@link GameController#restartGame(String) restartGame} return SUCCESS
     *
     * @see GameController#restartGame(String)
     * @see Response
     * @see GameController
     * */
    public synchronized Response<VoidEventData> restartGame(GameController gameController, String username) {
        Objects.requireNonNull(username);
        Objects.requireNonNull(gameController);

        Response<VoidEventData> response;

        synchronized (gameController.getLock()) {
            response = gameController.restartGame(username);

            if (response.isOk()) {
                GameView view = gameController.getGameView();
                this.authenticated.stream().filter(p -> gameController.isAvailableForJoin(p.getKey()))
                        .forEach(p -> {
                            p.getValue().broadcast(new GameIsNoLongerAvailableEventData(view.getName()));
                            p.getValue().broadcast(new GameHasBeenCreatedEventData(view));
                        });
            }
        }

        return response;
    }
}
