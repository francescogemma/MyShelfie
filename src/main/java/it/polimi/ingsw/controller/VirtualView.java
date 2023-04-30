package it.polimi.ingsw.controller;

import it.polimi.ingsw.event.EventTransceiver;
import it.polimi.ingsw.event.data.EventData;
import it.polimi.ingsw.event.data.LoginEventData;
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

import java.awt.*;
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

        LoginEventData.responder(transceiver, transceiver,          event -> login(event.getUsername(), event.getPassword()));
        StartGameEventData.responder(transceiver, transceiver,      event -> startGame());
        InsertTileEventData.responder(transceiver, transceiver,     event -> insertTile(event.column()));
        SelectTileEventData.responder(transceiver, transceiver,     event -> selectTile(event.coordinate()));
        DeselectTileEventData.responder(transceiver, transceiver,   event -> deselectTile(event.coordinate()));
        JoinGameEventData.responder(transceiver, transceiver,       event -> joinGame(event.gameName()));
        CreateNewGameEventData.responder(transceiver, transceiver,  event -> createNewGame(event.gameName()));
        PlayerExitGame.responder(transceiver, transceiver,          event -> exitGame());
        PauseGameEventData.responder(transceiver, transceiver,      event -> pauseGame());
        LogoutEventData.responder(transceiver, transceiver,         event -> logout());

        PlayerDisconnectedInternalEventData.castEventReceiver(transceiver).registerListener(event -> disconnect());

        JoinStartedGameEventData.castEventReceiver(transceiver).registerListener(event -> this.sendGameState());
        PlayerHasJoinMenu       .castEventReceiver(transceiver).registerListener(event -> this.playerHasJoinMenu());
    }

    private synchronized Response logout () {
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

    private synchronized Response pauseGame () {
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

    private synchronized Response exitGame () {
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
            MenuController.getInstance().playerHasJoinMenu(this.transceiver);
        }
    }

    private synchronized Response createNewGame (String gameName) {
        if (this.isAuthenticated()) {
            if (!isInGame()) {
                return MenuController.INSTANCE.createNewGame(gameName, username);
            } else {
                return DEFAULT_MESSAGE_ALREADY_IN_GAME;
            }
        } else {
            return DEFAULT_MESSAGE_NOT_AUTHENTICATED;
        }
    }

    private synchronized Response joinGame (String gameName) {
        if (this.isAuthenticated()) {
            if (!this.isInGame()) {
                Pair<Response, GameController> res = MenuController.INSTANCE.joinGame(this.transceiver, this.username, gameName);

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

    private synchronized Response login (String username, String password) {
        if (!isAuthenticated()) {
            Response response = MenuController.INSTANCE.authenticated(transceiver, username, password);

            if (response.isOk()) {
                this.username = username;
            }

            return response;
        } else {
            return new Response("You are already login", ResponseStatus.FAILURE);
        }
    }

    private synchronized Response deselectTile (Coordinate coordinate) {
        if (isInGame()) {
            return gameController.deselectTile(username, coordinate);
        } else {
            return DEFAULT_MESSAGE_NOT_AUTHENTICATED;
        }
    }

    private synchronized Response insertTile (int col) {
        if (isInGame()) {
            return gameController.insertSelectedTilesInBookshelf(username, col);
        } else {
            return DEFAULT_MESSAGE_NOT_AUTHENTICATED;
        }
    }

    private synchronized Response selectTile(Coordinate coordinate) {
        if (isAuthenticated()) {
            return this.gameController.selectTile(username, coordinate);
        } else {
            return DEFAULT_MESSAGE_NOT_AUTHENTICATED;
        }
    }

    private synchronized Response startGame () {
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
