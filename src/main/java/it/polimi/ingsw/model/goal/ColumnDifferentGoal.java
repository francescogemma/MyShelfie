package it.polimi.ingsw.model.goal;

import it.polimi.ingsw.model.bookshelf.Shape;
import it.polimi.ingsw.model.evaluator.AtLeastEvaluator;
import it.polimi.ingsw.model.fetcher.ShapeFetcher;
import it.polimi.ingsw.model.filter.NumDifferentColorFilter;

/**
 * @author Giacomo Groppi
 * */
public class ColumnDifferentGoal extends CommonGoal {
    public ColumnDifferentGoal(int numPlayer) {
        super(
                new ShapeFetcher(Shape.getColumn(6)),
                new NumDifferentColorFilter(6, 6),
                new AtLeastEvaluator(numPlayer, 2, bookshelfMask -> bookshelfMask.getShelves().size() >= 2)
        );
    }
}
