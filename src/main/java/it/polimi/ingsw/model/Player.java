package it.polimi.ingsw.model;

public class Player {
    private String name;
    // private String hashedPassword;
    private Library library;
    // PersonalObjective personalObjective;
    private int points;

    public Player(String name) {
        this.name = name;
        this.library = new Library();
        this.points = 0;
    }

    public String getName() {
        return name;
    }

    public int getPoints() {
        return points;
    }

    public void addPoints(int points) {
        this.points += points;
    }

    public Library getLibrary() {
        return library;
    }

    public void setLibrary(Library library) {
        this.library = library;
    }
}
