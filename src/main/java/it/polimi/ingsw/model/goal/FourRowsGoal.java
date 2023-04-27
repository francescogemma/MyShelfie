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
 * This class represents a common goal:
 * <p>
 * Four lines each formed by 5 tiles of maximum three different types,
 * one line can show the same or a different combination of another line.
 * <p>
 * It extends CommonGoal.
 *
 * @see CommonGoal
 * @see Goal
 *
 * @author Francesco Gemma
 */
public class FourRowsGoal extends CommonGoal {
    /**
     * Constructor of the class.
     * It takes:
     * <ul>
     *     <li> a {@link ShapeFetcher} to fetch a row of width 5; </li>
     *     <li> a {@link NumDifferentColorFilter} to filter the rows with maximum three different colors; </li>
     *     <li> an {@link AtLeastEvaluator} with targetAmount equal to 4, because we need to find at least 4 rows; </li>
     *     <li> a {@link String} representing the description on the goal; </li>
     *     <li> a {@link Map} representing an example of the goal to display. </li>
     * </ul>
     *
     * @see CommonGoal#CommonGoal(Fetcher, Filter, CommonGoalEvaluator, String, Map)
     */
    public FourRowsGoal() {
        super(
                new ShapeFetcher(Shape.getRow(5)),
                new NumDifferentColorFilter(1, 3),
                new AtLeastEvaluator(4),
                "Four lines each formed\nby five tiles of maximum\nthree different types,\n" +
                        " one line can show\nthe same or a different\ncombination of another line.",
                Map.ofEntries(
                        entry(Shelf.getInstance(0, 0), TileColor.GREEN),
                        entry(Shelf.getInstance(0, 1), TileColor.GREEN),
                        entry(Shelf.getInstance(0, 2), TileColor.YELLOW),
                        entry(Shelf.getInstance(0, 3), TileColor.GREEN),
                        entry(Shelf.getInstance(0, 4), TileColor.GREEN),
                        entry(Shelf.getInstance(1, 0), TileColor.BLUE),
                        entry(Shelf.getInstance(1, 1), TileColor.BLUE),
                        entry(Shelf.getInstance(1, 2), TileColor.BLUE),
                        entry(Shelf.getInstance(1, 3), TileColor.BLUE),
                        entry(Shelf.getInstance(1, 4), TileColor.BLUE),
                        entry(Shelf.getInstance(3, 0), TileColor.CYAN),
                        entry(Shelf.getInstance(3, 1), TileColor.GREEN),
                        entry(Shelf.getInstance(3, 2), TileColor.GREEN),
                        entry(Shelf.getInstance(3, 3), TileColor.CYAN),
                        entry(Shelf.getInstance(3, 4), TileColor.YELLOW),
                        entry(Shelf.getInstance(5, 0), TileColor.MAGENTA),
                        entry(Shelf.getInstance(5, 1), TileColor.MAGENTA),
                        entry(Shelf.getInstance(5, 2), TileColor.MAGENTA),
                        entry(Shelf.getInstance(5, 3), TileColor.MAGENTA),
                        entry(Shelf.getInstance(5, 4), TileColor.GREEN)
                )
        );
    }
}
