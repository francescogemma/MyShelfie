package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.db.DBManager;
import it.polimi.ingsw.event.LocalEventTransceiver;
import it.polimi.ingsw.event.data.EventData;
import it.polimi.ingsw.event.data.game.*;
import it.polimi.ingsw.event.data.internal.ForceExitGameEventData;
import it.polimi.ingsw.event.receiver.EventReceiver;
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

    private final List<Pair<EventTransmitter, String>> clientsInGame;
    private final List<Pair<EventTransmitter, String>> clientsInLobby;

    private final LocalEventTransceiver transceiver = new LocalEventTransceiver();
    private final List<String> eventIgnored = new ArrayList<>();

    public GameController(Game game) {
        this.game = game;
        this.clientsInGame = new ArrayList<>();
        this.clientsInLobby = new ArrayList<>();

        eventIgnored.add(PlayerHasJoinGameEventData.ID);
        eventIgnored.add(PlayerHasDisconnectedEventData.ID);
        eventIgnored.add(CommonGoalCompletedEventData.ID);

        game.setTransceiver(transceiver);

        transceiver.registerListener(event -> {
                    clientsInGame.forEach(
                        client -> {
                            client.getKey().broadcast(event);
                        }
                    );

            Logger.writeMessage(event.getId());
            if (this.game.isStarted() && !eventIgnored.contains(event.getId()))
                DBManager.getGamesDBManager().save(game);
        });
    }

    public int getPersonalGoal(String username) {
        return game.getPersonalGoal(username);
    }

    public Response stopGame (String username) {
        synchronized (this) {
            if (!game.containPlayer(username))
                throw new IllegalArgumentException();

            if (game.stopGame(username)) {
                this.clientsInGame.clear();
                transceiver.broadcast(new ForceExitGameEventData(username));
                return new Response("Game has been successfully paused", ResponseStatus.SUCCESS);
            }
            return new Response("You are not the owner", ResponseStatus.FAILURE);
        }
    }

    private void forEachInLobby(EventData event) {
        assert event != null;
        this.clientsInLobby.forEach(p -> p.getKey().broadcast(event));
    }

    public synchronized boolean isInLobby(String username) {
        return clientsInLobby.stream().map(Pair::getValue).toList().contains(username);
    }

    public synchronized boolean isInGame(String username) {
        return clientsInGame.stream().map(Pair::getValue).toList().contains(username);
    }

    protected Response exitLobby(String username) {
        Logger.writeMessage("Call for username: %s".formatted(username));
        synchronized (this) {
            if (!clientsInLobby.stream().map(Pair::getValue).toList().contains(username)) {
                return new Response("Client not in lobby", ResponseStatus.FAILURE);
            }

            for (int i = 0; i < clientsInLobby.size(); i++) {
                if (clientsInLobby.get(i).getValue().equals(username)) {
                    clientsInLobby.remove(i);
                    break;
                }
            }

            forEachInLobby(new PlayerHasExitLobbyEventData(username));

            return new Response("Success exit lobby", ResponseStatus.SUCCESS);
        }
    }

    protected Response exitGame (String username) {
        Logger.writeMessage("Call for username: %s".formatted(username));
        synchronized (this) {
            if (!isInGame(username)) {
                return new Response("You are in the lobby", ResponseStatus.FAILURE);
            }

            try {
                game.disconnectPlayer(username);

                if (game.isStopped()) {
                    this.transceiver.broadcast(new ForceExitGameEventData(username));
                    clientsInGame.clear();
                    return new Response("You have book remove from this game", ResponseStatus.SUCCESS);
                }

                for (int i = 0; i < this.clientsInGame.size(); i++) {
                    if (clientsInGame.get(i).getValue().equals(username)) {
                        clientsInGame.remove(i);
                        return new Response("You have been remove from this game", ResponseStatus.SUCCESS);
                    }
                }
                Logger.writeCritical("Game has this player but i don't");
                assert false;
            } catch (IllegalFlowException e) {
                Logger.writeCritical("call " + e.getMessage());
                throw new IllegalStateException("Player already disconnected " + e.getMessage());
            }

            throw new IllegalStateException("Player not in this game!!!");
        }
    }

    public Response joinLobby(EventTransmitter newClient, String username) {
        if (newClient == null || username == null) {
            throw new NullPointerException();
        }

        synchronized (this) {
            if (!game.isAvailableForJoin(username))
                return new Response("You can't join this game", ResponseStatus.FAILURE);

            clientsInLobby.add(Pair.of(newClient, username));

            for (int i = 0; i < clientsInLobby.size() - 1; i++) {
                newClient.broadcast(new PlayerHasJoinLobbyEventData(clientsInLobby.get(i).getValue()));
            }
        }

        return new Response("You've joined the game", ResponseStatus.SUCCESS);
    }

    protected GameView getGameView () {
        return this.game.createView();
    }

    public Response selectTile(String username, Coordinate coordinate) {
        assert username != null;
        Logger.writeMessage(username + " trying to select tile at " + coordinate + " in " + gameName());

        synchronized (this) {
            try {
                this.game.selectTile(username, coordinate);
            } catch (IllegalExtractionException | FullSelectionException | IllegalFlowException e) {
                return new Response(e.toString(), ResponseStatus.FAILURE);
            }
            Logger.writeMessage("selected tile at " + coordinate + " in " + gameName());

            return new Response("You've selected a tile", ResponseStatus.SUCCESS);
        }
    }

    public Response deselectTile(String username, Coordinate coordinate) {
        assert username != null;
        Logger.writeMessage(username + " trying to deselect " + coordinate + " in " + gameName());

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
                game.insertTile(username, column);

                return new Response("Ok!", ResponseStatus.SUCCESS);
            } catch (IllegalExtractionException | IllegalFlowException | NotEnoughSpaceInColumnException e) {
                return new Response("Failure during insertion of tiles into the bookshelf",
                    ResponseStatus.FAILURE);
            }
        }
    }

    public synchronized String getOwner() {
        return this.game.getOwner();
    }

    public synchronized Response restartGame (String username) {
        try {
            game.restartGame(username);
            forEachInLobby(new GameHasStartedEventData());
            return new Response("ok", ResponseStatus.SUCCESS);
        } catch (IllegalFlowException e) {
            return new Response("You can't restart the game", ResponseStatus.FAILURE);
        }
    }

    public synchronized int getNumberOfPlayerInLobby() {
        return this.clientsInLobby.size();
    }

    public synchronized Response joinGame (String username) {
        assert isInLobby(username);

        try {
            game.connectPlayer(username);

            for (int i = 0; i < clientsInLobby.size(); i++) {
                if (clientsInLobby.get(i).getValue().equals(username)) {
                    clientsInGame.add(clientsInLobby.remove(i));
                    break;
                }
            }

            return new Response("You have reconnect", ResponseStatus.SUCCESS);
        } catch (IllegalFlowException | PlayerAlreadyInGameException e) {
            return new Response("You can't reconnect to this game", ResponseStatus.FAILURE);
        }
    }

    public Response startGame(String username) {
        synchronized (this) {
            try {
                for (Pair<EventTransmitter, String> client: clientsInLobby) {
                    game.addPlayer(client.getValue());
                }
            } catch (IllegalFlowException e) {
                assert game.isStarted();
                return new Response(e.getMessage(), ResponseStatus.FAILURE);
            }

            try {
                game.startGame(username);
            } catch (IllegalFlowException e) {
                for (Pair<EventTransmitter, String> client: clientsInLobby) {
                    try {
                        game.removePlayer(client.getValue());
                    } catch (IllegalFlowException ignore) {
                        Logger.writeCritical("call");
                        assert false;
                    }
                }
                return new Response(e.getMessage(), ResponseStatus.FAILURE);
            }

            forEachInLobby(new GameHasStartedEventData());

            return new Response("The game has started", ResponseStatus.SUCCESS);
        }
    }

    public synchronized boolean isStopped() {
        return game.isStopped();
    }

    /**
     * @throws IllegalArgumentException iff the player is not in this game.
     * */
    public void disconnect(String username) {
        synchronized (this) {
            try {
                if (isInGame(username)) {
                    game.disconnectPlayer(username);

                    for (int i = 0; i < clientsInGame.size(); i++) {
                        if (clientsInGame.get(i).getValue().equals(username)) {
                            clientsInGame.remove(i);
                            break;
                        }
                    }
                } else {
                    for (int i = 0; i < clientsInLobby.size(); i++) {
                        if (clientsInLobby.get(i).getValue().equals(username)) {
                            clientsInLobby.remove(i);
                            break;
                        }
                    }

                    forEachInLobby(new PlayerHasExitLobbyEventData(username));
                }
            } catch (IllegalFlowException e) {
                throw new IllegalStateException();
            }

            if (game.isStopped()) {
                transceiver.broadcast(new ForceExitGameEventData(username));
                clientsInGame.clear();
            }
        }
    }

    public EventReceiver<EventData> getInternalReceiver() {
        return this.transceiver;
    }

    /**
     * @return true iff game is not started or game has at least one player disconnected
     * */
    public boolean isAvailableForJoin (String username) {
        synchronized (this) {
            return game.isAvailableForJoin(username);
        }
    }

    public String gameName () {
        return this.game.getName();
    }
}
