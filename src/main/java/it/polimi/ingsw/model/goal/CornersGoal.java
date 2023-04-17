package it.polimi.ingsw.model.goal;

import it.polimi.ingsw.model.bookshelf.Shape;
import it.polimi.ingsw.model.evaluator.AtLeastEvaluator;
import it.polimi.ingsw.model.evaluator.CommonGoalEvaluator;
import it.polimi.ingsw.model.evaluator.Evaluator;
import it.polimi.ingsw.model.fetcher.Fetcher;
import it.polimi.ingsw.model.fetcher.ShapeFetcher;
import it.polimi.ingsw.model.filter.Filter;
import it.polimi.ingsw.model.filter.NumDifferentColorFilter;

/**
 * This class represents a common goal:
 * <p>
 * Four tiles of the same type in the four corners of the bookshelf.
 * <p>
 * It extends CommonGoal.
 *
 * @see CommonGoal
 * @see Goal
 *
 * @author Cristiano Migali
 */
public class CornersGoal extends CommonGoal {
    /**
     * Constructor of the class.
     * It takes:
     * <ul>
     *     <li> a {@link ShapeFetcher} to fetch the shape of the four corners; </li>
     *     <li> a {@link NumDifferentColorFilter} with only one color, because the tiles has to be of the same type; </li>
     *     <li> an {@link AtLeastEvaluator} with targetAmount equal to 1
     *     (CORNERS shape can fit only one time in the bookshelf). </li>
     * </ul>
     *
     * @see CommonGoal#CommonGoal(Fetcher, Filter, CommonGoalEvaluator)
     */
    public CornersGoal() {
        super(  new ShapeFetcher(Shape.CORNERS),
                new NumDifferentColorFilter(1, 1),
                new AtLeastEvaluator(1));
    }
}
