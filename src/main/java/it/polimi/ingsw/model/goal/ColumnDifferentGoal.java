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
 * This class represents a common goal: two columns made up of all different colors.
 * @author Giacomo Groppi
 * */
public class ColumnDifferentGoal extends CommonGoal {
    /**
     * Constructor of the class.
     * It takes:
     * <ul>
     *     <li> a {@link ShapeFetcher} to fetch a column of width 6; </li>
     *     <li> a {@link NumDifferentColorFilter} to filter the column with at least six different colors; </li>
     *     <li> an {@link AtLeastEvaluator} with targetAmount equal to 2, because we need to find at least 2 column; </li>
     *     <li> a {@link String} representing the description on the goal; </li>
     *     <li> a {@link Map} representing an example of the goal to display. </li>
     * </ul>
     *
     * @see CommonGoal#CommonGoal(Fetcher, Filter, CommonGoalEvaluator, String, Map)
     */
    public ColumnDifferentGoal() {
        super(
                new ShapeFetcher(Shape.getColumn(6)),
                new NumDifferentColorFilter(6, 6),
                new AtLeastEvaluator(2, bookshelfMask -> bookshelfMask.getShelves().size() >= 2),
                "Two columns made up,\neach one, of\nall different colors.",
                Map.ofEntries(
                        entry(Shelf.getInstance(0, 1), TileColor.MAGENTA),
                        entry(Shelf.getInstance(1, 1), TileColor.BLUE),
                        entry(Shelf.getInstance(2, 1), TileColor.GREEN),
                        entry(Shelf.getInstance(3, 1), TileColor.WHITE),
                        entry(Shelf.getInstance(4, 1), TileColor.YELLOW),
                        entry(Shelf.getInstance(5, 1), TileColor.CYAN),
                        entry(Shelf.getInstance(0, 3), TileColor.MAGENTA),
                        entry(Shelf.getInstance(1, 3), TileColor.BLUE),
                        entry(Shelf.getInstance(2, 3), TileColor.GREEN),
                        entry(Shelf.getInstance(3, 3), TileColor.WHITE),
                        entry(Shelf.getInstance(4, 3), TileColor.YELLOW),
                        entry(Shelf.getInstance(5, 3), TileColor.CYAN)
                )
        );
    }
}
