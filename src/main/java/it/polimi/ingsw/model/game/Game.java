package it.polimi.ingsw.model.game;

import it.polimi.ingsw.controller.db.Identifiable;
import it.polimi.ingsw.event.LocalEventTransceiver;
import it.polimi.ingsw.event.data.gameEvent.*;
import it.polimi.ingsw.model.bag.Bag;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.board.FullSelectionException;
import it.polimi.ingsw.model.board.IllegalExtractionException;
import it.polimi.ingsw.model.goal.AdjacencyGoal;
import it.polimi.ingsw.model.goal.CommonGoal;
import it.polimi.ingsw.model.goal.Goal;
import it.polimi.ingsw.model.goal.PersonalGoal;
import it.polimi.ingsw.model.tile.Tile;
import it.polimi.ingsw.utils.Coordinate;

import java.util.*;

/**
 * Class for manage game.
 * @author Giacomo Groppi
 * @author Cristiano Migali
 * */
public class Game implements Identifiable {
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
    private final List<Player> players = new ArrayList<>();

    /*
     * The optional winner of the game.
     */
    private final List<Player> winner = new ArrayList<>();

    /**
     * The common goals of the game.
     * */
    private CommonGoal[] commonGoals;

    /**
     * The bag of tiles in the game.
     */
    private final Bag bag = new Bag();

    /**
     * The board of the game.
     */
    private final Board board = new Board();

    /**
     * Whether the game has started.
     */
    private boolean isStarted;

    private final transient List<Integer> personalGoalIndexes;
    private transient int personalGoalIndex;

    /**
     * The index of the current player in the list of players.
     */
    private int currentPlayerIndex;

    private transient LocalEventTransceiver transceiver;

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
        this.currentPlayerIndex = -1;
        this.isStarted = false;

