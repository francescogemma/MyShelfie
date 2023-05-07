package it.polimi.ingsw.view.displayable;

import it.polimi.ingsw.model.bookshelf.BookshelfMaskSet;

public class DisplayableGoal {
    public enum GoalType {
        COMMON("common"),
        PERSONAL("personal"),
        ADJACENCY("final");

        private String string;

        GoalType(String string) {
            this.string = string;
        }

        public String toString() {
            return string;
        }
    }

    private String playerName;
    private GoalType type;
    private boolean isFirstCommonGoal;
    private int points;
    private BookshelfMaskSet maskSet;

    public DisplayableGoal(String playerName, GoalType type, boolean isFirstCommonGoal,
                           int points, BookshelfMaskSet maskSet) {
        this.playerName = playerName;
        this.type = type;
        this.isFirstCommonGoal = isFirstCommonGoal;
        this.points = points;
        this.maskSet = maskSet;
    }

    public String getPlayerName() {
        return playerName;
    }

    public GoalType getType() {
        return type;
    }

    public boolean isFirstCommonGoal() {
        return isFirstCommonGoal;
    }

    public int getPoints() {
        return points;
    }

    public BookshelfMaskSet getMaskSet() {
        return new BookshelfMaskSet(maskSet);
    }

    public String toText() {
        return playerName + " completed " + (type == GoalType.COMMON ? "a " : "the ") + type +
            " goal!";
    }
}
