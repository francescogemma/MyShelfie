package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.bookshelf.Bookshelf;
import it.polimi.ingsw.model.goal.PersonalGoal;

import java.awt.print.Book;

/**
 * @author Giacomo Groppi
 * */
public class PlayerView {
    protected String username;
    protected Bookshelf bookshelf;
    protected int points;
    protected boolean isConnected;
    protected boolean[] achievedCommonGoals;

    protected PlayerView() {
    }

    protected PlayerView(PlayerView other) {
        this.username = other.username;
        this.bookshelf = new Bookshelf(other.bookshelf);

        this.points = other.points;
        this.isConnected = other.isConnected;

        this.achievedCommonGoals = new boolean[2];
        System.arraycopy(other.achievedCommonGoals, 0, this.achievedCommonGoals, 0, 2);
    }

    public PlayerView getView() {
        return new PlayerView(this);
    }

    /**
     * @return a new copy of {@link Player#bookshelf}
     */
    public Bookshelf getBookshelf() {
        return new Bookshelf(bookshelf);
    }

    /**
     * @return {@link Player#isConnected} the connection state of the player.
     */
    public boolean isConnected() {
        return isConnected;
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
     * @return {@link Player#points}
     */
    public int getPoints() {
        return points;
    }

    /**
     * return an immutable object
     * TODO: javadoc
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
