package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.db.DBManager;
import it.polimi.ingsw.event.LocalEventTransceiver;
import it.polimi.ingsw.event.data.EventData;
import it.polimi.ingsw.event.data.game.*;
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

    private final List<String> eventIgnored = new ArrayList<>();

    public GameController(Game game) {
        this.game = game;
        this.clientsInGame = new ArrayList<>();
        this.clientsInLobby = new ArrayList<>();

        eventIgnored.add(PlayerHasJoinGameEventData.ID);
        eventIgnored.add(PlayerHasDisconnectedEventData.ID);
        eventIgnored.add(CommonGoalCompletedEventData.ID);

        LocalEventTransceiver transceiver = new LocalEventTransceiver();

        game.setTransceiver(transceiver);

        transceiver.registerListener(event -> {
                    clientsInGame.forEach(client -> client.getKey().broadcast(event));

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

    private synchronized void removePlayer(String username) {
        for (int i = 0; i < clientsInGame.size(); i++) {
            if (clientsInGame.get(i).getValue().equals(username)){
                clientsInGame.remove(i);
                return;
            }
        }
        assert false;
    }

    protected synchronized Response exitGame (String username) {
        Logger.writeMessage("Call for username: %s".formatted(username));

        if (!isInGame(username)) {
            return new Response("You are in the lobby", ResponseStatus.FAILURE);
        }

        removePlayer(username);

        try {
            game.disconnectPlayer(username);

            if (game.isStopped()) {
                clientsInGame.clear();
                return new Response("You have book remove from this game", ResponseStatus.SUCCESS);
            }

            return new Response("You have been remove from this game", ResponseStatus.SUCCESS);
        } catch (IllegalFlowException e) {
            Logger.writeCritical("call " + e.getMessage());
            throw new IllegalStateException("Player already disconnected " + e.getMessage());
        }
    }

    public Response joinLobby(EventTransmitter newClient, String username) {
        if (newClient == null || username == null) {
            throw new NullPointerException();
        }

        synchronized (this) {
            if (game.isStarted() && !game.isStopped())
                return new Response("This game is being playing", ResponseStatus.FAILURE);

            if (!game.isAvailableForJoin(username))
                return new Response("You can't join this game", ResponseStatus.FAILURE);

            clientsInLobby.forEach(c -> c.getKey().broadcast(new PlayerHasJoinLobbyEventData(username)));

            clientsInLobby.add(Pair.of(newClient, username));

            for (Pair<EventTransmitter, String> eventTransmitterStringPair : clientsInLobby)
                newClient.broadcast(new PlayerHasJoinLobbyEventData(eventTransmitterStringPair.getValue()));
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
            if (clientsInLobby.size() < 2) {
                return new Response("You can't restart a game with only one player connected", ResponseStatus.FAILURE);
            }

            game.setPlayersToWait(clientsInLobby.stream().map(Pair::getValue).toList());
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

    /**
     * utilizzato per joinare senza
     * */
    public synchronized Response rejoinGame(String username, EventTransmitter transmitter) {
        try {
            // dobbiamo inviargli gli eventi qua dentro altrimenti possiamo perdere delle informazioni
            // su game rilasciando il lock e riprendendolo

            Logger.writeMessage("Clients in lobby: " + clientsInLobby.stream().map(Pair::getValue).toList().toString());

            synchronized (game) {
                if (!game.isStarted() || game.isStopped() || !game.containPlayer(username) || game.isPlayerConnected(username))
                    return new Response("[4] Game is stopped!", ResponseStatus.FAILURE);

                clientsInGame.add(Pair.of(transmitter, username));

                Logger.writeMessage("Added new user %s; new size: %d".formatted(username, clientsInGame.size()));

                GameView view = getGameView();
                final int personalGoal = getPersonalGoal(username);

                transmitter.broadcast(new InitialGameEventData(view));
                transmitter.broadcast(new PersonalGoalSetEventData(personalGoal));

                game.connectPlayer(username);
            }
        } catch (IllegalFlowException | PlayerAlreadyInGameException | PlayerNotInGameException e) {
            throw new IllegalStateException("[5]");
        }

        return new Response("You have joined the game", ResponseStatus.SUCCESS);
    }

    /**
     * transmitter va passato null se la virtualview è nella lobby
     * */
    public synchronized Response joinGame (String username) {
         Logger.writeMessage("Clients in lobby: " + clientsInLobby.stream().map(Pair::getValue).toList().toString());

         for (int i = 0; i < clientsInLobby.size(); i++) {
             if (clientsInLobby.get(i).getValue().equals(username)) {
                 Pair<EventTransmitter, String> client = clientsInLobby.get(i);

                 Response response = this.rejoinGame(client.getValue(), client.getKey());

                 if (response.isOk()) {
                     clientsInLobby.remove(i);
                 }

                 return response;
             }
         }

        return new Response("Player not in lobby", ResponseStatus.FAILURE);
    }

    public synchronized Response startGame(String username) {
            try {
                for (Pair<EventTransmitter, String> client: clientsInLobby) {
                    game.addPlayer(client.getValue());
                }
            } catch (IllegalFlowException | PlayerAlreadyInGameException e) {
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
                clientsInGame.clear();
            }
        }
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
