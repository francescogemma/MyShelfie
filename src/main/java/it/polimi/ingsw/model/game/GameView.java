package it.polimi.ingsw.model.game;

import it.polimi.ingsw.controller.db.Identifiable;
import it.polimi.ingsw.event.LocalEventTransceiver;
import it.polimi.ingsw.model.bag.Bag;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.board.BoardView;
import it.polimi.ingsw.model.goal.CommonGoal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
     * The list of players in the game.
     */
    protected final List<Player> players;

    /*
     * The optional winner of the game.
     */
    protected final List<Player> winners;

    /**
     * The common goals of the game.
     * */
    protected CommonGoal[] commonGoals;

    /**
     * The bag of tiles in the game.
     */
    protected final Bag bag;

    /**
     * The board of the game.
     */
    protected final Board board;

    /**
     * Whether the game has started.
     */
    protected boolean isStarted;

    protected final transient List<Integer> personalGoalIndexes;
    protected transient int personalGoalIndex;

    /**
     * The index of the current player in the list of players.
     */
    protected int currentPlayerIndex;

    public GameView (String nameGame) {
        if (nameGame == null)
            throw new NullPointerException();

        if (nameGame.length() == 0)
            throw new IllegalArgumentException("String is empty");

        this.name = nameGame;
        this.bag = new Bag();
        this.board = new Board();
        this.winners = new ArrayList<>();
        this.currentPlayerIndex = -1;
        this.isStarted = false;
        this.personalGoalIndexes = new ArrayList<>(Arrays.asList( 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 ));
        this.personalGoalIndex = 0;
        this.players = new ArrayList<>();
    }

    public GameView(GameView other) {
        this.bag = new Bag(other.bag);
        this.board = new Board(other.board);
        this.winners = new ArrayList<>(other.winners);
        this.isStarted = other.isStarted;
        this.name = other.name;

        // TODO
        this.commonGoals = new CommonGoal[2];
        this.commonGoals[0] = other.commonGoals[0];
        this.commonGoals[1] = other.commonGoals[1];

        this.personalGoalIndexes = new ArrayList<>(other.personalGoalIndexes);
        this.personalGoalIndex = other.personalGoalIndex;
        this.players = new ArrayList<>(other.players);
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

    public boolean canStartGame (String username) throws IllegalFlowException {
        if (isStarted() || isOver() || players.isEmpty())
            throw new IllegalFlowException();
        return this.players.get(0).getUsername().equals(username);
    }

    /**
     * Checks if the game is over.
     * @return true iff the game is over, false otherwise
     */
    public boolean isOver () {
        return !winners.isEmpty();
    }

    public int getPersonalGoalIndex (String username) {
        for (Player player: players) {
            if (player.getUsername().equals(username)) {
                return player.getPersonalGoal().getIndex();
            }
        }
        throw new IllegalArgumentException("....");
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

        return players.get(currentPlayerIndex).getView();
    }

    /**
     * @return whether the game is over or not.
     */
    public List<PlayerView> getWinners() throws IllegalFlowException {
        if (!isOver()) {
            throw new IllegalFlowException("There is no winner until the game is over");
        }

        return winners.stream().map(PlayerView::getView).toList();
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

    public GameView getView () {
        return new GameView(this);
    }

    /**
     * @return The array of all common goals
     * @see CommonGoal
     * */
    public CommonGoal[] getCommonGoals() {
        return commonGoals;
    }

    protected Player getPlayer(String username) {
        for (Player p: this.players) {
            if (p.getUsername().equals(username))
                return p;
        }
        throw new IllegalArgumentException("Player not in this game");
    }

    protected int getNextPlayerOnline (int currentPlayerIndex) throws NoPlayerConnectedException {
        for (int i = 1; i < this.players.size(); i++) {
            final int index = (currentPlayerIndex + i) % this.players.size();
            if (this.players.get(index).isConnected) {
                return index;
            }
        }
        throw new NoPlayerConnectedException();
    }

    public BoardView getBoard () {
        return board.getView();
    }

    public boolean isStarted() {
        return this.isStarted;
    }

    public boolean hasPlayerDisconnected () {
        for (Player player: this.players) {
            if (!player.isConnected())
                return true;
        }
        return false;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
