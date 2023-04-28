package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.db.DBManager;
import it.polimi.ingsw.event.LocalEventTransceiver;
import it.polimi.ingsw.event.data.EventData;
import it.polimi.ingsw.event.data.game.*;
import it.polimi.ingsw.event.transmitter.EventTransmitter;
import it.polimi.ingsw.model.board.FullSelectionException;
import it.polimi.ingsw.model.board.IllegalExtractionException;
import it.polimi.ingsw.model.board.RemoveNotLastSelectedException;
import it.polimi.ingsw.model.bookshelf.NotEnoughSpaceInColumnException;
import it.polimi.ingsw.model.game.*;
import it.polimi.ingsw.utils.Coordinate;
import it.polimi.ingsw.utils.Logger;
import it.polimi.ingsw.utils.Pair;

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
                                if (this.game.isStarted())
                                    DBManager.getGamesDBManager().save(game);
                            }
                        }
                )
        );
    }

    public int getPersonalGoal(String username) {
        return game.getPersonalGoal(username);
    }

    public boolean containerPlayer(String username) {
        if (username == null)
            throw new NullPointerException();
        synchronized (this) {
            return this.game.containPlayer(username);
        }
    }

    public Response stopGame (String username) {
        synchronized (this) {
            try {
                if (!game.containPlayer(username))
                    throw new IllegalArgumentException();

                if (game.stopGame(username)) {
                    this.clients.clear();
                    return new Response("Game has been successfully paused", ResponseStatus.SUCCESS);
                }
                return new Response("You are not the owner", ResponseStatus.FAILURE);
            } catch (NoPlayerConnectedException e) {
                Logger.writeCritical("It's not possible one player ask me to stop a game and he is disconnected");
                return new Response("We have and internal problem", ResponseStatus.FAILURE);
            }
        }
    }

    protected Response exitGame (String username) {
        synchronized (this) {
            try {
                game.removePlayer (username);

                for (int i = 0; i < this.clients.size(); i++) {
                    if (clients.get(i).getValue().equals(username)) {
                        clients.remove(i);
                        return new Response("You have been remove from this game", ResponseStatus.SUCCESS);
                    }
                }
                Logger.writeCritical("Game has this player but i don't");
                assert false;
            } catch (IllegalFlowException e) {
                return new Response(e.getMessage(), ResponseStatus.FAILURE);
            }

            return new Response("Player not in this game", ResponseStatus.FAILURE);
        }
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

    protected GameView getGameView () {
        return this.game.createView();
    }

    public Response selectTile(String username, Coordinate coordinate) {
        assert username != null;
        synchronized (this) {
            Logger.writeMessage(username + " trying to select tile at " + coordinate + " in " + gameName());
            try {
                this.game.selectTile(username, coordinate);
            } catch (IllegalExtractionException | FullSelectionException | IllegalFlowException e) {
                return new Response(e.toString(), ResponseStatus.FAILURE);
            }
            Logger.write(Logger.Type.MESSAGE, " selected tile at " + coordinate + " in " + gameName());

            return new Response("You've selected a tile", ResponseStatus.SUCCESS);
        }
    }

    public Response deselectTile(String username, Coordinate coordinate) {
        assert username != null;
        Logger.write(Logger.Type.MESSAGE, username + " trying to deselect " + coordinate + " in " + gameName());
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
            } catch (IllegalExtractionException | IllegalFlowException | NotEnoughSpaceInColumnException e) {
                return new Response("Failure during insertion of tiles into the bookshelf",
                    ResponseStatus.FAILURE);
            }
        }
    }

    public Response startGame(String username) {
        synchronized (this) {
            try {
                game.startGame(username);
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
