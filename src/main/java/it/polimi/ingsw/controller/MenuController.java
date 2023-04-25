package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.db.IdentifiableNotFoundException;
import it.polimi.ingsw.event.data.EventData;
import it.polimi.ingsw.event.data.game.GameHasBeenCreatedEventData;
import it.polimi.ingsw.event.transmitter.EventTransmitter;
import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.controller.db.DBManager;
import it.polimi.ingsw.utils.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MenuController {
    private final List<GameController> gameControllerList;
    public static final MenuController INSTANCE;

    private final List<EventTransmitter> authenticated;
    private final List<EventTransmitter> notAuthenticated;
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
            userDBManager.save(user);
            System.out.println("Created new user: " + user.getName());
        }

        return user;
    }

    /**
     * We remove virtualView from notAuthenticated and place in authenticated
     * */
    private boolean authenticate (EventTransmitter transmitter, String username, String password) {
        User user = this.getUser(username, password);

        if (user.passwordMatches(password)) {
            synchronized (notAuthenticated) {
                notAuthenticated.remove(transmitter);
            }

            synchronized (authenticated) {
                authenticated.add(transmitter);
            }
            System.out.println(user.getName() + " authenticated correctly");

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
    }

    public Response authenticated(EventTransmitter view, String username, String password) {
        if (this.authenticate(view, username, password)) {
            List<String> gamePresent = new ArrayList<>();

            synchronized (this.gameControllerList) {
                for (GameController gameController : gameControllerList) {
                    if (gameController.isAvailableForJoin()) {
                        gamePresent.add(gameController.gameName());
                    }
                }
            }

            view.broadcast(new GameHasBeenCreatedEventData(gamePresent));

            return new Response("You are log in", ResponseStatus.SUCCESS);
        } else {
            return new Response("You are not login", ResponseStatus.FAILURE);
        }
    }

    private void forEachAuthenticatedBroadcast(EventData eventData) {
        synchronized (authenticated) {
            authenticated.forEach(t -> t.broadcast(eventData));
        }
    }

    private void forEachNotAuthenticatedBroadcast(EventData eventData) {
        synchronized (notAuthenticated) {
            notAuthenticated.forEach(t -> t.broadcast(eventData));
        }
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

        this.forEachAuthenticatedBroadcast(new GameHasBeenCreatedEventData(List.of(gameName)));

        return new Response("Game : [" + gameName + "] has been created", ResponseStatus.SUCCESS);
    }

    public Pair<Response, GameController> joinGame(EventTransmitter transmitter, String username, String gameName) {
        synchronized (gameControllerList) {
            for (GameController gameController: this.gameControllerList) {
                if (gameController.gameName().equals(gameName)) {

                    Response response = gameController.join(transmitter, username);

                    if (response.isOk()) {
                        return Pair.of(response, gameController);
                    } else {
                        return Pair.of(response, null);
                    }

                }
            }
        }

        return Pair.of(new Response("There is no game with this name...", ResponseStatus.FAILURE), null);
    }
}
