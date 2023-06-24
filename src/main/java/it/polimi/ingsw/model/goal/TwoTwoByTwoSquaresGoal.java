package it.polimi.ingsw.model.goal;

import it.polimi.ingsw.model.bookshelf.BookshelfMask;
import it.polimi.ingsw.model.bookshelf.Shape;
import it.polimi.ingsw.model.bookshelf.Shelf;
import it.polimi.ingsw.model.evaluator.CommonGoalEvaluator;
import it.polimi.ingsw.model.evaluator.CompatibleEvaluator;
import it.polimi.ingsw.model.fetcher.Fetcher;
import it.polimi.ingsw.model.fetcher.ShapeFetcher;
import it.polimi.ingsw.model.filter.Filter;
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
    /**
     * Constructor of the class.
     * It takes:
     * <ul>
     *     <li> an {@link ShapeFetcher} to fetch squares; </li>
     *     <li> a {@link NumDifferentColorFilter} to filter the tiles with the same color; </li>
     *     <li> an {@link CompatibleEvaluator} with targetGroupSize equal to 2; </li>
     *     <li> a {@link String} representing the description on the goal; </li>
     *     <li> a {@link Map} representing an example of the goal to display. </li>
     * </ul>
     *
     * @see CommonGoal#CommonGoal(Fetcher, Filter, CommonGoalEvaluator, String, Map)
     */
    public TwoTwoByTwoSquaresGoal() {
        super(  new ShapeFetcher(Shape.SQUARE),
                new NumDifferentColorFilter(1, 1),
                new CompatibleEvaluator(2, BookshelfMask.DO_NOT_INTERSECT),
                "Two groups\neach containing four\ntiles of the same\ntype in a 2x2 square.\n" +
                        "The tiles of one\nsquare can be\ndifferent from those\nof the other square.",
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
