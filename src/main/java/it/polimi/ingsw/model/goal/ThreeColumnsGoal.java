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
 * Three columns each formed by 6 tiles of maximum three different types,
 * one column can show the same or a different combination of another column.
 * <p>
 * It extends CommonGoal.
 *
 * @see CommonGoal
 * @see Goal
 *
 * @author Francesco Gemma
 */
public class ThreeColumnsGoal extends CommonGoal {
    /**
     * Constructor of the class.
     * It takes:
     * <ul>
     *     <li> a {@link ShapeFetcher} to fetch a column of height 6; </li>
     *     <li> a {@link NumDifferentColorFilter} to filter the columns with maximum three different colors; </li>
     *     <li> an {@link AtLeastEvaluator} with targetAmount equal to 3, because we need to find at least 3 columns. </li>
     * </ul>
     *
     * @see CommonGoal#CommonGoal(Fetcher, Filter, CommonGoalEvaluator)
     */
    public ThreeColumnsGoal() {
        super(
                new ShapeFetcher(Shape.getColumn(6)),
                new NumDifferentColorFilter(1, 3),
                new AtLeastEvaluator(3)
        );
    }
}