        this.personalGoalIndexes = new ArrayList<>(Arrays.asList( 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 ));
        Collections.shuffle(this.personalGoalIndexes);
        this.personalGoalIndex = 0;
    }

    public void setTransceiver (LocalEventTransceiver transceiver) {
        assert this.transceiver == null;
        this.transceiver = transceiver;
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
        return winner.size() != 0;
    }

    /**
     * Adds a player to the game with the given username or reconnects that player.
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
     * @return {@link Player player} added
     */
    public Player addPlayer(String username) throws IllegalFlowException, PlayerAlreadyInGameException {
        if (username == null || username.length() == 0)
            throw new NullPointerException("username is null or has length 0");

        // Allows for reconnection of disconnected players
        for (Player otherPlayer : players) {
            if (otherPlayer.getUsername().equals(username)) {
                if (otherPlayer.isConnected()) {
                    throw new PlayerAlreadyInGameException(username);
                }

                otherPlayer.setConnectionState(true);
                return otherPlayer;
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
        return player;
    }

    public void disconnectPlayer (String username) {
        Player player = this.getPlayer(username);

        if (!player.isConnected()) {
            return;
        }

        player.setConnectionState(false);

        if (!this.players.get(currentPlayerIndex).equals(player)) {
            calculateNextPlayer();
        }

        this.transceiver.broadcast(new PlayerHasDisconnectedEventData(player.getUsername()));
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
        this.transceiver.broadcast(new GameHasStartedEventData());
    }

    @Override
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
    public List<Player> getWinner () throws IllegalFlowException {
        if (!isOver()) {
            throw new IllegalFlowException("There is no winner until the game is over");
        }

        return new ArrayList<>(winner);
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

    public void forgetLastSelection(String username, Coordinate c) throws IllegalFlowException {
        if (isStarted) throw new IllegalFlowException("Game is not started");
        if (isOver()) throw new IllegalFlowException("Game has ended");
        if (!getCurrentPlayer().getUsername().equals(username)) throw new IllegalFlowException("It's not your turn");

        this.board.forgetSelected(c);
        this.transceiver.broadcast(new BoardChangedEventData(board));
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

    private Player getPlayer(String username) {
        for (Player p: this.players) {
            if (p.getUsername().equals(username))
                return p;
        }
        throw new IllegalArgumentException("Player not in this game");
    }

    private void refillBoardIfNecessary () {
        if (!this.board.needsRefill())
            return;

        boolean hasChanged = false;

        while (bag.isEmpty()) {
            Tile t = this.bag.getRandomTile();

            try {
                this.board.fillRandomly(t, this.players.size());
                hasChanged = true;
            } catch (IllegalArgumentException e) {
                bag.forgetLastExtraction();
                break;
            }
        }

        if (hasChanged) {
            this.transceiver.broadcast(new BoardChangedEventData(this.board));
        }
    }

    private void calculateNextPlayer() {
        assert this.players.size() >= 2 && this.players.size() <= 4;
        assert this.isStarted;

        refillBoardIfNecessary();

        if (this.currentPlayerIndex != -1) {
            if (atLeastOneBookshelfIsFull() && this.currentPlayerIndex + 1 == this.players.size()) {
                // Game ending logic:
                for (Player player : players) {
                    int personalGoalPoints = player.getPersonalGoal().calculatePoints(player.getBookshelf());
                    if (personalGoalPoints > 0) {
                        player.addPoints(personalGoalPoints);

                        this.transceiver.broadcast(new PlayerPointsChangeEventData(player, player.getPersonalGoal().getPointMasks()));
                    }

                    Goal adjacencyGoal = new AdjacencyGoal();
                    int adjacencyGoalPoints = adjacencyGoal.calculatePoints(player.getBookshelf());

                    if (adjacencyGoalPoints > 0) {
                        player.addPoints(adjacencyGoalPoints);

                        this.transceiver.broadcast(new PlayerPointsChangeEventData(player, adjacencyGoal.getPointMasks()));
                    }
                }

                for (Player player : players) {
                    int max = this.winner.isEmpty() ? 0 : winner.get(0).getPoints();

                    if (player.getPoints() >= max) {
                        if (player.getPoints() > max)
                            winner.clear();

                        winner.add(player);
                    }
                }

                this.transceiver.broadcast(new GameOverEventData(this.winner));
            } else {
                this.refillBoardIfNecessary();

                // set new turn
                this.currentPlayerIndex = (currentPlayerIndex + 1) % players.size();

                this.transceiver.broadcast(new CurrentPlayerChangedEventData(players.get(currentPlayerIndex).getUsername()));
            }
        } else {
            this.currentPlayerIndex = 0;
        }
    }

    /**
     * TODO: javadoc
     * insert tile selected
     * */
    public void insertTile (String username, int col) throws IllegalFlowException, IllegalExtractionException {
        final Player player = getPlayer(username);

        if (this.getCurrentPlayer().equals(player))
            throw new IllegalFlowException();

        List<Tile> t = board.getSelectableTiles();

        if (t.isEmpty())
            throw new IllegalExtractionException();

        player.getBookshelf().insertTiles(t, col);
        board.draw();

        this.transceiver.broadcast(new BoardChangedEventData(board));

        for (CommonGoal commonGoal: this.commonGoals) {
            int points = commonGoal.calculatePoints(player.getBookshelf());
            if (points > 0) {
                player.addPoints(points);

                this.transceiver.broadcast(new PlayerPointsChangeEventData(player, commonGoal.getPointMasks()));
            }
        }
    }

    /**
     * TODO: javadoc
     * select a tile from the board
     * */
    public void selectTile (String username, Coordinate coordinate) throws IllegalFlowException, IllegalExtractionException, FullSelectionException {
        if (!this.isStarted)
            throw new IllegalFlowException("Game is not started");
        if (!this.getCurrentPlayer().getUsername().equals(username))
            throw new IllegalFlowException("It's not your turn");
        if (isOver())
            throw new IllegalFlowException("Game is over");

        this.board.selectTile(coordinate);
    }

    /**
     * @param player winner of the game
     * @throws IllegalArgumentException iff player is not in this game
     * @throws IllegalFlowException
     *  <ul>
     *      <li> winner is already set </li>
     *      <li> game is not started </li>
     *  </ul>
     * @see Player
     * */
    private void setWinner(Player player) throws IllegalFlowException {
        if (!this.isStarted)
            throw new IllegalFlowException();
        if (!this.players.contains(player))
            throw new IllegalArgumentException(player + " is not in this game" + this);
        winner.add(player);
    }
}
