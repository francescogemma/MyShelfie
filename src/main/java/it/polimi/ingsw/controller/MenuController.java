package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.db.IdentifiableNotFoundException;
import it.polimi.ingsw.event.Responder;
import it.polimi.ingsw.event.data.LoginEventData;
import it.polimi.ingsw.event.data.clientEvent.JoinGameEventData;
import it.polimi.ingsw.event.data.gameEvent.GameHasBeenCreatedEventData;
import it.polimi.ingsw.event.data.gameEvent.GameHasStartedEventData;
import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.controller.db.DBManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class MenuController {
    private final List<GameController> gameControllerList;
    public static final MenuController INSTANCE;

    private final List<VirtualView> authenticated;
    private final List<VirtualView> notAuthenticated;
    private final List<User> users;

    static {
        INSTANCE = new MenuController();
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
        synchronized (users) {
            for (User user: users) {
                if (user.getName().equals(username))
                    return user;
            }
        }

        User user;
        DBManager<User> userDBManager = DBManager.getUsersDBManager();

        try {
            user = userDBManager.load(username);
        } catch (IdentifiableNotFoundException e) {
            user = new User(username, password);
        }

        return user;
    }

    /**
     * We remove virtualView from notAuthenticated and place in authenticated
     * */
    public boolean authenticate (VirtualView virtualView, String username, String password) {
        User user = this.getUser(username, password);

        if (user.passwordMatches(password)) {
            synchronized (notAuthenticated) {
                notAuthenticated.remove(virtualView);
            }

            synchronized (authenticated) {
                authenticated.add(virtualView);
            }
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
     * this function add virtualView and connect transmitter to GameHasBeenCreated
     * */
    public void join(VirtualView virtualView) {
        if (virtualView == null)
            throw new NullPointerException();

        synchronized (authenticated) {
            if (authenticated.contains(virtualView))
                throw new IllegalArgumentException("Already present in authenticated");
        }

        synchronized (notAuthenticated) {
            if (this.notAuthenticated.contains(virtualView))
                throw new IllegalArgumentException("Already present in notAuthenticated");
            notAuthenticated.add(virtualView);
        }

        LoginEventData.responder(virtualView.getNetworkTransmitter(), virtualView.getNetworkReceiver(), loginEventData -> {
            String username = loginEventData.getUsername();
            String password = loginEventData.getPassword();

            if (this.authenticate(virtualView, username, password)) {
                List<String> gamePresent = new ArrayList<>();

                synchronized (this.gameControllerList) {
                    for (GameController gameController : gameControllerList) {
                        gamePresent.add(gameController.gameName());
                    }
                }

                virtualView.getNetworkTransmitter().broadcast(new GameHasBeenCreatedEventData(gamePresent));

                // TODO we need to set here the new connection from client to server
                JoinGameEventData.responder(virtualView.getNetworkTransmitter(), virtualView.getNetworkReceiver(), event -> {
                    Optional<GameController> gameController = this.getGameController(event.getGameName());

                    if (gameController.isPresent()) {
                        return gameController.get().join(virtualView);
                    } else {
                        return new Response("Ciao", ResponseStatus.FAILURE);
                    }
                });

                return new Response("You are login", ResponseStatus.SUCCESS);
            }
            return new Response("You are not login", ResponseStatus.FAILURE);
        });
    }

    public Response createNewGame(String gameName) {
        Game game = new Game(gameName);
        GameController controller = new GameController(game);

        synchronized (gameControllerList) {
            Optional<GameController> gameController = this.getGameController(gameName);
            if (gameController.isPresent()) {
                return new Response("This game already exists", ResponseStatus.FAILURE);
            }

            this.gameControllerList.add(controller);
        }

        synchronized (authenticated) {
            for (VirtualView virtualView: authenticated) {
                virtualView.getNetworkTransmitter().broadcast(new GameHasBeenCreatedEventData(List.of(gameName)));
            }
        }

        return new Response("Game : [" + gameName + "] has been created", ResponseStatus.SUCCESS);
    }

    public Response joinGame(VirtualView virtualView, String gameName) {
        synchronized (gameControllerList) {
            for (GameController gameController: this.gameControllerList) {
                if (gameController.gameName().equals(gameName)) {
                    return gameController.join(virtualView);
                }
            }
        }

        return new Response("There is no game with this name...", ResponseStatus.FAILURE);
    }
}
