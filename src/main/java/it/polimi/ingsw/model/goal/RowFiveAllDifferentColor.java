package it.polimi.ingsw.model.goal;

import it.polimi.ingsw.model.bookshelf.Shape;
import it.polimi.ingsw.model.evaluator.AtLeastEvaluator;
import it.polimi.ingsw.model.fetcher.ShapeFetcher;
import it.polimi.ingsw.model.filter.NumDifferentColorFilter;

/**
 * Two rows, each consisting of 5 different types of tiles.
 * @author Giacomo Groppi
 * */
public class RowFiveAllDifferentColor extends CommonGoal{
    protected RowFiveAllDifferentColor() {
        super(
                new ShapeFetcher(Shape.getRow(5)),
                new NumDifferentColorFilter(5, 5),
                new AtLeastEvaluator(2, bookshelfMask -> bookshelfMask.getShelves().size() >= 2)
        );
    }
}
