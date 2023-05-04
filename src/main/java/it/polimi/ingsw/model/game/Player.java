package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.bookshelf.Bookshelf;
import it.polimi.ingsw.model.goal.PersonalGoal;

/**
 * This class represents a player of the {@link Game game}.
 * It contains the username of the player, his {@link Player#points}, his {@link Bookshelf bookshelf} and his personal goal.
 * @author Francesco Gemma
 */
public class Player {
    /**
     * Constructor of the class.
     * Sets the name of the player, his points to 0 and creates a new {@link Bookshelf bookshelf}.
     * @param username the name of the player.
     */
    public Player(String username) {
        this.username = username;
        this.bookshelf = new Bookshelf();
        this.points = 0;
        this.isConnected = false;
        this.achievedCommonGoals = new boolean[]{ false, false };
    }

    /**
     * This method adds {@link Player#points} to the player.
     * @param points number of points to add to the player.
     * @throws IllegalArgumentException if the points to add are negative.
     */
    public void addPoints(int points) {
        if(points < 0) {
            throw new IllegalArgumentException("Points to add must be positive");
        } else {
            this.points += points;
        }
    }

    public Bookshelf getBookshelf () {
        return this.bookshelf;
    }

    /**
     * This method sets the connection state of the player.
     * @param isConnected the new connection state of the player.
     */
    public void setConnectionState(boolean isConnected) {
        this.isConnected = isConnected;
    }

    /**
     * This method sets one of the two common goals as achieved.
     * @param index the index of the {@link it.polimi.ingsw.model.goal.CommonGoal CommonGoal} to set as achieved.
     */
    public void achievedCommonGoal(int index) {
        achievedCommonGoals[index] = true;
    }

    /*
     * username of the player
     */
    private final String username;

    /*
     * bookshelf of the player
     */
    private final Bookshelf bookshelf;

    /*
     * points earned so far
     */
    private int points;

    /*
     * true iff player is connected
     */
    private boolean isConnected;

    /**
     * Indicates whether the common goal in the game has been achieved.
     * The i-th position is true if the i-th common goal has been satisfied.
     * */
    private final boolean[] achievedCommonGoals;

    /**
     * Create a new instance of PlayerView equals to other
     * */
    protected Player(Player other) {
        this.username = other.username;
        this.bookshelf = new Bookshelf(other.bookshelf);

        this.points = other.points;
        this.isConnected = other.isConnected;

        this.achievedCommonGoals = new boolean[2];
        System.arraycopy(other.achievedCommonGoals, 0, this.achievedCommonGoals, 0, 2);
    }

    /**
     * Sets the specified common goal as achieved.
     * @param index the index of the common goal to be set as achieved
     * @throws AssertionError if the index is negative or greater than or equal to the length of the achievedCommonGoals array
     * */
    public void setAchievedCommonGoals(int index) {
        assert index >= 0 && index < achievedCommonGoals.length;
        achievedCommonGoals[index] = true;
    }

    /**
     * @return {@link Player#isConnected} the connection state of the player.
     */
    public boolean isConnected() {
        return isConnected;
    }

    /**
     * Returns true if the player is disconnected, false otherwise.
     * @return true if the player is disconnected, false otherwise
     * @see #isConnected()
     */
    public boolean isDisconnected () {
        return !isConnected();
    }

    /**
     * This method checks if the common goal at the given index has been achieved.
     * @param index the index of the {@link it.polimi.ingsw.model.goal.CommonGoal CommonGoal} to check.
     * @return true if the common goal at the given index has been achieved, false otherwise.
     */
    public boolean hasAchievedCommonGoal(int index) {
        return achievedCommonGoals[index];
    }

    /**
     * @return {@link Player#username}
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns true if the given username matches the player's username, false otherwise.
     * @param username the username to compare with the player's username
     * @return true if the given username matches the player's username, false otherwise
     */
    public boolean is(String username) {
        return this.username.equals(username);
    }

    /**
     * @return {@link Player#points}
     */
    public int getPoints() {
        return points;
    }

    @Override
    public boolean equals (Object other) {
        if (other == this)
            return true;
        if (other == null || other.getClass() != this.getClass())
            return false;
        return this.username.equals(((Player) other).getUsername());
    }
}
