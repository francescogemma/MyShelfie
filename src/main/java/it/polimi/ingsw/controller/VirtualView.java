package it.polimi.ingsw.controller;

import it.polimi.ingsw.event.EventTransceiver;
import it.polimi.ingsw.event.data.EventData;
import it.polimi.ingsw.event.data.client.LoginEventData;
import it.polimi.ingsw.event.data.client.*;
import it.polimi.ingsw.event.data.game.InitialGameEventData;
import it.polimi.ingsw.event.data.game.PersonalGoalSetEventData;
import it.polimi.ingsw.event.data.internal.PlayerDisconnectedInternalEventData;
import it.polimi.ingsw.event.receiver.EventReceiver;
import it.polimi.ingsw.event.transmitter.EventTransmitter;
import it.polimi.ingsw.model.game.GameView;
import it.polimi.ingsw.utils.Coordinate;
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
        CreateNewGameEventData  .responder(transceiver, transceiver, this::createNewGame);
        PlayerExitGame          .responder(transceiver, transceiver, this::exitGame);
        PauseGameEventData      .responder(transceiver, transceiver, this::pauseGame);
        LogoutEventData         .responder(transceiver, transceiver, this::logout);

        PlayerDisconnectedInternalEventData.castEventReceiver(transceiver).registerListener(event -> disconnect());

        JoinStartedGameEventData.castEventReceiver(transceiver).registerListener(event -> this.sendGameState());
        PlayerHasJoinMenu       .castEventReceiver(transceiver).registerListener(event -> this.playerHasJoinMenu());
    }

    private synchronized Response logout (LogoutEventData eventData) {
        if (isInGame()) {
            Logger.writeWarning("The client has asked to log out but is in game");
            return new Response("You are in a game...", ResponseStatus.FAILURE);
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
        MenuController.getInstance().forceDisconnect(this, username);

        this.gameController = null;
    }

    private synchronized Response pauseGame (PauseGameEventData eventData) {
        if (isInGame()) {
            Response response = MenuController.getInstance().stopGame(username);

            if (response.isOk()) {
                this.gameController = null;
            }

            return response;
        } else {
            return DEFAULT_MESSAGE_NOT_IN_GAME;
        }
    }

    private synchronized Response exitGame (PlayerExitGame exitGame) {
        if (isInGame()) {
            Response response = MenuController.INSTANCE.exitGame(username);

            if (response.isOk()) {
                this.gameController = null;
            }
            return response;
        } else {
            return new Response("You are not in a game", ResponseStatus.FAILURE);
        }
    }

    private synchronized void sendGameState() {
        if (this.isInGame()) {
            GameView view = gameController.getGameView();
            final int personalGoal = gameController.getPersonalGoal(username);

            broadcast(new InitialGameEventData(view));
            broadcast(new PersonalGoalSetEventData(personalGoal));
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
            if (!isInGame()) {
                return MenuController.INSTANCE.createNewGame(eventData.gameName(), username);
            } else {
                return DEFAULT_MESSAGE_ALREADY_IN_GAME;
            }
        } else {
            return DEFAULT_MESSAGE_NOT_AUTHENTICATED;
        }
    }

    private synchronized Response joinGame (JoinGameEventData eventData) {
        if (this.isAuthenticated()) {
            if (!this.isInGame()) {
                Pair<Response, GameController> res = MenuController.INSTANCE.joinGame(this.transceiver, this.username, eventData.gameName());

                if (res.getKey().isOk()) {
                    this.gameController = res.getValue();
                }

                return res.getKey();
            } else {
                return DEFAULT_MESSAGE_ALREADY_IN_GAME;
            }
        } else
            return DEFAULT_MESSAGE_NOT_AUTHENTICATED;
    }

    private synchronized Response login(LoginEventData event) {
        if (!isAuthenticated()) {
            Response response = MenuController.INSTANCE.authenticated(
                    transceiver,
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
        if (isAuthenticated()) {
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

    public boolean isInGame() {
        /*
         * this method is not synchronized, and you need to
         * synchronized on this!!
         */
        assert Thread.holdsLock(this);

        return this.gameController != null;
    }

    public void setGameController(GameController gameController) {
        /*
         * this method is not synchronized, and you need to
         * synchronized on this!!
         */
        assert Thread.holdsLock(this);

        this.gameController = gameController;
    }

    public boolean isAuthenticated () {
        /*
         * this method is not synchronized, and you need to
         * synchronized on this!!
         */
        assert Thread.holdsLock(this);

        return username != null;
    }

    @Override
    public void broadcast(EventData data) {
        this.transceiver.broadcast(data);
    }
}
