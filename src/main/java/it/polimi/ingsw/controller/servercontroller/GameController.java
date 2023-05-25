package it.polimi.ingsw.controller.servercontroller;

import it.polimi.ingsw.controller.Response;
import it.polimi.ingsw.controller.db.DBManager;
import it.polimi.ingsw.event.EventTransceiver;
import it.polimi.ingsw.event.LocalEventTransceiver;
import it.polimi.ingsw.event.data.EventData;
import it.polimi.ingsw.event.data.VoidEventData;
import it.polimi.ingsw.event.data.game.*;
import it.polimi.ingsw.event.data.internal.GameHasBeenStoppedInternalEventData;
import it.polimi.ingsw.event.data.internal.GameOverInternalEventData;
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

/**
 * Class for managing a game.
 * To make games truly contemporary and take full advantage of multithreading,
 * the GameController class allows executing a set of instructions without passing
 * through the {@link MenuController MenuController}.
 * This is to avoid all game calls having to go
 * through the {@link MenuController MenuController} and thus slow down the entire server.
 * It is possible to perform atomic instructions on multiple functions of the
 * {@code GameController} by synchronizing on the Object returned by {@link GameController#getLock()},
 * which currently returns this, but, for portability reasons, it is always better
 * to rely on the {@link GameController#getLock()} function.
 * The GameController implements both a game session, with several players
 * connected within the game, and a lobby, where users can enter to restart or
 * start a game.
 * It is possible to enter directly into the game without going
 * through the lobby only if you were already part of the game, you are currently
 * in the menu, and there is someone already playing inside.
 *
 * @see Game
 * @see MenuController
 * @see EventTransceiver
 * @see LocalEventTransceiver
 *
 * @author Giacomo Groppi
 */
public class GameController {
    /**
     * Current game.
     */
    private final Game game;

    /**
     * Clients in game
     * */
    private final ArrayList<Pair<EventTransmitter, String>> clientsInGame;

    /**
     * Clients in lobby
     * */
    private final ArrayList<Pair<EventTransmitter, String>> clientsInLobby;

    /**
     * Events for which it's possible to avoid saving the game on disk.
     */
    private static final Set<String> eventIgnored = Set.of(
            PlayerHasJoinLobbyEventData.ID,
            PlayerHasDisconnectedEventData.ID,
            GameOverEventData.ID
    );

    /**
     * Uses a LocalEventTransceiver to implement the observer pattern for those who want to
     * stay updated on changes to the GameController.
     * The only event that is broadcast is {@link GameOverInternalEventData GameOverInternalEventData},
     * which is used to notify the {@link MenuController MenuController} that the game can be
     * completely removed from the list of games and from the disk.
     *
     * @see LocalEventTransceiver
     * @see MenuController
     */
    private final EventTransceiver internalTransceiver = new LocalEventTransceiver();

    /**
     * Constructs a new {@code GameController} object for the {@link Game game}.
     * 
     * @throws NullPointerException iff game is null
     *
     * @see Game
     * @see MenuController
     * */
    public GameController(Game game) {
        Objects.requireNonNull(game);

        this.game = game;
        this.clientsInGame = new ArrayList<>();
        this.clientsInLobby = new ArrayList<>();

        LocalEventTransceiver transceiver = new LocalEventTransceiver();

        game.setTransceiver(transceiver);

        transceiver.registerListener(event -> {
            synchronized (this) {
                clientsInGame.forEach(client -> client.getKey().broadcast(event));

                if (this.game.isStarted() && !eventIgnored.contains(event.getId()))
                    DBManager.getGamesDBManager().save(game);

                if (event.getId().equals(GameOverEventData.ID)) {
                    this.internalTransceiver.broadcast(new GameOverInternalEventData(this));
                    DBManager.getGamesDBManager().delete(game);
                    clientsInGame.clear();
                } else if (event.getId().equals(GameHasBeenStoppedEventData.ID)) {
                    this.internalTransceiver.broadcast(new GameHasBeenStoppedInternalEventData(this));
                }
            }
        });
    }

