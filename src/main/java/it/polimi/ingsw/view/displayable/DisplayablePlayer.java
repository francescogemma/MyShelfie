package it.polimi.ingsw.view.displayable;

import it.polimi.ingsw.model.game.Player;

public class DisplayablePlayer {
    private String name;
    private int points;
    private boolean isConnected;
    private int originalIndex;
    private boolean isClientPlayer;

    private int position = 0;
    private int additionalPoints = 0;
    private boolean isBlurred = false;
    private boolean isWinner = false;

    public DisplayablePlayer(Player player,
                             int originalIndex,
                             boolean isClientPlayer) {

        this.name = player.getUsername();
        this.points = player.getPoints();
        this.isConnected = player.isConnected();
        this.originalIndex = originalIndex;
        this.isClientPlayer = isClientPlayer;
    }

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

    public String getName() {
        return name;
    }

    public int getPoints() {
        return points;
    }

    public void sumPoints() {
        points += additionalPoints;
        additionalPoints = 0;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnectionState(boolean isConnected) {
        this.isConnected = isConnected;
    }

    public boolean isClientPlayer() {
        return isClientPlayer;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        if (position < 1) {
            throw new IllegalArgumentException("Positions start from 1");
        }

        this.position = position;
    }

    public int getAdditionalPoints() {
        return additionalPoints;
    }

    public void setAdditionalPoints(int additionalPoints) {
        if (additionalPoints < 1) {
            throw new IllegalArgumentException("You can't set non-positive additional points");
        }

        this.additionalPoints = additionalPoints;
    }

    private int getActualPoints() {
        return points + additionalPoints;
    }

    public boolean isBlurred() {
        return isBlurred;
    }

    public void setIsBlurred(boolean isBlurred) {
        this.isBlurred = isBlurred;
    }

    public boolean isWinner() {
        return isWinner;
    }

    public void setIsWinner(boolean isWinner) {
        this.isWinner = isWinner;
    }

    public int getOriginalIndex() {
        return originalIndex;
    }

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
}
