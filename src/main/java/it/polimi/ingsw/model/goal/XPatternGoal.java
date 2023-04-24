package it.polimi.ingsw.model.goal;

import it.polimi.ingsw.model.bookshelf.Shape;
import it.polimi.ingsw.model.bookshelf.Shelf;
import it.polimi.ingsw.model.evaluator.AtLeastEvaluator;
import it.polimi.ingsw.model.evaluator.CommonGoalEvaluator;
import it.polimi.ingsw.model.fetcher.Fetcher;
import it.polimi.ingsw.model.fetcher.ShapeFetcher;
import it.polimi.ingsw.model.filter.Filter;
import it.polimi.ingsw.model.filter.NumDifferentColorFilter;
import it.polimi.ingsw.model.tile.TileColor;

import java.util.Map;

import static java.util.Map.entry;

/**
 * Five tiles of the same type forming an X.
 * @author Giacomo Groppi
 * */
public class XPatternGoal extends CommonGoal{
    /**
     * Constructor of the class.
     *
     * @see CommonGoal#CommonGoal(Fetcher, Filter, CommonGoalEvaluator, String, Map)
     * */
    public XPatternGoal() {
        super(new ShapeFetcher(Shape.X),
            new NumDifferentColorFilter(1, 1),
            new AtLeastEvaluator(1),
            "Five tiles\n of the same type\n forming an X.",
            Map.ofEntries(
                entry(Shelf.getInstance(1, 3), TileColor.YELLOW),
                entry(Shelf.getInstance(1, 1), TileColor.YELLOW),
                entry(Shelf.getInstance(2, 2), TileColor.YELLOW),
                entry(Shelf.getInstance(3, 3), TileColor.YELLOW),
                entry(Shelf.getInstance(3, 1), TileColor.YELLOW)
            )
        );
    }
}
