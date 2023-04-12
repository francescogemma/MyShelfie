package it.polimi.ingsw.model.goal;

import it.polimi.ingsw.model.evaluator.Evaluator;
import it.polimi.ingsw.model.fetcher.Fetcher;
import it.polimi.ingsw.model.filter.Filter;

import java.util.Random;

/**
 * This abstract class represents a common goal. It extends Goal.
 * <p>
 * All common goals should extend this class.
 *
 * {@see Goal}
 *
 * @author Francesco Gemma
 */
public abstract class CommonGoal extends Goal{
    /**
     * Constructor of the class. It simply calls the constructor of the superclass {@link Goal}.
     *
     * @param fetcher the {@link Fetcher} type used to fetch the shape of the goal;
     * @param filter the {@link Filter} type used for the goal;
     * @param evaluator the {@link Evaluator} type used to evaluate the goal.
     */
    protected CommonGoal(Fetcher fetcher, Filter filter, Evaluator evaluator) {
        super(fetcher, filter, evaluator);
    }

    // TODO: Add getTwoRandomCommonGoals
    public static CommonGoal[] getTwoRandomCommonGoals(int numPlayers) {
        int firstIndex = new Random().nextInt(12);
        int secondIndex = new Random().nextInt(11);
        if (secondIndex == firstIndex) {
            secondIndex++;
        }

        return new CommonGoal[]{ fromIndex(firstIndex, numPlayers), fromIndex(secondIndex, numPlayers) };
    }

    private static CommonGoal fromIndex(int index, int numPlayers) {
        if (index < 0 || index >= 12) {
            throw new IllegalArgumentException("Common goal index must be between 0 and 12, got: " + index);
        }

        if (numPlayers < 2 || numPlayers > 4) {
            throw new IllegalArgumentException("The number of players must be between 0 and 4");
        }

        return switch (index) {
            case 0 -> new ColumnDifferentGoal(numPlayers);
            case 1 -> new CornersGoal(numPlayers);
            case 2 -> new DiagonalGoal(numPlayers);
            case 3 -> new EightTilesGoal(numPlayers);
            case 4 -> new FourGroupsFourTilesGoal(numPlayers);
            case 5 -> new FourRowsGoal(numPlayers);
            case 6 -> new RowFiveAllDifferentColor(numPlayers);
            case 7 -> new SixGroupsTwoTilesGoal(numPlayers);
            case 8 -> new StairGoal(numPlayers);
            case 9 -> new ThreeColumnsGoal(numPlayers);
            case 10 -> new TwoTwoByTwoSquaresGoal(numPlayers);
            case 11 -> new XPatternGoal(numPlayers);
            default -> throw new IllegalStateException("We've already checked that index is between 0 and 11");
        };
    }
}
