package it.polimi.ingsw.model.game;

import it.polimi.ingsw.controller.db.Identifiable;
import it.polimi.ingsw.model.bag.Bag;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.board.BoardView;
import it.polimi.ingsw.model.goal.CommonGoal;

import java.util.ArrayList;
import java.util.List;

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
    protected CommonGoal[] commonGoals;

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

    /*
     * Index of the first player who completed the bookshelf
     */
    protected int firstPlayerCompleteBookshelf = -1;

    /*
     * The index of the current player in the list of players.
     */
    protected int currentPlayerIndex;

    // TODO: Write custom GSON adapter for Game to remove players redundancy
    private final List<PlayerView> playerViews;

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
        this.winners = new ArrayList<>();
        this.currentPlayerIndex = -1;
        this.isStarted = false;
        this.playerViews = new ArrayList<>();
        this.creator = username;
        this.isStopped = false;
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
        this.playerViews = new ArrayList<>(other.playerViews);
        this.creator = other.creator;
        this.isStopped = other.isStopped;
        this.firstPlayerCompleteBookshelf = other.firstPlayerCompleteBookshelf;

        this.commonGoals = new CommonGoal[other.commonGoals.length];
        for (int i = 0; i < other.commonGoals.length; i++) {
            this.commonGoals[i] = other.commonGoals[i];
        }
    }

    /**
     * @return The index of the first player that completed the bookshelf
     * */
    public int getFirstPlayerCompleteBookshelf() {
        return this.firstPlayerCompleteBookshelf;
    }

    /**
     * Gets the starting player of the game.
     * @return the starting player
     * @throws IllegalFlowException if there are no players in the game
     */
    public PlayerView getStartingPlayer() throws IllegalFlowException {
        if (playerViews.isEmpty()) {
            throw new IllegalFlowException("There is no starting player until someone joins the game");
        }

        return playerViews.get(FIRST_PLAYER_INDEX);
    }

    /**
     * @return true iff there is one player online
     * */
    public boolean hasPlayerDisconnected () {
        return playerViews.stream().anyMatch(PlayerView::isDisconnected);
    }

    /**
     * @return The next player online from username
     * @throws NoPlayerConnectedException iff there is no player online except username
     * */
    protected int getNextPlayerOnline(String username) throws NoPlayerConnectedException {
        int index = -1;
        for (int i = 0; i < playerViews.size(); i++) {
            if (playerViews.get(i).is(username))
                index = i;
        }

        if (index == -1)
            throw new IllegalArgumentException("Player not in this game");

        return getNextPlayerOnline(index);
    }

    protected int getNextPlayerOnline (int currentPlayerIndex) throws NoPlayerConnectedException {
        for (int i = 1; i < this.playerViews.size(); i++) {
            final int index = (currentPlayerIndex + i) % this.playerViews.size();
            if (this.playerViews.get(index).isConnected()) {
                return index;
            }
        }
        throw new NoPlayerConnectedException();
    }

    /**
     * @return Number of players online
     * */
    public int numberOfPlayerOnline () {
        return (int) this.playerViews.stream().filter(PlayerView::isConnected).count();
    }

    /**
     * This method is call to understand if one player can start or resume one game
     * @throws NullPointerException iff username is null
     * @throws IllegalFlowException
     * <ul>
     *     <li> Game is over </li>
     *     <li> Game is started and it's not stopped </li>
     *     <li> Game is started, it's stopped, and there are less then 2 player online </li>
     * </ul>
     * @return true iff username can start or resume this game.
     * */
    public boolean canStartGame (String username) throws IllegalFlowException {
        if (username == null)
            throw new NullPointerException();

        if (isOver())
            throw new IllegalFlowException();

        if (isStarted() && !isStopped())
            throw new IllegalFlowException();

        // it is stop, and it can't start with only 1 player connected
        if (isStarted() && isStopped() && playerViews.size() < 2)
            throw new IllegalFlowException();

        if (playerViews.isEmpty())
            throw new IllegalFlowException();

        return username.equals(creator);
    }

    /**
     * Returns a boolean indicating whether the game has been stopped.
     * @return true if the game has been stopped, false otherwise
     */
    public boolean isStopped() {
        return isStopped;
    }

    /**
     * Checks if the game is over.
     * @return true iff the game is over, false otherwise
     */
    public boolean isOver () {
        return !winners.isEmpty();
    }

    /**
     * Returns the current {@link Player player}  of the game.
     *
     * @throws IllegalFlowException if the game is not started or if the game is over.
     * @return the current player of the game.
     */
    public PlayerView getCurrentPlayer() throws IllegalFlowException {
        if (!isStarted) {
            throw new IllegalFlowException("There is no current player until you start the game");
        }

        if (isOver()) {
            throw new IllegalFlowException("There is no current player when the game is over");
        }

        if (isStopped())
            throw new IllegalFlowException("Game is stopped");

        return playerViews.get(currentPlayerIndex);
    }

    public boolean isAvailableForJoin(String username) {
        if (isStopped()) {
            return playerViews.stream().anyMatch(p -> p.is(username)) || creator.equals(username);
        }
        return !isStarted() || numberOfPlayerOnline() != playerViews.size();
    }

    /**
     * @return true iff at least one bookshelf is full
     * */
    public boolean atLeastOneBookshelfIsFull() {
        return playerViews.stream().anyMatch(p -> p.getBookshelf().isFull());
    }

    /**
     * @return whether the game is over or not.
     */
    public List<PlayerView> getWinners() throws IllegalFlowException {
        if (!isOver()) {
            throw new IllegalFlowException("There is no winner until the game is over");
        }

        return winners.stream().map(PlayerView::createView).toList();
    }

    /**
     * @throws IllegalFlowException iff the game hasn't started yet.
     * @return the last player in the list of players
     * */
    public PlayerView getLastPlayer() throws IllegalFlowException {
        if (!isStarted) {
            throw new IllegalFlowException("There is no last player until you start the game");
        }

        return playerViews.get(playerViews.size() - 1);
    }

    protected void addPlayerView (PlayerView playerView) {
        assert playerView.getClass() == Player.class;
        this.playerViews.add(playerView);
    }

    protected void removePlayerView(int index) {
        playerViews.remove(index);
    }

    /**
     * This method is useful when we want to create a new GameView that
     * can't be modified by anyone.
     * @return a new immutable GameView
     * */
    public GameView createView () {
        return new GameView(this);
    }

    /**
     * @return The array of all common goals
     * @see CommonGoal
     * */
    public CommonGoal[] getCommonGoals() {
        return commonGoals;
    }

    /**
     * Creates a new immutable viewBoard and returns it to the caller.
     * @return a new immutable viewBoard
     *
     * @see Board
     * @see BoardView
     */
    public BoardView getBoard () {
        return board.createView();
    }

    /**
     * Returns a boolean indicating whether the game has been started.
     * @return true if the game has been started, false otherwise
     */
    public boolean isStarted() {
        return this.isStarted;
    }

    /**
     * Returns a new immutable list of player views.
     * @return a new list of immutable player view instances
     * */
    public List<PlayerView> getPlayers () {
        return new ArrayList<>(playerViews);
    }

    @Override
    public String getName() {
        return this.name;
    }
}
