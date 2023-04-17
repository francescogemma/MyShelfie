package it.polimi.ingsw.model.goal;

import it.polimi.ingsw.model.bookshelf.Shape;
import it.polimi.ingsw.model.evaluator.CommonGoalEvaluator;
import it.polimi.ingsw.model.evaluator.EightTilesGoalEvaluator;
import it.polimi.ingsw.model.evaluator.Evaluator;
import it.polimi.ingsw.model.fetcher.Fetcher;
import it.polimi.ingsw.model.fetcher.ShapeFetcher;
import it.polimi.ingsw.model.filter.AcceptsAllFilter;
import it.polimi.ingsw.model.filter.Filter;


/**
 * This class represents a common goal:
 * <p>
 * Eight tiles of the same type, there’s no restriction about the position of these tiles.
 * <p>
 * It extends CommonGoal.
 *
 * @see CommonGoal
 * @see Goal
 *
 * @author Francesco Gemma
 */
public class EightTilesGoal extends CommonGoal {
    /**
     * Constructor of the class.
     * It takes:
     * <ul>
     *     <li> a {@link ShapeFetcher} to fetch the whole bookshelf, because there's no restriction about the position of these tiles; </li>
     *     <li> an {@link AcceptsAllFilter} because we need to fetch the whole bookshelf; </li>
     *     <li> an {@link EightTilesGoalEvaluator} with playersAmount equal to 1 that will count the number of tiles of the same type.
     *     and will give us the score. </li>
     * </ul>
     *
     * @see CommonGoal#CommonGoal(Fetcher, Filter, CommonGoalEvaluator)
     */
    public EightTilesGoal() {
        super(
                new ShapeFetcher(Shape.WHOLE_BOOKSHELF),
                new AcceptsAllFilter(),
                new EightTilesGoalEvaluator()
        );
    }
}
