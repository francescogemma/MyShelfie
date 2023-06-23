package it.polimi.ingsw.view.displayable;

import it.polimi.ingsw.model.bookshelf.BookshelfMaskSet;

/**
 * Represents a goal that has been completed by a player and has to be displayed.
 * A DisplayableGoal has a type: common, personal or final and indicates the player that has completed
 * it, the number of points that the player has scored and the {@link BookshelfMaskSet} that made the player
 * score those points.
 *
 * @author Cristiano Migali
 */
public class DisplayableGoal {
    /**
     * Represents the type of the DisplayableGoal among common, personal or final.
     */
    public enum GoalType {
        /**
         * It is the type of common goals.
         */
        COMMON("common"),

        /**
         * It is the type of personal goals.
         */
        PERSONAL("personal"),

        /**
         * It is the type of the goal completed at the end of the game by putting tiles of the same color in
         * adjacent cells.
         */
        ADJACENCY("final");

        /**
         * It is a string with the name of the goal type.
         */
        private String string;

        /**
         * Constructor of the class.
         * It initializes the string with the name of the goal type.
         *
         * @param string is the name of the goal type.
         */
        GoalType(String string) {
            this.string = string;
        }

        @Override
        public String toString() {
            return string;
        }
    }

    /**
     * It is the name of the player that has completed the goal.
     */
    private String playerName;

    /**
     * It is the type of the goal that has been completed among common, personal and final.
     */
    private GoalType type;

    /**
     * It is true iff the completed goal is a common one and in particular it is the first common goal of the game.
     * It allows to distinguish which one of the common goals has been completed.
     */
    private boolean isFirstCommonGoal;

    /**
     * It is the number of points that the user has scored by completing the common goal.
     */
    private int points;

    /**
     * It is the {@link BookshelfMaskSet} which allowed the player to complete the goal.
     */
    private BookshelfMaskSet maskSet;

    /**
     * Constructor of the class.
     *
     * @param playerName is the name of the player that has completed the goal.
     * @param type is the type of the goal that has been completed among common, personal and final.
     * @param isFirstCommonGoal it must be true iff the completed goal is a common one and in particular it is the first
     *                          common goal of the game.
     * @param points it is the number of points that the player has scored by completing the goal.
     * @param maskSet it is the {@link BookshelfMaskSet} which allowed the player to complete the goal.
     */
    public DisplayableGoal(String playerName, GoalType type, boolean isFirstCommonGoal,
                           int points, BookshelfMaskSet maskSet) {
        this.playerName = playerName;
        this.type = type;
        this.isFirstCommonGoal = isFirstCommonGoal;
        this.points = points;
        this.maskSet = maskSet;
    }

    /**
     * @return the name of the player that has completed the goal.
     */
    public String getPlayerName() {
        return playerName;
    }

    /**
     * @return the type of the goal that has been completed among common, personal and final.
     */
    public GoalType getType() {
        return type;
    }

    /**
     * @return true iff the completed goal is a common one and in particular it is the first common goal of the
     * game.
     */
    public boolean isFirstCommonGoal() {
        return isFirstCommonGoal;
    }

    /**
     * @return the number of points that the player has scored by completing the goal.
     */
    public int getPoints() {
        return points;
    }

    /**
     * @return the {@link BookshelfMaskSet} which allowed the player to complete the goal.
     */
    public BookshelfMaskSet getMaskSet() {
        return new BookshelfMaskSet(maskSet);
    }

    /**
     * @return a text representation of the completed goal which can be shown on a pop-up.
     */
    public String toText() {
        return playerName + " completed " + (type == GoalType.COMMON ? "a " : "the ") + type +
            " goal!";
    }
}
