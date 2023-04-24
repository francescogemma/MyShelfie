package it.polimi.ingsw.model.goal;

import it.polimi.ingsw.model.bookshelf.BookshelfMask;
import it.polimi.ingsw.model.bookshelf.Shape;
import it.polimi.ingsw.model.bookshelf.Shelf;
import it.polimi.ingsw.model.evaluator.CompatibleEvaluator;
import it.polimi.ingsw.model.fetcher.ShapeFetcher;
import it.polimi.ingsw.model.filter.NumDifferentColorFilter;
import it.polimi.ingsw.model.tile.TileColor;

import java.util.Map;

import static java.util.Map.entry;

/**
 * Two groups each containing 4 tiles of
 * the same type in a 2x2 square. The tiles
 * of one square can be different from
 * those of the other square.
 * @author Cristiano Migali
 */
public class TwoTwoByTwoSquaresGoal extends CommonGoal {
    public TwoTwoByTwoSquaresGoal() {
        super(  new ShapeFetcher(Shape.SQUARE),
                new NumDifferentColorFilter(1, 1),
                new CompatibleEvaluator(2, BookshelfMask.DO_NOT_INTERSECT),
                "Two groups\n each containing 4\n tiles of the same\n type in a 2x2 square.\n" +
                        "The tiles of one\n square can be\n different from those\n of the other square.",
                Map.ofEntries(
                        entry(Shelf.getInstance(0, 0), TileColor.MAGENTA),
                        entry(Shelf.getInstance(1, 0), TileColor.MAGENTA),
                        entry(Shelf.getInstance(0, 1), TileColor.MAGENTA),
                        entry(Shelf.getInstance(1, 1), TileColor.MAGENTA),
                        entry(Shelf.getInstance(3, 2), TileColor.BLUE),
                        entry(Shelf.getInstance(3, 3), TileColor.BLUE),
                        entry(Shelf.getInstance(4, 2), TileColor.BLUE),
                        entry(Shelf.getInstance(4, 3), TileColor.BLUE)
                )
        );
    }
}
