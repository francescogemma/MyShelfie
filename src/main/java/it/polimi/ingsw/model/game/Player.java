package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.bookshelf.Bookshelf;
import it.polimi.ingsw.model.goal.PersonalGoal;

/**
 * This class represents a player of the {@link Game game}.
 * It contains the username of the player, his {@link Player#points}, his {@link Bookshelf bookshelf} and his personal goal.
 * @author Francesco Gemma
 */
public class Player {
    // TODO: Add JavaDoc and appropriate testing for added features

    private final String username;
    private final Bookshelf bookshelf;
    private PersonalGoal personalGoal;
    private int points;
    private boolean isConnected;
    private boolean[] achievedCommonGoals;

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

    public void setPersonalGoal(PersonalGoal personalGoal) {
        this.personalGoal = personalGoal;
    }

    public PersonalGoal getPersonalGoal() {
        return this.personalGoal;
    }

    public void setConnectionState(boolean isConnected) {
        this.isConnected = isConnected;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void achievedCommonGoal(int index) {
        achievedCommonGoals[index] = true;
    }

    public boolean hasAchievedCommonGoal(int index) {
        return achievedCommonGoals[index];
    }
}
