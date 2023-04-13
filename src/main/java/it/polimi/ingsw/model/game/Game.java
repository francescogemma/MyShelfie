package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.bag.Bag;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.goal.CommonGoal;
import it.polimi.ingsw.model.goal.PersonalGoal;

import java.util.*;

/**
 * Class for manage game.
 * @author Giacomo Groppi
 * @author Cristiano Migali
 * */
public class Game {
    // TODO: Add JavaDoc for this class

    /*
     * The index of the first player in the list of players.
     */
    private static final int FIRST_PLAYER_INDEX = 0;

    /*
     * The name of the game.
     */
    private final String name;

    /*
     * The list of players in the game.
     */
    private final List<Player> players;

    /*
     * The optional winner of the game.
     */
    private Optional<Player> winner;

    /**
     * The common goals of the game.
     * */
    private CommonGoal[] commonGoals;

    /**
     * The bag of tiles in the game.
     */
    private final Bag bag;

    /**
     * The board of the game.
     */
    private final Board board;

    /**
     * Whether the game has started.
     */
    private boolean isStarted;

    private List<Integer> personalGoalIndexes;
    private int personalGoalIndex;

    /**
     * The index of the current player in the list of players.
     */
    private int currentPlayerIndex;

    /**
     * Creates a new game with the given name.
     * @param name the name of the game
     * @throws IllegalArgumentException iff the name is empty
     * @throws NullPointerException iff name is null
     */
    public Game(String name) {
        if (name == null)
            throw new NullPointerException();

        if (name.length() == 0)
            throw new IllegalArgumentException("String is empty");

        this.name = name;

        this.players = new ArrayList<>();

        this.winner = Optional.empty();

        this.bag = new Bag();
        this.board = new Board();

        this.isStarted = false;

        this.personalGoalIndexes = new ArrayList<>(Arrays.asList( 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 ));
        Collections.shuffle(this.personalGoalIndexes);
        this.personalGoalIndex = 0;
    }

    /**
     * Gets the starting player of the game.
     * @return the starting player
     * @throws IllegalFlowException if there are no players in the game
     */
    public Player getStartingPlayer() throws IllegalFlowException {
        if (players.isEmpty()) {
            throw new IllegalFlowException("There is no starting player until someone joins the game");
        }

        return players.get(FIRST_PLAYER_INDEX);
    }

    /**
     * Checks if the game is over.
     * @return true iff the game is over, false otherwise
     */
    public boolean isOver () {
        return winner.isPresent();
    }

    /**
     * Adds a player to the game with the given username.
     * @param username the username of the player to add
     * @throws PlayerAlreadyInGameException if the player is already in the game
     * @throws NullPointerException
     *  <ul>
     *      <li> username is null </li>
     *      <li> a player with the name "username" already exists </li>
     *  </ul>
     * @throws IllegalFlowException
     *  <ul>
     *      <li> if the game has already started </li>
     *      <li> if the game is already 4 players </li>
     *  </ul>
     */
    public void addPlayer(String username) throws IllegalFlowException, PlayerAlreadyInGameException {
        if (username == null || username.length() == 0)
            throw new NullPointerException("username is null or has length 0");

        // Allows for reconnection of disconnected players
        for (Player otherPlayer : players) {
            if (otherPlayer.getUsername().equals(username)) {
                if (otherPlayer.isConnected()) {
                    throw new PlayerAlreadyInGameException(username);
                }

                otherPlayer.setConnectionState(true);
                return;
            }
        }

        if (isStarted) {
            throw new IllegalFlowException("You can't add players when the game has already started");
        }

        if (players.size() == 4) {
            throw new IllegalFlowException("You can't have more than 4 players in the same game");
        }

        Player player = new Player(username);

        player.setPersonalGoal(PersonalGoal.fromIndex(personalGoalIndexes.get(personalGoalIndex)));
        personalGoalIndex++;
        players.add(player);
    }

    /**
     * Starts the game by initializing the common goals and the first player index.
     *
     * @throws IllegalFlowException if there are less than two players.
     */
    public void startGame() throws IllegalFlowException {
        if (players.size() < 2) {
            throw new IllegalFlowException("You need at least two players in order to start the game");
        }

        this.currentPlayerIndex = FIRST_PLAYER_INDEX;

        this.commonGoals = CommonGoal.getTwoRandomCommonGoals(players.size());

        isStarted = true;
    }

    /**
     * @return the name of the game.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the current {@link Player player}  of the game.
     *
     * @throws IllegalFlowException if the game is not started or if the game is over.
     * @return the current player of the game.
     */
    public Player getCurrentPlayer() throws IllegalFlowException {
        if (!isStarted) {
            throw new IllegalFlowException("There is no current player until you start the game");
        }

        if (isOver()) {
            throw new IllegalFlowException("There is no current player when the game is over");
        }

        return players.get(currentPlayerIndex);
    }

    /**
     * @return whether the game is over or not.
     */
    public Player getWinner () throws IllegalFlowException {
        if (!isOver()) {
            throw new IllegalFlowException("There is no winner until the game is over");
        }

        return winner.get();
    }

    /**
     * The function ends the current player's turn and moves on to the next one
     *
     * @throws IllegalFlowException iff the game hasn't started yet.
     * <ul>
     *     <li> the game hasn't started yet </li>
     *     <li> game is over </li>
     * </ul>
     * */
    public void nextTurn() throws IllegalFlowException {
        if (!isStarted) {
            throw new IllegalFlowException("You can't move to next turn until the game has started");
        }

        if (isOver()) {
            throw new IllegalFlowException("You can't move to next turn when the game is over");
        }

        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }

    /**
     * @throws IllegalFlowException iff the game hasn't started yet.
     * @return the last player in the list of players
     * */
    public Player getLastPlayer() throws IllegalFlowException {
        if (!isStarted) {
            throw new IllegalFlowException("There is no last player until you start the game");
        }

        return players.get(players.size() - 1);
    }


    /**
     * @return The list of players in this game.
     * */
    public List<Player> getPlayers() {
        return new ArrayList<>(players);
    }

    /**
     * @return The {@link Bag bag} of the current Game
     * @see Bag
     * */
    public Bag getBag() {
        return bag;
    }

    /**
     * @return The {@link Board board} of the current Game
     * @see Board
     * */
    public Board getBoard() {
        return board;
    }

    /**
     * @return The array of all common goals
     * @see CommonGoal
     * */
    public CommonGoal[] getCommonGoals() {
        return commonGoals;
    }

    /**
     * @return true iff at least one bookshelf is full
     * */
    public boolean atLeastOneBookshelfIsFull() {
        return players.stream().anyMatch(p -> p.getBookshelf().isFull());
    }


    /**
     * @param player winner of the game
     * @throws IllegalArgumentException iff player is not in this game
     * @throws IllegalFlowException iff winner is already set
     * @see Player
     * */
    public void setWinner(Player player) throws IllegalFlowException {
        if (!this.players.contains(player))
            throw new IllegalArgumentException(player + " is not in this game" + this);
        if (this.winner.isPresent())
            throw new IllegalFlowException("Winner is already set. CurrentWinner: [" + this.winner.get() + "]" + " passed winner: [" + player + "]");
        winner = Optional.of(player);
    }
}
