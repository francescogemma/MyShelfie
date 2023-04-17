package it.polimi.ingsw.model.goal;

import it.polimi.ingsw.model.evaluator.AtLeastEvaluator;
import it.polimi.ingsw.model.fetcher.AdjacencyFetcher;
import it.polimi.ingsw.model.filter.NumDifferentColorFilter;

/**
 * Six groups each containing at least
 * 2 tiles of the same type (not necessarily
 * in the depicted shape).
 * The tiles of one group can be different
 * from those of another group.
 * @author Cristiano Migali
 */
public class SixGroupsTwoTilesGoal extends CommonGoal {
    public SixGroupsTwoTilesGoal() {
        super(  new AdjacencyFetcher(),
                new NumDifferentColorFilter(1, 1),
                new AtLeastEvaluator(6, bookshelfMask -> bookshelfMask.getShelves().size() >= 2));
    }
}
