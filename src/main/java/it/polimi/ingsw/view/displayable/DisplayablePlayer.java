package it.polimi.ingsw.view.displayable;

import it.polimi.ingsw.model.game.Player;

/**
 * Represents a player that can be displayed inside a score board in the game layout.
 * In particular a player is characterized by a name, the number of points that it has scored, if it is currently connected
 * or not, its index in the original lobby ordering and if it is the "client" (player associated with the user on whose
 * machine the program is running) player or not.
 * Additional information is its position in the scoreboard, the additional points used when displaying a common goal
 * which are displayed besides other points with a "+" and are highlighted in green, if it should be blurred or not
 * (when displaying a goal every player gets blurred except for the one that has completed the goal) and if it is the
 * winner or not.
 *
 * @author Cristiano Migali
 */
public class DisplayablePlayer {
    /**
     * It is the name of the player.
     */
    private String name;

    /**
     * It corresponds to the number of points that the player has scored (except for additional points which
     * are added in a second time after having displayed the goal completion).
     */
    private int points;

    /**
     * It is true iff the player is currently connected.
     */
    private boolean isConnected;

    /**
     * It is the original index of the player in the lobby ordering.
     */
    private int originalIndex;

    /**
     * It is true iff the player is associated with the user on whose machine the program is running.
     */
    private boolean isClientPlayer;

    /**
     * It is the position of the player inside the scoreboard.
     */
    private int position = 0;

    /**
     * Additional points used while displaying goal completion. They are displayed beside the previous points
     * with a "+" and are highlighted in green.
     */
    private int additionalPoints = 0;

    /**
     * It is true iff this player should be blurred in the scoreboard.
     */
    private boolean isBlurred = false;

    /**
     * It is true iff this player is the winner.
     */
    private boolean isWinner = false;

    /**
     * Constructor of the class.
     * It initializes the required attributes of the DisplayablePlayer.
     *
     * @param player is the {@link Player} which contains the name, points and connection state of the DisplayablePlayer
     *               that we want to create.
     * @param originalIndex is the original index of the DisplayablePlayer in the lobby ordering.
     * @param isClientPlayer must be true iff the constructed DisplayablePlayer is associated with the user on whose
     *                       machine the program is running.
     */
    public DisplayablePlayer(Player player,
                             int originalIndex,
                             boolean isClientPlayer) {

        this.name = player.getUsername();
        this.points = player.getPoints();
        this.isConnected = player.isConnected();
        this.originalIndex = originalIndex;
        this.isClientPlayer = isClientPlayer;
    }

    /**
     * Copy constructor of the class.
     *
     * @param other is the DisplayablePlayer which will be copied into the new one.
     */
    public DisplayablePlayer(DisplayablePlayer other) {
        name = other.name;
        points = other.points;
        isConnected = other.isConnected;
        originalIndex = other.originalIndex;
        isClientPlayer = other.isClientPlayer;
        position = other.position;
        additionalPoints = other.additionalPoints;
        isBlurred = other.isBlurred;
        isWinner = other.isWinner;
    }

    /**
     * @return the name of the DisplayablePlayer.
     */
    public String getName() {
        return name;
    }

    /**
     * @return the number of points that the player has scored except for additional ones.
     */
    public int getPoints() {
        return points;
    }

    /**
     * Sums together player points with additional points and saves the result as the current player points.
     * Additional points are reset to 0.
     */
    public void sumPoints() {
        points += additionalPoints;
        additionalPoints = 0;
    }

    /**
     * @return true iff the player is connected.
     */
    public boolean isConnected() {
        return isConnected;
    }

    /**
     * Sets the connection state of the DisplayablePlayer.
     *
     * @param isConnected must be true iff this DisplayablePlayer is connected to the server.
     */
    public void setConnectionState(boolean isConnected) {
        this.isConnected = isConnected;
    }