    /**
     * This function allows you to get the local transceiver,
     * on which only internal events will be sent.
     *
     * @return Internal transceiver
     */
    protected EventTransceiver getInternalTransceiver() {
        return this.internalTransceiver;
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
     *      <li> Player is not in this game </li>
     *      <li> The game has been stopped </li>
     *  </ul>
     * FAILURE otherwise
     *
     * @throws NullPointerException iff "username" is null.
     */
    public Response<VoidEventData> stopGame (String username) {
        Objects.requireNonNull(username);

        synchronized (this) {
            if (!game.containPlayer(username))
                return Response.failure("Player not in this game");

            try {
                game.stopGame(username);
                this.clientsInGame.clear();

                return Response.success("Game has been stopped");
            } catch (IllegalFlowException e) {
                return Response.failure("Game is not stopped");
            }
        }
    }

    /**
     * This method should be called if a set of atomic operations needs to be performed.
     * Since Java has a reentrant lock, if a function is called that acquires the lock
     * on this object, there is no problem of deadlock.
     *
     * @return The lock used internally by GameController.
     * */
    public Object getLock () {
        return this;
    }

    private void forEachInLobby(EventData event) {
        assert event != null;
        this.clientsInLobby.forEach(p -> p.getKey().broadcast(event));
    }

    /**
     * This method is used to check if the player with the given "username" is in the lobby.
     *
     * @param username The username to search for in the lobby.
     * @throws NullPointerException iff username is null
     * @return True if there exists a player with the given "username" in the lobby.
     */
    public synchronized boolean isInLobby(String username) {
        Objects.requireNonNull(username);
        return clientsInLobby.stream().anyMatch(p -> p.getValue().equals(username));
    }

    /**
     * This method is used to check if the player with the given "username" is in the game.
     *
     * @param username The username to search for in the game.
     * @throws NullPointerException iff username is null
     * @return True if there exists a player with the given "username" in the game.
     * */
    public synchronized boolean isInGame(String username) {
        Objects.requireNonNull(username);
        return clientsInGame.stream().anyMatch(p -> p.getValue().equals(username));
    }

    /**
     * This method removes a player from the lobby.
     * It requires that the player is in the lobby.
     *
     * @param username the username that wants to exit the lobby
     * @throws NullPointerException iff username is null
     * @return FAILURE iff username is not in lobby, SUCCESS otherwise
     */
    protected synchronized Response<VoidEventData> exitLobby(String username) {
        Objects.requireNonNull(username);

        Logger.writeMessage("Call for username: %s".formatted(username));
        if (!isInLobby(username)) return Response.failure("Not in lobby");

        removeFromList(username, clientsInLobby);

        forEachInLobby(new PlayerHasExitLobbyEventData(username));

        return Response.success("Success exit lobby");
    }

    /**
     * This function is used to check if the lobby is currently full.
     * @return true iff lobby is full
     */
    protected synchronized boolean isFull() {
        return clientsInLobby.size() == 4;
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
    protected synchronized Response<VoidEventData> exitGame (String username) {
        Objects.requireNonNull(username);
        Logger.writeMessage("Call for username: %s".formatted(username));

        if (!isInGame(username)) {
            return Response.failure("You are in the lobby");
        }

        removeFromList(username, this.clientsInGame);

        try {
            game.disconnectPlayer(username);

            if (game.isStopped()) {
                clientsInGame.clear();
                return Response.success("You have book remove from this game");
            }

            return Response.success("You have been remove from this game");
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
    public Response<VoidEventData> joinLobby(EventTransmitter newClient, String username) {
        Objects.requireNonNull(newClient);
        Objects.requireNonNull(username);

        synchronized (this) {
            if (isInGame(username) || isInLobby(username)) return Response.failure("You are in lobby or game");

            if (game.isStarted() && !game.isStopped())
                return Response.failure("This game is being playing");

            if (!game.isAvailableForJoin(username))
                return Response.failure("You can't join this game");

            clientsInLobby.forEach(c -> c.getKey().broadcast(new PlayerHasJoinLobbyEventData(username)));

            clientsInLobby.add(Pair.of(newClient, username));

            for (Pair<EventTransmitter, String> eventTransmitterStringPair : clientsInLobby)
                newClient.broadcast(new PlayerHasJoinLobbyEventData(eventTransmitterStringPair.getValue()));
        }

        return Response.success("You've joined the game");
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
    public Response<VoidEventData> selectTile(String username, Coordinate coordinate) {
        assert username != null;

        Logger.writeMessage("[%s] trying to select tile at %s in %s".formatted(username, coordinate, gameName()));

        synchronized (this) {
            if (!isInGame(username)) return Response.failure("Not in game");

            try {
                this.game.selectTile(username, coordinate);
            } catch (IllegalExtractionException | FullSelectionException | IllegalFlowException e) {
                return Response.failure(e.toString());
            }
            Logger.writeMessage("selected tile at " + coordinate + " in " + gameName());

            return Response.success("You've selected a tile");
        }
    }

    /**
     * This method deselects the coordinate from the selection.
     * The function requires that the username is in the game and not in the lobby.
     *
     * @param username The username that wants to deselect the Tiles from the game.
     * @param coordinate The {@link Coordinate coordinate} to remove from the selection.
     *
     * @return FAILURE iff
     *  <ul>
     *     <li> User is not in game </li>
     *     <li> forgetLastSelection create is an illegal operation </li>
     *  </ul>
     *  SUCCESS otherwise
     * @throws NullPointerException iff
     * <ul>
     *     <li> username is null </li>
     *     <li> coordinate is null </li>
     * </ul>
     * @see Game#forgetLastSelection(String, Coordinate)
     * @see Coordinate
     */
    public Response<VoidEventData> deselectTile(String username, Coordinate coordinate) {
        Objects.requireNonNull(username);
        Objects.requireNonNull(coordinate);

        Logger.writeMessage(username + " trying to deselect " + coordinate + " in " + gameName());

        synchronized (this) {
            if (!isInGame(username))
                return Response.failure("%s not in game".formatted(username));

            try {
                this.game.forgetLastSelection(username, coordinate);
            } catch (IllegalFlowException  | RemoveNotLastSelectedException | IllegalArgumentException e) {
                return Response.failure(e.toString());
            }

            return Response.success("You've deselected a tile");
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
    public Response<VoidEventData> insertSelectedTilesInBookshelf(String username, int column) {
        assert username != null;

        synchronized (this) {
            if (!isInGame(username))
                return Response.failure("Not in game");
            try {
                game.insertTile(username, column);

                return Response.success("Ok!");
            } catch (IllegalExtractionException | IllegalFlowException | NotEnoughSpaceInColumnException e) {
                return Response.failure("Failure during insertion of tiles into the bookshelf");
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
    protected synchronized Response<VoidEventData> restartGame (String username) {
        try {
            if (!isInLobby(username)) return Response.failure("%s not in lobby".formatted(username));

            game.restartGame(username, clientsInLobby.stream().map(Pair::getValue).toList());
            forEachInLobby(new GameHasStartedEventData());
            return Response.success("Ok!");
        } catch (IllegalFlowException e) {
            return Response.failure(e.getMessage());
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
     * @return FAILURE iff
     * <ul>
     *     <li> Game is not started </li>
     *     <li> Game is stopped </li>
     *     <li> Username is already connected </li>
     *     <li> Username is not in this game </li>
     * </ul>
     * SUCCESS otherwise
     */
    private synchronized Response<VoidEventData> reconnectUserDisconnect(String username, EventTransmitter transmitter) {
        try {
            Logger.writeMessage("Clients in lobby: " + clientsInLobby.stream().map(Pair::getValue).toList().toString());

            synchronized (game) {
                if (!game.isStarted())                  return Response.failure("The match hasn't started yet");
                if (game.isStopped())                   return Response.failure("Game is stopped");
                if (game.isPlayerConnected(username))   return Response.failure("%s already connected".formatted(username));
                if (!game.containPlayer(username))      return Response.failure("%s not in this game".formatted(username));

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

        return Response.success("You have joined the game");
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
    public synchronized Response<VoidEventData> rejoinGame(String username, EventTransmitter transmitter) {
        Objects.requireNonNull(username);
        Objects.requireNonNull(transmitter);

        if (isInLobby(username) || isInGame(username))
            return Response.failure("%s is in lobby or game".formatted(username));

        return reconnectUserDisconnect(username, transmitter);
    }

    /**
     * This method is used for join a user "username" into the game
     * It requires that the user is in the lobby and not in the game.
     *
     * @param username the username of the user that wants to join the game
     * @throws NullPointerException if username is null
     * @return FAILURE iff
     * <ul>
     *     <li> username is not in lobby </li>
     *     <li> reconnectUserDisconnect return a FAILURE </li>
     * </ul>
     * SUCCESS otherwise
     */
    public synchronized Response<VoidEventData> joinGame (String username) {
        Objects.requireNonNull(username);
        Logger.writeMessage("Clients in lobby before modify: " + clientsInLobby.stream().map(Pair::getValue).toList().toString());

        for (int i = 0; i < clientsInLobby.size(); i++) {
            if (clientsInLobby.get(i).getValue().equals(username)) {
                Pair<EventTransmitter, String> client = clientsInLobby.get(i);

                Response<VoidEventData> response = reconnectUserDisconnect(client.getValue(), client.getKey());

                if (response.isOk()) {
                    clientsInLobby.remove(i);
                }

                return response;
            }
        }

        return Response.failure("Player not in lobby");
    }

    /**
     * This method is used for start the game
     * It requires that the game is not started and that there are at least 2 players in the lobby
     * All the players in the lobby are added to the game and the lobby is cleared
     *
     * @return FAILURE iff
     * <ul>
     *     <li> Username is not in lobby </li>
     *     <li> Game is already started </li>
     *     <li> Username is not the owner </li>
     * </ul>
     * SUCCESS otherwise
     */
    protected synchronized Response<VoidEventData> startGame(String username) {
        if (!isInLobby(username))
            return Response.failure("%s not in lobby".formatted(username));

         // add player to game
        try {
            for (Pair<EventTransmitter, String> client: clientsInLobby) {
                game.addPlayer(client.getValue());
            }
        } catch (IllegalFlowException | PlayerAlreadyInGameException e) {
            assert game.isStarted();
            return Response.failure(e.getMessage());
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
            return Response.failure(e.getMessage());
        }

        forEachInLobby(new GameHasStartedEventData());

        return Response.success("The game has started");
    }

    /**
     * @return true if the game is started, false otherwise
     */
    public synchronized boolean isStopped() {
        return game.isStopped();
    }

    private void removeFromList (String username, ArrayList<Pair<EventTransmitter, String>> list) {
        Objects.requireNonNull(username);
        Objects.requireNonNull(list);

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getValue().equals(username)) {
                list.remove(i);
                return;
            }
        }

    }

    /**
     * This method is used for disconnect a user "username" from the game or from the lobby.
     * It doesn't return anything as the method cannot fail.
     *
     * @param username the username of the user that wants to disconnect
     */
    public synchronized void disconnect(String username) {
        try {
            if (isInGame(username)) {
                game.disconnectPlayer(username);

                removeFromList(username, clientsInGame);
            } else {
                removeFromList(username, clientsInLobby);

                forEachInLobby(new PlayerHasExitLobbyEventData(username));
            }
        } catch (IllegalFlowException e) {
            throw new IllegalStateException();
        }

        if (game.isStopped()) {
            clientsInGame.clear();
        }
    }

    /**
     * @return true iff username can join the game
     * @param username the username of the user that wants to join the game
     * @throws NullPointerException iff username is null
     */
    public synchronized boolean isAvailableForJoin (String username) {
        Objects.requireNonNull(username);
        return game.isAvailableForJoin(username);
    }

    /**
     * It's thread safe because the name of the game is immutable
     * @return the name of the game
     * */
    public String gameName () {
        return this.game.getName();
    }
}
