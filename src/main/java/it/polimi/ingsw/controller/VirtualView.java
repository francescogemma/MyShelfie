package it.polimi.ingsw.controller;

import com.sun.glass.ui.Menu;
import it.polimi.ingsw.event.EventTransceiver;
import it.polimi.ingsw.event.data.EventData;
import it.polimi.ingsw.event.data.LoginEventData;
import it.polimi.ingsw.event.data.client.*;
import it.polimi.ingsw.event.receiver.EventReceiver;
import it.polimi.ingsw.event.transmitter.EventTransmitter;
import it.polimi.ingsw.utils.Coordinate;
import it.polimi.ingsw.utils.Pair;

import java.util.Optional;

public class VirtualView implements EventTransmitter{
    private GameController gameController;
    private String username;
    private final EventTransceiver transceiver;

    private static final Response DEFAULT_MESSAGE_NOT_AUTHENTICATED = new Response("You are not login", ResponseStatus.FAILURE);
    private static final Response DEFAULT_MESSAGE_ALREADY_IN_GAME = new Response("You are in a game...", ResponseStatus.FAILURE);

    public VirtualView(EventTransceiver transceiver) {
        if (transceiver == null)
            throw new NullPointerException();

        this.transceiver = transceiver;
        gameController = null;

        LoginEventData.responder(transceiver, transceiver,          event -> login(event.getUsername(), event.getPassword()));
        StartGameEventData.responder(transceiver, transceiver,      event -> startGame());
        InsertTileEventData.responder(transceiver, transceiver,     event -> insertTile(event.getColumn()));
        SelectTileEventData.responder(transceiver, transceiver,     event -> selectTile(event.getCoordinate()));
        DeselectTileEventData.responder(transceiver, transceiver,   event -> deselectTile(event.coordinate()));
        JoinGameEventData.responder(transceiver, transceiver,       event -> joinGame(event.getGameName()));
        CreateNewGameEventData.responder(transceiver, transceiver,  event -> createNewGame(event.gameName()));

        PlayerHasJoinMenu.castEventReceiver(transceiver).registerListener(event -> this.playerHasJoinMenu());
    }

    private void playerHasJoinMenu () {
        if (this.isAuthenticated()) {
            MenuController.getInstance().playerHasJoinMenu(this.transceiver);
        }
    }

    private Response createNewGame (String gameName) {
        if (this.isAuthenticated()) {
            if (!isInGame()) {
                return MenuController.INSTANCE.createNewGame(gameName);
            } else {
                return DEFAULT_MESSAGE_ALREADY_IN_GAME;
            }
        } else {
            return DEFAULT_MESSAGE_NOT_AUTHENTICATED;
        }
    }

    private Response joinGame (String gameName) {
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

    private Response login (String username, String password) {
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

    private Response deselectTile (Coordinate coordinate) {
        if (isInGame()) {
            return gameController.deselectTile(username, coordinate);
        } else {
            return DEFAULT_MESSAGE_NOT_AUTHENTICATED;
        }
    }

    private Response insertTile (int col) {
        if (isInGame()) {
            return gameController.insertSelectedTilesInBookshelf(username, col);
        } else {
            return DEFAULT_MESSAGE_NOT_AUTHENTICATED;
        }
    }

    private Response selectTile(Coordinate coordinate) {
        if (isAuthenticated()) {
            return this.gameController.selectTile(username, coordinate);
        } else {
            return DEFAULT_MESSAGE_NOT_AUTHENTICATED;
        }
    }

    private Response startGame () {
        if (this.isAuthenticated()) {
            return this.gameController.startGame(this.username);
        } else {
            return DEFAULT_MESSAGE_NOT_AUTHENTICATED;
        }
    }

    public Optional<String> getUsername () {
        return Optional.of(username);
    }

    public EventReceiver<EventData> getNetworkReceiver () {
        return this.transceiver;
    }

    public boolean isInGame() {
        return this.gameController != null;
    }

    public void setGameController(GameController gameController) {
        this.gameController = gameController;
    }

    public boolean isAuthenticated () {
        return username != null;
    }

    public EventTransceiver getTransceiver () {
        return this.transceiver;
    }

    @Override
    public void broadcast(EventData data) {
        this.transceiver.broadcast(data);
    }
}
