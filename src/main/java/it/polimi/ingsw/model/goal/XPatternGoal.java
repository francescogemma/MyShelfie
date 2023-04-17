package it.polimi.ingsw.model.goal;

import it.polimi.ingsw.model.bookshelf.Shape;
import it.polimi.ingsw.model.evaluator.AtLeastEvaluator;
import it.polimi.ingsw.model.evaluator.CommonGoalEvaluator;
import it.polimi.ingsw.model.fetcher.Fetcher;
import it.polimi.ingsw.model.fetcher.ShapeFetcher;
import it.polimi.ingsw.model.filter.Filter;
import it.polimi.ingsw.model.filter.NumDifferentColorFilter;

/**
 * Five tiles of the same type forming an X
 * @author Giacomo Groppi
 * */
public class XPatternGoal extends CommonGoal{
    /**
     * Constructor of the class.
     *
     * @see CommonGoal#CommonGoal(Fetcher, Filter, CommonGoalEvaluator)
     * */
    public XPatternGoal() {
        super(new ShapeFetcher(Shape.X),
            new NumDifferentColorFilter(1, 1),
            new AtLeastEvaluator(1)
            );
    }
}
