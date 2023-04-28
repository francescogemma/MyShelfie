package it.polimi.ingsw.model.game;

import it.polimi.ingsw.controller.db.Identifiable;
import it.polimi.ingsw.model.bag.Bag;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.board.BoardView;
import it.polimi.ingsw.model.goal.CommonGoal;

import java.util.ArrayList;
import java.util.List;

/**
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

    protected String creator;

    protected boolean isStopped;

    /**
     * The index of the current player in the list of players.
     */
    protected int currentPlayerIndex;

    // TODO: Write custom GSON adapter for Game to remove players redundancy
    private final List<PlayerView> playerViews;

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

    public GameView(GameView other) {
        this.bag = new Bag(other.bag);
        this.board = new Board(other.board);
        this.winners = new ArrayList<>(other.winners);
        this.isStarted = other.isStarted;
        this.name = other.name;
        this.playerViews = new ArrayList<>(other.playerViews);
        this.creator = other.creator;
        this.isStopped = other.isStopped;

        this.commonGoals = new CommonGoal[other.commonGoals.length];
        for (int i = 0; i < other.commonGoals.length; i++) {
            this.commonGoals[i] = other.commonGoals[i];
        }
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

    public boolean canStartGame (String username) throws IllegalFlowException {
        if (isStarted() || isOver() || playerViews.isEmpty())
            throw new IllegalFlowException();
        return this.creator.equals(username);
    }

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

        return playerViews.get(currentPlayerIndex);
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
    public PlayerView getLastPlayer() throws IllegalFlowException {
        if (!isStarted) {
            throw new IllegalFlowException("There is no last player until you start the game");
        }

        return playerViews.get(playerViews.size() - 1);
    }

    protected void addPlayerView (PlayerView playerView) {
        this.playerViews.add(playerView);
    }
    protected void removePlayerView(int index) {
        playerViews.remove(index);
    }

    protected GameView createView () {
        return new GameView(this);
    }

    /**
     * @return The array of all common goals
     * @see CommonGoal
     * */
    public CommonGoal[] getCommonGoals() {
        return commonGoals;
    }

    public BoardView getBoard () {
        return board.getView();
    }

    public boolean isStarted() {
        return this.isStarted;
    }

    public List<PlayerView> getPlayers () {
        return new ArrayList<>(playerViews);
    }

    @Override
    public String getName() {
        return this.name;
    }
}
