package it.polimi.ingsw.model;

/**
 * This class represents a player of the {@link Game game}.
 * It contains the name of the player, his {@link Player#points}, his {@link Library library} and his personal objective of that player.
 * @author Francesco Gemma
 */
public class Player {
    private String name;
    // private String hashedPassword;
    private Library library;
    // PersonalObjective personalObjective;
    private int points;

    /**
     * Constructor of the class.
     * Sets the name of the player, his points to 0 and creates a new {@link Library library}.
     * @param name the name of the player.
     */
    public Player(String name) {
        this.name = name;
        this.library = new Library();
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
        if(points < 0) {
            throw new IllegalArgumentException("Points to add must be positive");
        } else {
            this.points += points;
        }
    }

    /**
     * @return {@link Player#library}
     */
    public Library getLibrary() {
        return library;
    }

    /**
     * This method sets the {@link Player#library} of the player.
     * @param library the new {@link Library}.
     */
    public void setLibrary(Library library) {
        this.library = library;
    }
}
