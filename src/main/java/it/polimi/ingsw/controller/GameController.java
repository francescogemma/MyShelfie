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
import it.polimi.ingsw.model.goal.PersonalGoal;

import java.util.*;

public class GameController {
    private final Game game;

    private final ArrayList<Pair<EventTransmitter, String>> clientsInGame;
    private final ArrayList<Pair<EventTransmitter, String>> clientsInLobby;

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

    /**
     * This method returns the index of the {@link PersonalGoal personalGoal} of the player "username".
     *
     * @return The index of the {@link PersonalGoal personalGoal} of the player "username".
     * @throws NullPointerException iff "username" is null.
     *
     * @see PersonalGoal
     * @see it.polimi.ingsw.model.goal.Goal
     */
    public synchronized int getPersonalGoal(String username) {
        Objects.requireNonNull(username);
        return game.getPersonalGoal(username);
    }

    /**
     * This method stops the game.
     *
     * @param username The username that wants to stop the game.
     * @return SUCCESS iff
     *  <ul>
     *      <li> The game has been stopped </li>
     *  </ul>
     * FAILURE otherwise
     *
     * @throws NullPointerException iff "username" is null.
     */
    public Response stopGame (String username) {
        Objects.requireNonNull(username);

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

    /**
     * This method is used to check if the player with the given "username" is in the lobby.
     *
     * @param username The username to search for in the lobby.
     * @return True if there exists a player with the given "username" in the lobby.
     */
    public synchronized boolean isInLobby(String username) {
        Objects.requireNonNull(username);
        return clientsInLobby.stream().map(Pair::getValue).toList().contains(username);
    }

    /**
     * This method is used to check if the player with the given "username" is in the game.
     *
     * @param username The username to search for in the game.
     * @return True if there exists a player with the given "username" in the game.
     * */
    public synchronized boolean isInGame(String username) {
        return clientsInGame.stream().map(Pair::getValue).toList().contains(username);
    }

    /**
     * This method removes a player from the lobby.
     * It requires that the player is in the lobby.
     *
     * @param username the username that wants to exit the lobby
     * @return Always SUCCESS
     */
    public Response exitLobby(String username) {
        Objects.requireNonNull(username);
        assert isInLobby(username);

        Logger.writeMessage("Call for username: %s".formatted(username));
        synchronized (this) {
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

    /**
     * This method removes the player with the given username from the game, marking them as disconnected if the game has already started.
     *
     * @param username the username of the player who wants to exit the game
     * @return FAILURE iff
     * <ul>
     *     <li>The player is not in the game</li>
     * </ul>
     * SUCCESS otherwise
     *
     * @throws NullPointerException iff username is null
     */
    protected synchronized Response exitGame (String username) {
        Objects.requireNonNull(username);
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

    /**
     * This method is used to add a username to the lobby.
     * It requires that the username is not already in game or in the lobby.
     *
     * @param username the username of the player who wants to enter the lobby
     * @param newClient the {@link EventTransmitter transmitter} of the user who wants to enter the lobby
     *
     * @throws NullPointerException iff
     * <ul>
     *     <li>newClient is null</li>
     *     <li>username is null</li>
     * </ul>
     *
     * @return FAILURE iff
     * <ul>
     *     <li>The game has started and is not stopped</li>
     *     <li>Username cannot enter the game</li>
     * </ul>
     * SUCCESS otherwise
     */
    public Response joinLobby(EventTransmitter newClient, String username) {
        assert !isInGame(username) && !isInLobby(username);
        Objects.requireNonNull(newClient);
        Objects.requireNonNull(username);

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

    /**
     * Use this method to get the view of the game.
     * This method can never fail.
     *
     * @return An immutable {@link GameView gameView} equal to the current game
     *
     * @see Game#createView()
     * @see GameView
     */
    protected synchronized GameView getGameView () {
        return this.game.createView();
    }

    /**
     * This method selects a tile from the board for the user "username".
     *
     * @param coordinate The {@link Coordinate coordinate} to select the tile for.
     * @param username The username of the user who wants to select a tile in the game.
     *
     * @return SUCCESS iff
     *  <ul>
     *   <li> The selection of the tile in the game was successful </li>
     *  </ul>
     *  otherwise FAILURE.
     *
     * @see Game#selectTile(String, Coordinate)
     * @see Coordinate
     */
    public Response selectTile(String username, Coordinate coordinate) {
        assert username != null;
        assert !isInLobby(username) && isInGame(username);

        Logger.writeMessage("[%s] trying to select tile at %s in %s".formatted(username, coordinate, gameName()));

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

    /**
     * This method deselects the coordinate from the selection.
     * The function requires that the username is in the game and not in the lobby.
     *
     * @param username The username that wants to deselect the Tiles from the game.
     * @param coordinate The {@link Coordinate coordinate} to remove from the selection.
     *
     * @return SUCCESS iff
     *  <ul>
     *   <li> The deselect operation in the game for the username "username" was successful </li>
     *  </ul>
     *  otherwise FAILURE.
     *
     * @see Game#forgetLastSelection(String, Coordinate)
     * @see Coordinate
     */
    public Response deselectTile(String username, Coordinate coordinate) {
        assert username != null;
        assert isInGame(username) && !isInLobby(username);
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

    /**
     * This method inserts the selection of Tiles into the bookshelf.
     *
     * @param username The username of the player who wants to add the selected set of tiles to the bookshelf in the column.
     * @param column The column where the Tiles should be inserted.
     * @return SUCCESS iff
     *  <ul>
     *      <li> The insertion of Tiles into the game was successful </li>
     *  </ul>
     * FAILURE otherwise.
     */
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

    /**
     * This method returns the owner of the game.
     * It does not need to be synchronized as the corresponding object in Game is immutable and final.
     *
     * @return The owner of the game.
     */
    public String getOwner() {
        return this.game.getOwner();
    }

    /**
     * This method is used to restart a paused game.
     *
     * @return A FAILURE response if
     *  <ul>
     *       <li> The number of players is less than 2 </li>
     *       <li> The game is not stopped </li>
     *       <li> username cannot restart the game </li>
     *  </ul>
     *  Returns SUCCESS otherwise
     *
     * @param username The username of the player who wants to restart the game.
     */
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

    /**
     * Use this method for get the number of players in the lobby
     * @return the number of players in the lobby
     * */
    public synchronized int getNumberOfPlayerInLobby() {
        return this.clientsInLobby.size();
    }

    /**
     * use this method for reconnect a user "username" into the game
     * this method adds "transmitter" to clientsInGame
     *
     * @param username the username of the user to reconnect
     * @param transmitter the transmitter of the user to reconnect
     *
     * @return a response with the result of the operation
     */
    private synchronized Response reconnectUserDisconnect(String username, EventTransmitter transmitter) {
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
     * This method is used for reconnect a user "username" into the game
     * It requires that the user is not in the lobby and not in game.
     *
     * @param username the username of the user to reconnect
     * @param transmitter the transmitter of the user to reconnect
     *
     * @throws NullPointerException if username or transmitter is null
     * @return a response with the result of the operation
     */
    public synchronized Response rejoinGame(String username, EventTransmitter transmitter) {
        Objects.requireNonNull(username);
        Objects.requireNonNull(transmitter);

        assert !isInLobby(username);
        assert !isInGame(username);

        return reconnectUserDisconnect(username, transmitter);
    }

    /**
     * This method is used for join a user "username" into the game
     * It requires that the user is in the lobby and not in game.
     *
     * @param username the username of the user that wants to join the game
     * @throws NullPointerException if username is null
     */
    public synchronized Response joinGame (String username) {
        assert isInLobby(username);
        assert !isInGame(username);

        Objects.requireNonNull(username);
        Logger.writeMessage("Clients in lobby: " + clientsInLobby.stream().map(Pair::getValue).toList().toString());

        for (int i = 0; i < clientsInLobby.size(); i++) {
            if (clientsInLobby.get(i).getValue().equals(username)) {
                Pair<EventTransmitter, String> client = clientsInLobby.get(i);

                Response response = reconnectUserDisconnect(client.getValue(), client.getKey());

                if (response.isOk()) {
                    clientsInLobby.remove(i);
                }

                return response;
            }
        }

        return new Response("Player not in lobby", ResponseStatus.FAILURE);
    }

    /**
     * This method is used for start the game
     * It requires that the game is not started and that there are at least 2 players in the lobby
     * All the players in the lobby are added to the game and the lobby is cleared
     *
     * @return a response with the result of the operation. The result is SUCCESS if the game is started, FAILURE otherwise
     */
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

    /**
     * @return true if the game is started, false otherwise
     */
    public synchronized boolean isStopped() {
        return game.isStopped();
    }

    /**
     * This method is used for disconnect a user "username" from the game or from the lobby.
     * It doesn't return anything as the method cannot fail.
     *
     * @param username the username of the user that wants to disconnect
     */
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
     * @return true iff username can join the game
     * @param username the username of the user that wants to join the game
     */
    public boolean isAvailableForJoin (String username) {
        synchronized (this) {
            return game.isAvailableForJoin(username);
        }
    }

    /**
     * It's thread safe because the name of the game is immutable
     * @return the name of the game
     * */
    public String gameName () {
        return this.game.getName();
    }
}
