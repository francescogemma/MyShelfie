package it.polimi.ingsw.model.goal;

import it.polimi.ingsw.model.evaluator.AtLeastEvaluator;
import it.polimi.ingsw.model.fetcher.AdjacencyFetcher;
import it.polimi.ingsw.model.filter.NumDifferentColorFilter;

public class SixGroupsTwoTilesGoal extends CommonGoal {
    public SixGroupsTwoTilesGoal(int numPlayers) {
        super(  new AdjacencyFetcher(),
                new NumDifferentColorFilter(1, 1),
                new AtLeastEvaluator(numPlayers, 2, bookshelfMask -> bookshelfMask.getShelves().size() >= 4));
    }
}
