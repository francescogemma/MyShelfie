package it.polimi.ingsw.model.goal;

import it.polimi.ingsw.model.bookshelf.Shelf;
import it.polimi.ingsw.model.evaluator.CommonGoalEvaluator;
import it.polimi.ingsw.model.evaluator.Evaluator;
import it.polimi.ingsw.model.fetcher.Fetcher;
import it.polimi.ingsw.model.filter.Filter;
import it.polimi.ingsw.model.tile.TileColor;

import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * This abstract class represents a common goal. It extends Goal.
 * <p>
 * All common goals should extend this class.
 * {@see Goal}
 *
 * @author Francesco Gemma
 */
public abstract class CommonGoal extends IndexedGoal {
    private final String description;
    private final Map<Shelf, TileColor> display;

    /**
     * Constructor of the class. It simply calls the constructor of the superclass {@link Goal},
     * and sets the description of the goal and an example of it to display.
     *
     * @param fetcher the {@link Fetcher} type used to fetch the shape of the goal;
     * @param filter the {@link Filter} type used for the goal;
     * @param evaluator the {@link Evaluator} type used to evaluate the goal.
     * @param description the description of the goal.
     * @param display an example of the goal completed.
     */
    protected CommonGoal(Fetcher fetcher, Filter filter, CommonGoalEvaluator evaluator, String description, Map<Shelf, TileColor> display) {
        super(fetcher, filter, evaluator);
        this.description = description;
        this.display = display;
    }

    /**
     * This method returns an array containing two random common goals (not repeated).
     * @return an array containing two random common goals
     */
    public static CommonGoal[] getTwoRandomCommonGoals(int playersAmount) {
        int firstIndex = new Random().nextInt(12);
        int secondIndex = new Random().nextInt(11);
        if (secondIndex == firstIndex) {
            secondIndex++;
        }

        return new CommonGoal[]{ fromIndex(firstIndex, playersAmount), fromIndex(secondIndex, playersAmount) };
    }

    /**
     * @param index the index of the common goal.
     * @return a new common goal which type depends on the index
     * @throws IllegalArgumentException if the index is not between 0 and 11, or if the number of players is not between 2 and 4
     * @throws IllegalStateException if we try to create a common goal from index which is not between 0 and 11
     */
    public static CommonGoal fromIndex(int index) {
        if (index < 0 || index >= 12) {
            throw new IllegalArgumentException("Common goal index must be between 0 and 12, got: " + index);
        }

        return switch (index) {
            case 0 -> (CommonGoal) new ColumnDifferentGoal().index(0);
            case 1 -> (CommonGoal) new CornersGoal().index(1);
            case 2 -> (CommonGoal) new DiagonalGoal().index(2);
            case 3 -> (CommonGoal) new EightTilesGoal().index(3);
            case 4 -> (CommonGoal) new FourGroupsFourTilesGoal().index(4);
            case 5 -> (CommonGoal) new FourRowsGoal().index(5);
            case 6 -> (CommonGoal) new RowFiveAllDifferentColor().index(6);
            case 7 -> (CommonGoal) new SixGroupsTwoTilesGoal().index(7);
            case 8 -> (CommonGoal) new StairGoal().index(8);
            case 9 -> (CommonGoal) new ThreeColumnsGoal().index(9);
            case 10 -> (CommonGoal) new TwoTwoByTwoSquaresGoal().index(10);
            case 11 -> (CommonGoal) new XPatternGoal().index(11);
            default -> throw new IllegalStateException("We've already checked that index is between 0 and 11");
        };
    }

    /**
     * @param index the index of the common goal.
     * @param playersAmount the number of players in the game.
     * @return a new common goal which type depends on the index, with the point stack set according to the number of players
     */
    public static CommonGoal fromIndex(int index, int playersAmount) {
        CommonGoal commonGoal = fromIndex(index);

        if (playersAmount < 2 || playersAmount > 4) {
            throw new IllegalArgumentException("The number of players must be between 0 and 4");
        }

        switch (playersAmount) {
            case 2 -> commonGoal.setPointStack(List.of(4, 8));
            case 3 -> commonGoal.setPointStack(List.of(4, 6, 8));
            case 4 -> commonGoal.setPointStack(List.of(2, 4, 6, 8));

            default -> throw new IllegalArgumentException("Players must be between 2 and 4.");
        }

        return commonGoal;
    }

    public List<Integer> getPointStack() {
        return ((CommonGoalEvaluator) evaluator).getPointStack();
    }

    public void setPointStack(List<Integer> pointStack) {
        ((CommonGoalEvaluator) evaluator).setPointStack(pointStack);
    }

    public String getDescription() {
        return description;
    }

    public Map<Shelf, TileColor> getDisplay() {
        return display;
    }
}
