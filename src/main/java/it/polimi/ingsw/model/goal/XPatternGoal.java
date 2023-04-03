package it.polimi.ingsw.model.goal;

import it.polimi.ingsw.model.bookshelf.Shape;
import it.polimi.ingsw.model.evaluator.AtLeastEvaluator;
import it.polimi.ingsw.model.evaluator.Evaluator;
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
     * @see CommonGoal#CommonGoal(Fetcher, Filter, Evaluator)
     *
     * @param numPlayers the number of players in the game
     * */
    public XPatternGoal(int numPlayers) {
        super(new ShapeFetcher(Shape.X),
            new NumDifferentColorFilter(1, 1),
            new AtLeastEvaluator(numPlayers, 1)
            );
    }
}
