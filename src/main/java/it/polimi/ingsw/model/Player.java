package it.polimi.ingsw.model;

/**
 * This class represents a player of the game.
 * It contains the name of the player, the points, the library and the personal objective of that player.
 * @author Francesco Gemma
 */
public class Player {
    private String name;
    // private String hashedPassword;
    private Library library;
    // PersonalObjective personalObjective;
    private int points;

    /**
     * Constructor of the class Player.
     * Sets the name of the player, his points to 0 and creates a new library.
     * @param name the name of the player
     * @author Francesco Gemma
     */
    public Player(String name) {
        this.name = name;
        this.library = new Library();
        this.points = 0;
    }

    /**
     * @return the name of the player
     * @author Francesco Gemma
     */
    public String getName() {
        return name;
    }

    /**
     * @return the points of the player
     * @author Francesco Gemma
     */
    public int getPoints() {
        return points;
    }

    /**
     * This method adds points to the player
     * @param points number of points to add to the player
     * @throws IllegalArgumentException if the points to add are negative
     * @author Francesco Gemma
     */
    public void addPoints(int points) {
        if(points < 0) {
            throw new IllegalArgumentException("Points to add must be positive");
        } else {
            this.points += points;
        }
    }

    /**
     * @return the library of the player
     * @author Francesco Gemma
     */
    public Library getLibrary() {
        return library;
    }

    /**
     * @param library the library to set
     * @author Francesco Gemma
     */
    public void setLibrary(Library library) {
        this.library = library;
    }
}
