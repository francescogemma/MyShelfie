package it.polimi.ingsw.controller;

import it.polimi.ingsw.event.EventTransceiver;
import it.polimi.ingsw.event.data.EventData;
import it.polimi.ingsw.event.data.client.LoginEventData;
import it.polimi.ingsw.event.data.client.*;
import it.polimi.ingsw.event.data.game.InitialGameEventData;
import it.polimi.ingsw.event.data.game.PersonalGoalSetEventData;
import it.polimi.ingsw.event.data.internal.ForceExitGameEventData;
import it.polimi.ingsw.event.data.internal.PlayerDisconnectedInternalEventData;
import it.polimi.ingsw.event.receiver.CastEventReceiver;
import it.polimi.ingsw.event.receiver.EventListener;
import it.polimi.ingsw.event.transmitter.EventTransmitter;
import it.polimi.ingsw.model.game.GameView;
import it.polimi.ingsw.utils.Logger;
import it.polimi.ingsw.utils.Pair;

import java.util.Optional;

public class VirtualView implements EventTransmitter{
    private GameController gameController;
    private String username;
    private final EventTransceiver transceiver;

    private static final Response DEFAULT_MESSAGE_NOT_AUTHENTICATED = new Response("You are not login", ResponseStatus.FAILURE);
    private static final Response DEFAULT_MESSAGE_ALREADY_IN_GAME = new Response("You are in a game...", ResponseStatus.FAILURE);
    private static final Response DEFAULT_MESSAGE_NOT_IN_GAME = new Response("You are not in a game...", ResponseStatus.FAILURE);
    private static final Response DEFAULT_IN_LOBBY = new Response("You are in lobby...", ResponseStatus.FAILURE);
    private static final Response DEFAULT_NOT_IN_LOBBY = new Response("You are not in lobby...", ResponseStatus.FAILURE);

    private CastEventReceiver<ForceExitGameEventData> castEventReceiver = null;

    private final EventListener<ForceExitGameEventData> listener = (event -> {
        synchronized (this) {
            this.gameController = null;
        }
    });

    public VirtualView(EventTransceiver transceiver) {
        if (transceiver == null)
            throw new NullPointerException();

        this.transceiver = transceiver;
        gameController = null;

        LoginEventData          .responder(transceiver, transceiver, this::login);
        StartGameEventData      .responder(transceiver, transceiver, this::startGame);
        InsertTileEventData     .responder(transceiver, transceiver, this::insertTile);
        SelectTileEventData     .responder(transceiver, transceiver, this::selectTile);
        DeselectTileEventData   .responder(transceiver, transceiver, this::deselectTile);
        JoinGameEventData       .responder(transceiver, transceiver, this::joinGame);
        RestartGameEventData    .responder(transceiver, transceiver, this::restartGame);
        JoinLobbyEventData      .responder(transceiver, transceiver, this::joinLobby);

        CreateNewGameEventData  .responder(transceiver, transceiver, this::createNewGame);
        PlayerExitGame          .responder(transceiver, transceiver, this::exitGame);
        PauseGameEventData      .responder(transceiver, transceiver, this::pauseGame);
        LogoutEventData         .responder(transceiver, transceiver, this::logout);
        ExitLobbyEventData      .responder(transceiver, transceiver, this::exitLobby);

        PlayerDisconnectedInternalEventData.castEventReceiver(transceiver).registerListener(event -> disconnect());

        PlayerHasJoinMenu       .castEventReceiver(transceiver).registerListener(event -> this.playerHasJoinMenu());
    }

    private synchronized Response joinLobby (JoinLobbyEventData event) {
        if (isAuthenticated() && !isInGame() && !isInLobby()) {
            Pair<Response, GameController> res = MenuController
                    .getInstance()
                    .joinLobby(this, username, event.gameName());

            if (res.getKey().isOk()) {
                this.gameController = res.getValue();
            }

            return res.getKey();
        } else {
            return DEFAULT_IN_LOBBY;
        }
    }

    private synchronized Response exitLobby(ExitLobbyEventData event) {
        if (isInLobby()) {
            assert this.isAuthenticated();
            return this.gameController.exitLobby(username);
        } else {
            return DEFAULT_NOT_IN_LOBBY;
        }
    }

    private synchronized Response logout (LogoutEventData eventData) {
        if (isInGame() || isInLobby()) {
            Logger.writeWarning("The client has asked to log out but is in game");
            return new Response("You are in a game or lobby...", ResponseStatus.FAILURE);
        }

        if (isAuthenticated()) {
            Response response = MenuController.getInstance().logout(this);

            if (response.isOk()) {
                this.username = null;
            }

            return response;
        } else {
            Logger.writeWarning("The client has asked to log out but is not authenticated");
            return new Response("You are not login", ResponseStatus.SUCCESS);
        }
    }

    private synchronized void disconnect() {
        Logger.writeMessage("user %s disconnected".formatted(username));
        MenuController.getInstance().forceDisconnect(
                this,
                gameController,
                username
        );

        if (gameController != null)
            removeListener();

        this.gameController = null;
    }

