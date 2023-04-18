package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.db.IdentifiableNotFoundException;
import it.polimi.ingsw.event.data.LoginEventData;
import it.polimi.ingsw.event.data.client.CreateNewGameEventData;
import it.polimi.ingsw.event.data.client.JoinGameEventData;
import it.polimi.ingsw.event.data.game.GameHasBeenCreatedEventData;
import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.controller.db.DBManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
                        if (gameController.isAvailableForJoin()) {
                            gamePresent.add(gameController.gameName());
                        }
                    }
                }

                virtualView.getNetworkTransmitter().broadcast(new GameHasBeenCreatedEventData(gamePresent));

                // TODO we need to set here the new connection from client to server
                JoinGameEventData.responder(virtualView.getNetworkTransmitter(), virtualView.getNetworkReceiver(), event -> {
                    return this.joinGame(virtualView, event.getGameName());
                });

                CreateNewGameEventData.responder(virtualView.getNetworkTransmitter(), virtualView.getNetworkReceiver(), event -> {
                    if (virtualView.isInGame()) {
                        return new Response("You can't create a game if you are in a game...", ResponseStatus.FAILURE);
                    }
                    return this.createNewGame(event.gameName());
                });
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
        if (virtualView.isInGame()) {
            return new Response("You are already in a game", ResponseStatus.FAILURE);
        }

        synchronized (gameControllerList) {
            for (GameController gameController: this.gameControllerList) {
                if (gameController.gameName().equals(gameName)) {

                    Response response = gameController.join(virtualView);

                    if (response.isOk()) {
                        virtualView.setGameController(gameController);
                    }
                }
            }
        }

        return new Response("There is no game with this name...", ResponseStatus.FAILURE);
    }
}
