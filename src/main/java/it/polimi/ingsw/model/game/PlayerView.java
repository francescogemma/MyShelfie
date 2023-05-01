package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.bookshelf.Bookshelf;

/**
 * Container for all the public information of the player.
 * This object contains only getters and is immutable by itself.
 * To create a new immutable object, use the function {@link #createView()}.
 *
 * @author Giacomo Groppi
 * */
public class PlayerView {
    /*
     * username of the player
     */
    protected String username;

    /*
     * bookshelf of the player
     */
    protected Bookshelf bookshelf;

    /*
     * points earned so far
     */
    protected int points;

    /*
     * true iff player is connected
     */
    protected boolean isConnected;

    /**
     * Indicates whether the common goal in the game has been achieved.
     * The i-th position is true if the i-th common goal has been satisfied.
     * */
    protected boolean[] achievedCommonGoals;

    /**
     * create a new instance of PlayerView
     * */
    protected PlayerView() {
    }

    /**
     * Create a new instance of PlayerView equals to other
     * */
    protected PlayerView(PlayerView other) {
        this.username = other.username;
        this.bookshelf = new Bookshelf(other.bookshelf);

        this.points = other.points;
        this.isConnected = other.isConnected;

        this.achievedCommonGoals = new boolean[2];
        System.arraycopy(other.achievedCommonGoals, 0, this.achievedCommonGoals, 0, 2);
    }

    /**
     * @return a new copy of {@link Player#bookshelf}
     */
    public Bookshelf getBookshelf() {
        return new Bookshelf(bookshelf);
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

    /**
     * @return a new instance of player-view immutable
     * */
    public PlayerView createView() {
        return new PlayerView(this);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this)
            return true;
        if (other == null || other.getClass() != this.getClass())
            return false;

        // is it right!?
        return (((PlayerView) other).username.equals(this.username));
    }
}
