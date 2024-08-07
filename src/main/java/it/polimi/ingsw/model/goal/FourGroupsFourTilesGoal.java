package it.polimi.ingsw.model.goal;

import it.polimi.ingsw.model.bookshelf.Shelf;
import it.polimi.ingsw.model.evaluator.AtLeastEvaluator;
import it.polimi.ingsw.model.fetcher.AdjacencyFetcher;
import it.polimi.ingsw.model.filter.NumDifferentColorFilter;
import it.polimi.ingsw.model.tile.TileColor;

import java.util.Map;

import static java.util.Map.entry;

/**
 * Four groups each containing at least
 * 4 tiles of the same type (not necessarily
 * in the depicted shape).
 * The tiles of one group can be different
 * from those of another group.
 * @author Cristiano Migali
 */
public class FourGroupsFourTilesGoal extends CommonGoal {
    /**
     * Constructor of the class.
     * It takes:
     * <ul>
     *     <li> a {@link AdjacencyFetcher} to fetch all groups of adjacent tiles; </li>
     *     <li> a {@link NumDifferentColorFilter} to filter out groups of tiles with the same color; </li>
     *     <li> an {@link AtLeastEvaluator} with targetAmount equal to 4 consenting at least 4 groups of tiles
     *     with at least 4 tiles of the same color; </li>
     *     <li> a {@link String} representing the description on the goal; </li>
     *     <li> a {@link Map} representing an example of the goal to display. </li>
     * </ul>
     */
    public FourGroupsFourTilesGoal() {
        super(  new AdjacencyFetcher(),
                new NumDifferentColorFilter(1, 1),
                new AtLeastEvaluator(4, bookshelfMask -> bookshelfMask.getShelves().size() >= 4),
                "Four groups\neach containing at least\nfour tiles of\nthe same type.\n" +
                        "The tiles of one group\ncan be different from\nthose of another group.",
                Map.ofEntries(
                        entry(Shelf.getInstance(0, 0), TileColor.BLUE),
                        entry(Shelf.getInstance(0, 1), TileColor.BLUE),
                        entry(Shelf.getInstance(1, 0), TileColor.BLUE),
                        entry(Shelf.getInstance(1, 1), TileColor.BLUE),
                        entry(Shelf.getInstance(0, 3), TileColor.GREEN),
                        entry(Shelf.getInstance(1, 3), TileColor.GREEN),
                        entry(Shelf.getInstance(2, 3), TileColor.GREEN),
                        entry(Shelf.getInstance(3, 3), TileColor.GREEN),
                        entry(Shelf.getInstance(5, 2), TileColor.MAGENTA),
                        entry(Shelf.getInstance(5, 3), TileColor.MAGENTA),
                        entry(Shelf.getInstance(5, 4), TileColor.MAGENTA),
                        entry(Shelf.getInstance(4, 4), TileColor.MAGENTA),
                        entry(Shelf.getInstance(3, 0), TileColor.YELLOW),
                        entry(Shelf.getInstance(3, 1), TileColor.YELLOW),
                        entry(Shelf.getInstance(4, 0), TileColor.YELLOW),
                        entry(Shelf.getInstance(5, 0), TileColor.YELLOW)
                )
        );
    }
}
