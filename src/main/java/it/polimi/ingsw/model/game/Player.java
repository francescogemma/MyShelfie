package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.bookshelf.Bookshelf;
import it.polimi.ingsw.model.goal.PersonalGoal;

/**
 * This class represents a player of the {@link Game game}.
 * It contains the username of the player, his {@link Player#points}, his {@link Bookshelf bookshelf} and his personal goal.
 * @author Francesco Gemma
 */
public class Player {
    private final String username;
    private final Bookshelf bookshelf;
    private PersonalGoal personalGoal;
    private int points;
    private boolean isConnected;
    private final boolean[] achievedCommonGoals;

    /**
     * Constructor of the class.
     * Sets the name of the player, his points to 0 and creates a new {@link Bookshelf bookshelf}.
     * @param username the name of the player.
     */
    public Player(String username) {
        this.username = username;
        this.bookshelf = new Bookshelf();
        this.points = 0;
        this.isConnected = true;
        this.achievedCommonGoals = new boolean[]{ false, false };
    }

    /**
     * @return {@link Player#username}
     */
    public String getUsername() {
        return username;
    }

    /**
     * @return {@link Player#points}
     */
    public int getPoints() {
        return points;
    }

    /**
     * This method adds {@link Player#points} to the player.
     * @param points number of points to add to the player.
     * @throws IllegalArgumentException if the points to add are negative.
     */
    public void addPoints(int points) {
        if(points <= 0) {
            throw new IllegalArgumentException("Points to add must be positive");
        } else {
            this.points += points;
        }
    }

    /**
     * @return {@link Player#bookshelf}
     */
    public Bookshelf getBookshelf() {
        return bookshelf;
    }

    /**
     * This method sets the personal goal to the player.
     * @param personalGoal the personal goal to set to the player.
     */
    public void setPersonalGoal(PersonalGoal personalGoal) {
        this.personalGoal = personalGoal;
    }

    /**
     * @return {@link Player#personalGoal} of the player.
     */
    public PersonalGoal getPersonalGoal() {
        return this.personalGoal;
    }

    /**
     * This method sets the connection state of the player.
     * @param isConnected the new connection state of the player.
     */
    public void setConnectionState(boolean isConnected) {
        this.isConnected = isConnected;
    }

    /**
     * @return {@link Player#isConnected} the connection state of the player.
     */
    public boolean isConnected() {
        return isConnected;
    }

    /**
     * This method sets one of the two common goals as achieved.
     * @param index the index of the {@link it.polimi.ingsw.model.goal.CommonGoal CommonGoal} to set as achieved.
     */
    public void achievedCommonGoal(int index) {
        achievedCommonGoals[index] = true;
    }

    /**
     * This method checks if the common goal at the given index has been achieved.
     * @param index the index of the {@link it.polimi.ingsw.model.goal.CommonGoal CommonGoal} to check.
     * @return true if the common goal at the given index has been achieved, false otherwise.
     */
    public boolean hasAchievedCommonGoal(int index) {
        return achievedCommonGoals[index];
    }
}
