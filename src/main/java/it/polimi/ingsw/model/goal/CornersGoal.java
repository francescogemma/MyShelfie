package it.polimi.ingsw.model.goal;

import it.polimi.ingsw.model.bookshelf.Shape;
import it.polimi.ingsw.model.evaluator.AtLeastEvaluator;
import it.polimi.ingsw.model.fetcher.ShapeFetcher;
import it.polimi.ingsw.model.filter.NumDifferentColorFilter;

public class CornersGoal extends CommonGoal {
    public CornersGoal(int numPlayers) {
        super(  new ShapeFetcher(Shape.CORNERS),
                new NumDifferentColorFilter(1, 1),
                new AtLeastEvaluator(numPlayers, 1));
    }
}
