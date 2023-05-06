package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.db.DBManager;
import it.polimi.ingsw.controller.db.IdentifiableNotFoundException;
import it.polimi.ingsw.event.EventTransceiver;
import it.polimi.ingsw.event.data.EventData;
import it.polimi.ingsw.event.data.game.GameHasBeenCreatedEventData;
import it.polimi.ingsw.event.transmitter.EventTransmitter;
import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.model.game.GameView;
import it.polimi.ingsw.utils.Logger;
import it.polimi.ingsw.utils.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MenuController {
    private static final MenuController INSTANCE;
    private final List<GameController> gameControllerList;

    private final List<VirtualView> authenticated;
    private final List<VirtualView> notAuthenticated;
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
                INSTANCE.gameControllerList.add(new GameController(game));
            }
        }
    }

    private MenuController() {
        gameControllerList = new ArrayList<>();
        notAuthenticated = new ArrayList<>();
        users = new ArrayList<>();
        authenticated = new ArrayList<>();
    }

    public static MenuController getInstance() {
        return INSTANCE;
    }

    /**
     * password is required in case of player not existing
     * */
    private User getUser (String username, String password) {
        User user;

        synchronized (users) {
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

    /**
     * We remove virtualView from notAuthenticated and place in authenticated
     * */
    private boolean authenticate (VirtualView transmitter, String username, String password) {
        User user = this.getUser(username, password);

        if (user.passwordMatches(password)) {
            synchronized (authenticated) {
                if (authenticated.stream().anyMatch(v -> v.getUsername().get().equals(username))) {
                    return false;
                }
            }

            synchronized (notAuthenticated) {
                notAuthenticated.remove(transmitter);
            }

            synchronized (authenticated) {
                authenticated.add(transmitter);
            }
            Logger.writeMessage(user.getName() + " authenticated correctly");

            return true;
        } else {
            return false;
        }
    }

    private Optional<GameController> getGameController(String gameName) {
        synchronized (this.gameControllerList) {
            for (GameController controller: gameControllerList) {
                if (controller.gameName().equals(gameName))
                    return Optional.of(controller);
            }
        }

        return Optional.empty();
    }

    /**
     * this function adds virtualView and connect transmitter to GameHasBeenCreated
     * */
    public void joinMenu(VirtualView virtualView) {
        if (virtualView == null)
            throw new NullPointerException();

        synchronized (authenticated) {
            if (authenticated.contains(virtualView))
                throw new IllegalArgumentException("Already present in authenticated");
        }

        synchronized (notAuthenticated) {
            if (notAuthenticated.contains(virtualView))
                throw new IllegalArgumentException("Already present in notAuthenticated");
            notAuthenticated.add(virtualView);
        }
    }

    public void playerHasJoinMenu (EventTransmitter transceiver, String username) {
        synchronized (gameControllerList) {
            transceiver.broadcast(
                    new GameHasBeenCreatedEventData(gameControllerList
                        .stream()
                        .filter(g -> g.isAvailableForJoin(username))
                        .map(g -> {
                            GameView view = g.getGameView();
                            return new GameHasBeenCreatedEventData.AvailableGame(
                                    g.getOwner(),
                                    view.getName(),
                                    view.isStarted(),
                                    view.isPause(),
                                    view.isStopped()
                            );
                        }).toList()
                    )
            );
        }
    }

    public Response authenticated(VirtualView view, String username, String password) {
        return this.authenticate(view, username, password) ?
            new Response("You are log in", ResponseStatus.SUCCESS) :
            new Response("Bad credentials", ResponseStatus.FAILURE);
    }

    /**
     * Let's assume that the player is not in any game.
     */
    public Response logout (VirtualView eventTransmitter) {
        synchronized (authenticated) {
            assert authenticated.contains(eventTransmitter);
            authenticated.remove(eventTransmitter);
        }

        synchronized (notAuthenticated) {
            assert !notAuthenticated.contains(eventTransmitter);
            notAuthenticated.add(eventTransmitter);
        }

        return new Response("You are now logout", ResponseStatus.SUCCESS);
    }

    public void forceDisconnect(VirtualView transmitter, GameController gameController, String username) {
        synchronized (notAuthenticated) {
            notAuthenticated.remove(transmitter);
        }

        synchronized (authenticated) {
            authenticated.remove(transmitter);
        }

        if (gameController != null) {
            assert username != null;

            synchronized (gameControllerList) {
                gameController.disconnect(username);
            }
        }
    }

    public Response createNewGame(String gameName, String username) {
        if (gameName == null || gameName.isEmpty()) {
            return new Response("Game is empty or null", ResponseStatus.FAILURE);
        }

        if (gameName.length() < 2) {
            return new Response("Game name is too short", ResponseStatus.SUCCESS);
        }

        Game game = new Game(gameName, username);
        GameController controller = new GameController(game);

        synchronized (gameControllerList) {
            Optional<GameController> gameController = this.getGameController(gameName);
            if (gameController.isPresent()) {
                return new Response("This game already exists", ResponseStatus.FAILURE);
            }

            this.gameControllerList.add(controller);
        }

        this.authenticated
                .stream()
                .filter(v -> !v.isInGame() && !v.isInLobby())
                .forEach(
                        p -> p.broadcast(new GameHasBeenCreatedEventData(
                                List.of(
                                    new GameHasBeenCreatedEventData.AvailableGame(
                                            username,
                                            gameName,
                                            game.isStarted(),
                                            game.isPause(),
                                            game.isStopped()
                                    )
                                )
                            )
                )
        );

        return new Response("Game: [%s] has been created".formatted(gameName), ResponseStatus.SUCCESS);
    }

    public Pair<Response, GameController> joinLobby(VirtualView transmitter, String username, String gameName) {
        Optional<GameController> controller = this.getGameController(gameName);

        if (controller.isPresent()) {
            Response response = controller.get().joinLobby(transmitter, username);

            if (response.isOk()) {
                return Pair.of(response, controller.get());
            } else {
                return Pair.of(response, null);
            }
        }

        return Pair.of(new Response("There is no game with this name...", ResponseStatus.FAILURE), null);
    }

    public Pair<Response, GameController> joinGame(VirtualView virtualView, String username, String gameName) {
        Optional<GameController> controller = this.getGameController(gameName);

        if (controller.isPresent()) {
            Response response = controller.get().rejoinGame(username, virtualView);

            if (response.isOk()) {
                return Pair.of(response, controller.get());
            } else {
                return Pair.of(response, null);
            }
        }

        return Pair.of(new Response("There is no game with this name...", ResponseStatus.FAILURE), null);
    }
}
