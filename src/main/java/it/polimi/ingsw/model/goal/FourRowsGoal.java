package it.polimi.ingsw.model.goal;

import it.polimi.ingsw.model.bookshelf.Shape;
import it.polimi.ingsw.model.evaluator.AtLeastEvaluator;
import it.polimi.ingsw.model.evaluator.CommonGoalEvaluator;
import it.polimi.ingsw.model.fetcher.Fetcher;
import it.polimi.ingsw.model.fetcher.ShapeFetcher;
import it.polimi.ingsw.model.filter.Filter;
import it.polimi.ingsw.model.filter.NumDifferentColorFilter;

/**
 * This class represents a common goal:
 * <p>
 * Four lines each formed by 5 tiles of maximum three different types,
 * one line can show the same or a different combination of another line.
 * <p>
 * It extends CommonGoal.
 *
 * @see CommonGoal
 * @see Goal
 *
 * @author Francesco Gemma
 */
public class FourRowsGoal extends CommonGoal {
    /**
     * Constructor of the class.
     * It takes:
     * <ul>
     *     <li> a {@link ShapeFetcher} to fetch a row of width 5; </li>
     *     <li> a {@link NumDifferentColorFilter} to filter the rows with maximum three different colors; </li>
     *     <li> an {@link AtLeastEvaluator} with targetAmount equal to 4, because we need to find at least 4 rows. </li>
     * </ul>
     *
     * @see CommonGoal#CommonGoal(Fetcher, Filter, CommonGoalEvaluator)
     */
    public FourRowsGoal() {
        super(
                new ShapeFetcher(Shape.getRow(5)),
                new NumDifferentColorFilter(1, 3),
                new AtLeastEvaluator(4)

        );
    }
}