    private synchronized Response pauseGame (PauseGameEventData eventData) {
        Logger.writeMessage("Call for username: %s".formatted(username));

        if (isInGame()) {
            Response response = gameController.stopGame(username);

            if (response.isOk()) {
                removeListener();
                this.gameController = null;
            }

            return response;
        } else {
            return DEFAULT_MESSAGE_NOT_IN_GAME;
        }
    }

    private void removeListener () {
        assert this.castEventReceiver != null;
        this.castEventReceiver.unregisterListener(this.listener);
    }

    private synchronized Response exitGame (PlayerExitGame exitGame) {
        Logger.writeMessage("Call for username %s".formatted(username));
        if (isInGame()) {
            Response response = gameController.exitGame(username);

            if (response.isOk()) {
                removeListener();
                this.gameController = null;
            }
            return response;
        } else {
            return new Response("You are not in a game", ResponseStatus.FAILURE);
        }
    }

    private synchronized void playerHasJoinMenu () {
        if (this.isAuthenticated()) {
            MenuController.getInstance().playerHasJoinMenu(this.transceiver, username);
        } else {
            Logger.writeCritical("View send join menu but he is not authenticated");
        }
    }

    private synchronized Response createNewGame (CreateNewGameEventData eventData) {
        if (this.isAuthenticated()) {
            if (!isInGame() && !isInLobby()) {
                return MenuController.getInstance().createNewGame(eventData.gameName(), username);
            } else {
                return DEFAULT_MESSAGE_ALREADY_IN_GAME;
            }
        } else {
            return DEFAULT_MESSAGE_NOT_AUTHENTICATED;
        }
    }

    private synchronized Response restartGame (RestartGameEventData event) {
        if (isInLobby()) {
            assert isAuthenticated();

            Response res = gameController.restartGame(username);

            if (res.isOk()) {
                castEventReceiver.registerListener(this.listener);
            }

            return res;
        } else {
            return DEFAULT_NOT_IN_LOBBY;
        }
    }

    private synchronized Response joinGame (JoinGameEventData eventData) {
        if (this.isAuthenticated()) {
            if (isInLobby()) {
                Response res = gameController.joinGame(username);
                if (res.isOk()) {
                    GameView view = gameController.getGameView();
                    final int personalGoal = gameController.getPersonalGoal(username);

                    broadcast(new InitialGameEventData(view));
                    broadcast(new PersonalGoalSetEventData(personalGoal));
                    
                    castEventReceiver = ForceExitGameEventData.castEventReceiver(gameController.getInternalReceiver());
                    castEventReceiver.registerListener(this.listener);
                }

                return res;
            } else {
                return DEFAULT_NOT_IN_LOBBY;
            }
        } else
            return DEFAULT_MESSAGE_NOT_AUTHENTICATED;
    }

    private synchronized Response login(LoginEventData event) {
        if (!isAuthenticated()) {
            Response response = MenuController.getInstance().authenticated(
                    this,
                    event.getUsername(),
                    event.getPassword()
            );

            if (response.isOk()) {
                this.username = event.getUsername();
            }

            return response;
        } else {
            return new Response("You are already login", ResponseStatus.FAILURE);
        }
    }

    private synchronized Response deselectTile (DeselectTileEventData eventData) {
        if (isInGame()) {
            return gameController.deselectTile(username, eventData.coordinate());
        } else {
            return DEFAULT_MESSAGE_NOT_AUTHENTICATED;
        }
    }

    private synchronized Response insertTile (InsertTileEventData eventData) {
        if (isInGame()) {
            return gameController.insertSelectedTilesInBookshelf(username, eventData.column());
        } else {
            return DEFAULT_MESSAGE_NOT_AUTHENTICATED;
        }
    }

    private synchronized Response selectTile(SelectTileEventData eventData) {
        if (isInGame()) {
            return this.gameController.selectTile(username, eventData.coordinate());
        } else {
            return DEFAULT_MESSAGE_NOT_AUTHENTICATED;
        }
    }

    private synchronized Response startGame (StartGameEventData ignore) {
        if (this.isAuthenticated()) {
            return this.gameController.startGame(this.username);
        } else {
            return DEFAULT_MESSAGE_NOT_AUTHENTICATED;
        }
    }

    public synchronized Optional<String> getUsername () {
        return Optional.of(username);
    }

    public synchronized boolean isInGame() {
        return gameController != null && gameController.isInGame(username);
    }

    public void setGameController(GameController gameController) {
        /*
         * this method is not synchronized, and you need to
         * synchronize on this!!
         */
        assert Thread.holdsLock(this);

        assert this.gameController == null;
        this.gameController = gameController;

        assert gameController != null;

        castEventReceiver = ForceExitGameEventData.castEventReceiver(gameController.getInternalReceiver());
        castEventReceiver.registerListener(this.listener);
    }

    public synchronized boolean isAuthenticated () {
        return username != null;
    }

    public synchronized boolean isInLobby() {
        return this.gameController != null && !isInGame();
    }

    @Override
    public void broadcast(EventData data) {
        this.transceiver.broadcast(data);
    }
}
