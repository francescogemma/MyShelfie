package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.bag.Bag;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.goal.CommonGoal;

import java.util.*;

public class Game {
    // TODO: Add JavaDoc for this class

    private static final int FIRST_PLAYER_INDEX = 0;
    private final String name;
    private final List<Player> players;
    private Optional<Player> winner;
    private final CommonGoal[] commonGoals;
    private final Bag bag;
    private final Board board;
    private boolean isStarted;

    private int currentPlayerIndex;

    public Game(String name) {
        if (name.length() == 0)
            throw new IllegalArgumentException("String is empty");

        this.name = name;

        this.players = new ArrayList<>();

        this.winner = Optional.empty();

        // TODO: Get two random common goals
        this.commonGoals = new CommonGoal[]{ null, null };

        this.bag = new Bag();
        this.board = new Board();

        this.isStarted = false;
    }

    public Player getStartingPlayer() throws IllegalFlowException {
        if (players.isEmpty()) {
            throw new IllegalFlowException("There is no starting player until someone joins the game");
        }

        return players.get(FIRST_PLAYER_INDEX);
    }

    public boolean isOver () {
        return winner.isPresent();
    }

    public void addPlayer(String username) throws IllegalFlowException, PlayerAlreadyInGameException {
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
        // TODO: Get random (and available, i.e. not given to other players) personal goal
        player.setPersonalGoal(null);
        players.add(player);
    }

    public void startGame() throws IllegalFlowException {
        if (players.size() < 2) {
            throw new IllegalFlowException("You need at least two players in order to start the game");
        }

        this.currentPlayerIndex = FIRST_PLAYER_INDEX;
        isStarted = true;
    }

    /**
     * @return Name of the game.
     */
    public String getName() {
        return this.name;
    }

    public Player getCurrentPlayer() throws IllegalFlowException {
        if (!isStarted) {
            throw new IllegalFlowException("There is no current player until you start the game");
        }

        if (isOver()) {
            throw new IllegalFlowException("There is no current player when the game is over");
        }

        return players.get(currentPlayerIndex);
    }

    public Player getWinner () throws IllegalFlowException {
        if (!isOver()) {
            throw new IllegalFlowException("There is no winner until the game is over");
        }

        return winner.get();
    }

    public void nextTurn() throws IllegalFlowException {
        if (!isStarted) {
            throw new IllegalFlowException("You can't move to next turn until the game has started");
        }

        if (isOver()) {
            throw new IllegalFlowException("You can't move to next turn when the game is over");
        }

        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }

    public Player getLastPlayer() throws IllegalFlowException {
        if (!isStarted) {
            throw new IllegalFlowException("There is no last player until you start the game");
        }

        return players.get(players.size() - 1);
    }

    public List<Player> getPlayers() {
        return new ArrayList<>(players);
    }

    public Bag getBag() {
        return bag;
    }

    public Board getBoard() {
        return board;
    }

    public CommonGoal[] getCommonGoals() {
        return commonGoals;
    }

    public boolean atLeastOneBookshelfIsFull() {
        return players.stream().anyMatch(p -> p.getBookshelf().isFull());
    }

    public void setWinner(Player player) {
        winner = Optional.of(player);
    }
}
