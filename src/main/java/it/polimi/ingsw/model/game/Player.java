package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.bookshelf.Bookshelf;

/**
 * This class represents a player of the {@link Game game}.
 * It contains the name of the player, his {@link Player#points}, his {@link Bookshelf bookshelf} and his personal goal.
 * @author Francesco Gemma
 */
public class Player {
    private String name;
    private Bookshelf bookshelf;
    // TODO: Add personal goal support
    // PersonalGoal personalGoal;
    private int points;

    /**
     * Constructor of the class.
     * Sets the name of the player, his points to 0 and creates a new {@link Bookshelf bookshelf}.
     * @param name the name of the player.
     */
    public Player(String name) {
        this.name = name;
        this.bookshelf = new Bookshelf();
        this.points = 0;
    }

    /**
     * @return {@link Player#name}
     */
    public String getName() {
        return name;
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
     * This method sets the {@link Player#bookshelf} of the player.
     * @param bookshelf the new {@link Bookshelf}.
     */
    public void setBookshelf(Bookshelf bookshelf) {
        this.bookshelf = bookshelf;
    }
}
