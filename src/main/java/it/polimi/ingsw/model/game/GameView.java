package it.polimi.ingsw.model.game;

import it.polimi.ingsw.controller.db.Identifiable;
import it.polimi.ingsw.model.bag.Bag;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.board.BoardView;
import it.polimi.ingsw.model.goal.CommonGoal;
import it.polimi.ingsw.utils.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Class containing all the getters for the Game class.
 * Objects of type GameView do not contain sensitive information, such as
 * personal objectives, and can be sent over the network
 * after calling the {@link #createView()} function.
 * GameView objects created with the {@link #createView()} command are immutable.
 *
 * @see Game
 * @author Giacomo Groppi
 * */
public class GameView implements Identifiable {
    /*
     * The index of the first player in the list of players.
     */
    protected static final int FIRST_PLAYER_INDEX = 0;

    /*
     * Time after which, if the game is still waiting for reconnection, it is terminated, assigning the connected player as the winner.
     */
    protected static final int TIME_WAITING_FOR_RECONNECTIONS_BEFORE_WIN = 60 * 1000;

    /*
     * Time after which, if the first player is not connected, he lost his turn
     */
    protected static final int TIME_FIRST_PLAYER_CONNECT = 10 * 1000;

    /*
     * The name of the game.
     */
    protected final String name;

    /*
     * The optional winner of the game.
     */
    protected final List<Player> winners;

    /*
     * The common goals of the game.
     */
    protected final CommonGoal[] commonGoals;

    /*
     * The bag of tiles in the game.
     */
    protected final Bag bag;

    /*
     * The board of the game.
     */
    protected final Board board;

    /*
     * Whether the game has started.
     */
    protected boolean isStarted;

    /*
     * name of the creator of the game
     */
    protected String creator;

    /*
     * true if game is stopped
     */
    protected boolean isStopped;

    /**
     *
     * */
    protected boolean isWaitingForReconnections;

    /*
     * Index of the first player who completed the bookshelf
     */
    protected int firstPlayerCompleteBookshelf = -1;

    /*
     * The index of the current player in the list of players.
     */
    protected int currentPlayerIndex;

    /*
     * list of all player in the game
     */
    protected final List<Player> players;

    /**
     * Constructs a new GameView with the given game name and username of the creator.
     *
     * @param nameGame the name of the game
     * @param username the username of the creator
     *
     * @throws NullPointerException if either nameGame or username is null
     * @throws IllegalArgumentException if either nameGame or username is an empty string
     * */
    public GameView (String nameGame, String username) {
        if (nameGame == null || username == null)
            throw new NullPointerException();

        if (nameGame.isEmpty() || username.isEmpty())
            throw new IllegalArgumentException("String is empty");

        this.name = nameGame;
        this.bag = new Bag();
        this.board = new Board();
        this.isWaitingForReconnections = false;
        this.winners = new ArrayList<>();
        this.currentPlayerIndex = -1;
        this.isStarted = false;
        this.players = new ArrayList<>();
        this.creator = username;
        this.isStopped = false;
        this.commonGoals = new CommonGoal[2];
    }

    /**
     * Constructs a new GameView object by copying another GameView object.
     *
     * @param other the GameView object to copy
     * */
    public GameView(GameView other) {
        this.bag = new Bag(other.bag);
        this.board = new Board(other.board);
        this.winners = new ArrayList<>(other.winners);
        this.isStarted = other.isStarted;
        this.name = other.name;
        this.players = new ArrayList<>(other.players);
        this.creator = other.creator;
        this.isStopped = other.isStopped;
        this.firstPlayerCompleteBookshelf = other.firstPlayerCompleteBookshelf;
        this.currentPlayerIndex = other.currentPlayerIndex;
        this.isWaitingForReconnections = other.isWaitingForReconnections;

        this.commonGoals = new CommonGoal[other.commonGoals.length];
        for (int i = 0; i < other.commonGoals.length; i++) {
            this.commonGoals[i] = other.commonGoals[i];
        }
    }

    /**
     * @param username player to search
     * @return true iff exists a player with username "username"
     * */
    public synchronized boolean containPlayer (String username) {
        return players.stream().anyMatch(p -> p.getUsername().equals(username));
    }

    /**
     * This method returns the pause state of a game.
     * @return True iff the game is waitingForReconnections
     */
    public synchronized boolean isWaitingForReconnections() {
        return this.isWaitingForReconnections;
    }

    /**
     * @return The index of the first player that completed the bookshelf
     * */
    public synchronized int getFirstPlayerCompleteBookshelf() {
        return this.firstPlayerCompleteBookshelf;
    }

    /**
     * Gets the starting player of the game.
     * @return the starting player
     * @throws IllegalFlowException if there are no players in the game
     */
    public synchronized Player getStartingPlayer() throws IllegalFlowException {
        if (players.isEmpty()) {
            throw new IllegalFlowException("There is no starting player until someone joins the game");
        }

        return players.get(FIRST_PLAYER_INDEX);
    }

    /**
     * @return true iff there is one player online
     * */
    public synchronized boolean hasPlayerDisconnected () {
        return numberOfPlayerOnline() != players.size();
    }

    protected synchronized int getNextPlayerOnline (int currentPlayerIndex) throws NoPlayerConnectedException {
        for (int i = 1; i < this.players.size(); i++) {
            final int index = (currentPlayerIndex + i) % this.players.size();
            if (this.players.get(index).isConnected()) {
                return index;
            }
        }
        throw new NoPlayerConnectedException();
    }

    /**
     * @return Number of players online
     * */
    public synchronized int numberOfPlayerOnline () {
        return (int) this.players.stream().filter(Player::isConnected).count();
    }

    /**
     * This method is call to understand if one player can start or resume one game
     * @throws NullPointerException iff username is null
     * @throws IllegalStateException
     * <ul>
     *     <li> Game is started </li>
     *     <li> Game is started, it's stopped, and there are less then 2 player online </li>
     * </ul>
     * @return true iff username can start or resume this game.
     * */
    public synchronized boolean canStartGame (String username) {
        if (username == null)
            throw new NullPointerException();

        if (isStarted() || players.isEmpty())
            throw new IllegalStateException();

        return username.equals(creator);
    }

    protected synchronized int getIndex (String username) {
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).is(username))
                return i;
        }

        throw  new IllegalArgumentException("Player not in this game");
    }

    protected synchronized Player getPlayer (String username) {
        final int index = getIndex(username);
        return players.get(index);
    }

    public Optional<String> getCurrentOwner() {
        if (getPlayer(creator).isConnected())
            return Optional.of(creator);
        return  players.stream().filter(Player::isConnected).map(Player::getUsername).findFirst();
    }

    public synchronized boolean canStopGame (String username) throws NoPlayerConnectedException {
        Optional<String> currentOwner = getCurrentOwner();
        if (currentOwner.isEmpty())
            throw new NoPlayerConnectedException();
        return username.equals(currentOwner.get());
    }

    /**
     * Returns a boolean indicating whether the game has been stopped.
     * @return true if the game has been stopped, false otherwise
     */
    public synchronized boolean isStopped() {
        return isStopped;
    }

    /**
     * Checks if the game is over.
     * @return true iff the game is over, false otherwise
     */
    public synchronized boolean isOver () {
        return !winners.isEmpty();
    }

    /**
     * Returns the current {@link Player player}  of the game.
     *
     * @throws IllegalFlowException if the game is not started or if the game is over.
     * @return the current player of the game.
     */
    public synchronized Player getCurrentPlayer() throws IllegalFlowException {
        if (!isStarted()) {
            throw new IllegalFlowException("There is no current player until you start the game");
        }

        if (isOver()) {
            throw new IllegalFlowException("There is no current player when the game is over");
        }

        return players.get(currentPlayerIndex);
    }

    public synchronized boolean isAvailableForJoin(String username) {
        if (isStarted()) {
            return players.stream().anyMatch(p -> p.is(username));
        }
        return players.size() < 4;
    }

    /**
     * @return true iff at least one bookshelf is full
     * */
    public synchronized boolean atLeastOneBookshelfIsFull() {
        return players.stream().anyMatch(p -> p.getBookshelf().isFull());
    }

    /**
     * @return whether the game is over or not.
     */
    public synchronized List<Player> getWinners() throws IllegalFlowException {
        if (!isOver()) {
            throw new IllegalFlowException("There is no winner until the game is over");
        }

        return winners.stream().map(Player::new).toList();
    }

    /**
     * @throws IllegalFlowException iff the game hasn't started yet.
     * @return the last player in the list of players
     * */
    public synchronized Player getLastPlayer() throws IllegalFlowException {
        if (!isStarted()) {
            throw new IllegalFlowException("There is no last player until you start the game");
        }

        return players.get(players.size() - 1);
    }

    /**
     * This method is useful when we want to create a new GameView that
     * can't be modified by anyone.
     * @return a new immutable GameView
     * */
    public synchronized GameView createView () {
        return new GameView(this);
    }

    public synchronized String getOwner () {
        return this.creator;
    }

    /**
     * @return The array of all common goals
     * @see CommonGoal
     * */
    public synchronized CommonGoal[] getCommonGoals() {
        return commonGoals;
    }

    /**
     * Creates a new immutable viewBoard and returns it to the caller.
     * @return a new immutable viewBoard
     *
     * @see Board
     * @see BoardView
     */
    public synchronized BoardView getBoard () {
        return board.createView();
    }

    /**
     * Returns a boolean indicating whether the game has been started.
     * @return true if the game has been started, false otherwise
     */
    public synchronized boolean isStarted() {
        return this.isStarted;
    }

    /**
     * Returns a new immutable list of player views.
     * @return a new list of immutable player view instances
     * */
    public synchronized List<Player> getPlayers () {
        return new ArrayList<>(players);
    }

    @Override
    public String getName() {
        return this.name;
    }
}