    /**
     * @return true iff the player is associated with the user on whose machine the program is running.
     */
    public boolean isClientPlayer() {
        return isClientPlayer;
    }

    /**
     * @return the position of the DisplayablePlayer in the score board.
     */
    public int getPosition() {
        return position;
    }

    /**
     * Sets the position of the DisplayablePlayer in the score board.
     *
     * @param position is the position of the DisplayablePlayer in the score board.
     */
    public void setPosition(int position) {
        if (position < 1) {
            throw new IllegalArgumentException("Positions start from 1");
        }

        this.position = position;
    }

    /**
     * @return the additional points that the player has scored by completing a goal recently.
     */
    public int getAdditionalPoints() {
        return additionalPoints;
    }

    /**
     * Sets the additional points of the DisplayablePlayer.
     *
     * @param additionalPoints is the number of points that the player has recently scored by completing a goal.
     */
    public void setAdditionalPoints(int additionalPoints) {
        if (additionalPoints < 1) {
            throw new IllegalArgumentException("You can't set non-positive additional points");
        }

        this.additionalPoints = additionalPoints;
    }

    /**
     * @return the actual points of the DisplayablePlayer, obtained summing the previous points of the player
     * with the additional ones recently scored.
     */
    private int getActualPoints() {
        return points + additionalPoints;
    }

    /**
     * @return true iff the player should be blurred inside the score board.
     */
    public boolean isBlurred() {
        return isBlurred;
    }

    /**
     * Sets if the DisplayablePlayer should be blurred or not inside the score board.
     *
     * @param isBlurred must be true iff the player should be blurred inside the score board.
     */
    public void setIsBlurred(boolean isBlurred) {
        this.isBlurred = isBlurred;
    }

    /**
     * @return true iff the DisplayablePlayer is a winner.
     */
    public boolean isWinner() {
        return isWinner;
    }

    /**
     * Sets if the DisplayablePlayer is a winner-
     *
     * @param isWinner must be true iff this DisplayablePlayer is a winner.
     */
    public void setIsWinner(boolean isWinner) {
        this.isWinner = isWinner;
    }

    /**
     * @return the index of this DisplayablePlayer in the original lobby ordering.
     */
    public int getOriginalIndex() {
        return originalIndex;
    }

    /**
     * Provides an ordering among DisplayablePlayers which can be used to fill the score board.
     *
     * @param other is the DisplayablePlayer which we will compare this DisplayablePlayer with.
     * @return 0 if this DisplayablePlayer should have the same position of other in the score board,
     * 1 if this DisplayablePlayer should be after the other in the score board, -1 otherwise.
     */
    public int compare(DisplayablePlayer other) {
        if (!isConnected && !other.isConnected) {
            return 0;
        }

        if (!isConnected && other.isConnected) {
            return 1;
        }

        if (isConnected && !other.isConnected) {
            return -1;
        }

        if (getActualPoints() == other.getActualPoints()) {
            return 0;
        } else if (getActualPoints() < other.getActualPoints()) {
            return 1;
        } else {
            return -1;
        }
    }

    @Override
    public String toString() {
        return "#" + position + " " + name + (!isConnected ? " (disconnected)" : "") + " [" + points +
            (additionalPoints > 0 ? " + " + additionalPoints : "") + "]";
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }

        if (!(other instanceof DisplayablePlayer)) {
            return false;
        }

        DisplayablePlayer otherDisplayablePlayer = (DisplayablePlayer) other;

        return name.equals(otherDisplayablePlayer.name) && points == otherDisplayablePlayer.points
            && isConnected == otherDisplayablePlayer.isConnected && originalIndex == otherDisplayablePlayer.originalIndex
            && isClientPlayer == otherDisplayablePlayer.isClientPlayer && position == otherDisplayablePlayer.position
            && additionalPoints == otherDisplayablePlayer.additionalPoints && isBlurred == otherDisplayablePlayer.isBlurred
            && isWinner == otherDisplayablePlayer.isWinner;
    }
}
