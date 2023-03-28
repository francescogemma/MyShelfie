package it.polimi.ingsw.model.goal;

import it.polimi.ingsw.model.bookshelf.BookshelfMask;
import it.polimi.ingsw.model.bookshelf.Shape;
import it.polimi.ingsw.model.evaluator.CompatibleEvaluator;
import it.polimi.ingsw.model.fetcher.ShapeFetcher;
import it.polimi.ingsw.model.filter.NumDifferentColorFilter;

public class TwoTwoByTwoSquaresGoal extends CommonGoal {
    public TwoTwoByTwoSquaresGoal(int numPlayers) {
        super(  new ShapeFetcher(Shape.SQUARE),
                new NumDifferentColorFilter(1, 1),
                new CompatibleEvaluator(numPlayers, 2, BookshelfMask.DO_NOT_INTERSECT));
    }
}
