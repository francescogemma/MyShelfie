package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.db.DBManager;
import it.polimi.ingsw.event.EventTransceiver;
import it.polimi.ingsw.event.LocalEventTransceiver;
import it.polimi.ingsw.event.data.EventData;
import it.polimi.ingsw.event.data.client.DeselectTileEventData;
import it.polimi.ingsw.event.data.client.InsertTileEventData;
import it.polimi.ingsw.event.data.client.SelectTileEventData;
import it.polimi.ingsw.event.data.client.StartGameEventData;
import it.polimi.ingsw.event.data.game.*;
import it.polimi.ingsw.event.transmitter.EventTransmitter;
import it.polimi.ingsw.model.board.FullSelectionException;
import it.polimi.ingsw.model.board.IllegalExtractionException;
import it.polimi.ingsw.model.board.RemoveNotLastSelectedException;
import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.model.game.IllegalFlowException;
import it.polimi.ingsw.model.game.PlayerAlreadyInGameException;
import it.polimi.ingsw.utils.Coordinate;
import it.polimi.ingsw.utils.Pair;

// events

import java.beans.Transient;
import java.util.*;

public class GameController {
    private final Game game;
    private final List<Pair<EventTransmitter, String>> clients;

    public GameController(Game game) {
        this.game = game;
        this.clients = new ArrayList<>();

        final LocalEventTransceiver transceiver = new LocalEventTransceiver();
        game.setTransceiver(transceiver);

        transceiver.registerListener(event ->
                clients.forEach(
                        client -> {
                            client.getKey().broadcast(event);
                            synchronized (this) {
                                // TODO --> don't pass game, pass a GameView
                                if (this.game.isStarted())
                                    DBManager.getGamesDBManager().save(game);
                            }
                        }
                )
        );
    }

    private void broadcastForEachView (EventData data) {
        this.clients.forEach(client -> client.getKey().broadcast(data));
    }

    public Response join(EventTransmitter newClient, String username) {
        if (newClient == null || username == null) {
            throw new NullPointerException();
        }

        synchronized (this) {
            clients.add(Pair.of(newClient, username));

            try {
                game.addPlayer(username);
            } catch (IllegalFlowException | PlayerAlreadyInGameException e) {
                clients.remove(clients.size() - 1);
                return new Response(e.toString(), ResponseStatus.FAILURE);
            }

            for (int i = 0; i < clients.size() - 1; i++) {
                newClient.broadcast(new PlayerHasJoinEventData(clients.get(i).getValue()));
            }
        }

        return new Response("You've joined the game", ResponseStatus.SUCCESS);
    }

    public Response selectTile(String username, Coordinate coordinate) {
        assert username != null;
        synchronized (this) {
            try {
                this.game.selectTile(username, coordinate);
            } catch (IllegalExtractionException | FullSelectionException | IllegalFlowException e) {
                return new Response(e.toString(), ResponseStatus.FAILURE);
            }

            return new Response("You've selected a tile", ResponseStatus.SUCCESS);
        }
    }

    public Response deselectTile(String username, Coordinate coordinate) {
        assert username != null;
        synchronized (this) {
            try {
                this.game.forgetLastSelection(username, coordinate);
            } catch (IllegalFlowException  | RemoveNotLastSelectedException | IllegalArgumentException e) {
                return new Response(e.toString(), ResponseStatus.FAILURE);
            }

            return new Response("You've deselected a tile", ResponseStatus.SUCCESS);
        }
    }

    public Response insertSelectedTilesInBookshelf(String username, int column) {
        assert username != null;
        synchronized (this) {
            try {
                this.game.insertTile(username, column);
                return new Response("Ok!", ResponseStatus.SUCCESS);
            } catch (IllegalExtractionException | IllegalFlowException e) {
                return new Response(e.getMessage(), ResponseStatus.FAILURE);
            }
        }
    }

    public Response startGame(String username) {
        synchronized (this) {
            try {
                if (!game.canStartGame(username)) {
                    return new Response("Only the starting player: " + game.getStartingPlayer().getUsername() +
                        " can start the game", ResponseStatus.FAILURE);
                }
            } catch (IllegalFlowException e) {
                return new Response(e.getMessage(), ResponseStatus.FAILURE);
            }

            try {
                game.startGame();
            } catch (IllegalFlowException e) {
                return new Response(e.getMessage(), ResponseStatus.FAILURE);
            }

            return new Response("The game has started", ResponseStatus.SUCCESS);
        }
    }

    public Response disconnect(String username) {
        synchronized (this) {
            try {
                this.game.disconnectPlayer(username);
            } catch (IllegalArgumentException e) {
                return new Response("You're not connected to the game", ResponseStatus.FAILURE);
            }

            for (int i = 0; i < clients.size(); i++) {
                if (this.clients.get(i).getValue().equals(username)) {
                    this.clients.remove(i);
                    return new Response("You're now disconnected to the game", ResponseStatus.SUCCESS);
                }
            }

            return new Response("You're not connected to the game", ResponseStatus.FAILURE);
        }
    }

    /**
     * @return true iff game is not started or game has at least one player disconnected
     * */
    public boolean isAvailableForJoin () {
        synchronized (this) {
            return !this.game.isStarted() || this.game.hasPlayerDisconnected();
        }
    }

    public String gameName () {
        return this.game.getName();
    }
}
