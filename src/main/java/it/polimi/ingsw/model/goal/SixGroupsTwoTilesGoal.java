package it.polimi.ingsw.model.goal;

import it.polimi.ingsw.model.bookshelf.Shelf;
import it.polimi.ingsw.model.evaluator.AtLeastEvaluator;
import it.polimi.ingsw.model.fetcher.AdjacencyFetcher;
import it.polimi.ingsw.model.filter.NumDifferentColorFilter;
import it.polimi.ingsw.model.tile.TileColor;

import java.util.Map;

import static java.util.Map.entry;

/**
 * Six groups each containing at least
 * 2 tiles of the same type (not necessarily
 * in the depicted shape).
 * The tiles of one group can be different
 * from those of another group.
 * @author Cristiano Migali
 */
public class SixGroupsTwoTilesGoal extends CommonGoal {
    /**
     * Constructor of the class.
     * It takes:
     * <ul>
     *     <li> a {@link AdjacencyFetcher} to fetch all groups of adjacent tiles; </li>
     *     <li> a {@link NumDifferentColorFilter} to filter out groups of tiles with the same color; </li>
     *     <li> an {@link AtLeastEvaluator} with targetAmount equal to 6 consenting at least 6 groups of tiles
     *     with at least 2 tiles of the same color; </li>
     *     <li> a {@link String} representing the description on the goal; </li>
     *     <li> a {@link Map} representing an example of the goal to display. </li>
     * </ul>
     */
    public SixGroupsTwoTilesGoal() {
        super(  new AdjacencyFetcher(),
                new NumDifferentColorFilter(1, 1),
                new AtLeastEvaluator(6, bookshelfMask -> bookshelfMask.getShelves().size() >= 2),
                "Six groups\neach containing\nat least two tiles\nof the same type.\n" +
                        "The tiles of\none group can be\ndifferent from those\nof another group.",
                Map.ofEntries(
                        entry(Shelf.getInstance(0, 0), TileColor.GREEN),
                        entry(Shelf.getInstance(0, 1), TileColor.GREEN),
                        entry(Shelf.getInstance(0, 3), TileColor.BLUE),
                        entry(Shelf.getInstance(0, 4), TileColor.BLUE),
                        entry(Shelf.getInstance(2, 0), TileColor.YELLOW),
                        entry(Shelf.getInstance(3, 0), TileColor.YELLOW),
                        entry(Shelf.getInstance(2, 2), TileColor.CYAN),
                        entry(Shelf.getInstance(3, 2), TileColor.CYAN),
                        entry(Shelf.getInstance(2, 4), TileColor.MAGENTA),
                        entry(Shelf.getInstance(3, 4), TileColor.MAGENTA),
                        entry(Shelf.getInstance(5, 0), TileColor.WHITE),
                        entry(Shelf.getInstance(5, 1), TileColor.WHITE),
                        entry(Shelf.getInstance(5, 2), TileColor.WHITE),
                        entry(Shelf.getInstance(5, 3), TileColor.WHITE)
                )
        );
    }
}
