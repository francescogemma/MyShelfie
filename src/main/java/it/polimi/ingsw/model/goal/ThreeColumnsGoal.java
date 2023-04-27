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
 * Three columns each formed by 6 tiles of maximum three different types,
 * one column can show the same or a different combination of another column.
 * <p>
 * It extends CommonGoal.
 *
 * @see CommonGoal
 * @see Goal
 *
 * @author Francesco Gemma
 */
public class ThreeColumnsGoal extends CommonGoal {
    /**
     * Constructor of the class.
     * It takes:
     * <ul>
     *     <li> a {@link ShapeFetcher} to fetch a column of height 6; </li>
     *     <li> a {@link NumDifferentColorFilter} to filter the columns with maximum three different colors; </li>
     *     <li> an {@link AtLeastEvaluator} with targetAmount equal to 3, because we need to find at least 3 columns. </li>
     *     <li> a {@link String} representing the description on the goal </li>
     *     <li> a {@link Map} representing an example of the goal to display. </li>
     * </ul>
     *
     * @see CommonGoal#CommonGoal(Fetcher, Filter, CommonGoalEvaluator, String, Map)
     */
    public ThreeColumnsGoal() {
        super(
                new ShapeFetcher(Shape.getColumn(6)),
                new NumDifferentColorFilter(1, 3),
                new AtLeastEvaluator(3),
                "Three columns\neach formed by\nsix tiles of maximum\nthree different types,\n" +
                        "one column can\nshow the same or\na different combination\nof another column.",
                Map.ofEntries(
                        entry(Shelf.getInstance(0, 0), TileColor.MAGENTA),
                        entry(Shelf.getInstance(1, 0), TileColor.BLUE),
                        entry(Shelf.getInstance(2, 0), TileColor.GREEN),
                        entry(Shelf.getInstance(3, 0), TileColor.BLUE),
                        entry(Shelf.getInstance(4, 0), TileColor.BLUE),
                        entry(Shelf.getInstance(5, 0), TileColor.MAGENTA),
                        entry(Shelf.getInstance(0, 2), TileColor.MAGENTA),
                        entry(Shelf.getInstance(1, 2), TileColor.BLUE),
                        entry(Shelf.getInstance(2, 2), TileColor.BLUE),
                        entry(Shelf.getInstance(3, 2), TileColor.BLUE),
                        entry(Shelf.getInstance(4, 2), TileColor.BLUE),
                        entry(Shelf.getInstance(5, 2), TileColor.BLUE),
                        entry(Shelf.getInstance(0, 4), TileColor.YELLOW),
                        entry(Shelf.getInstance(1, 4), TileColor.YELLOW),
                        entry(Shelf.getInstance(2, 4), TileColor.YELLOW),
                        entry(Shelf.getInstance(3, 4), TileColor.YELLOW),
                        entry(Shelf.getInstance(4, 4), TileColor.YELLOW),
                        entry(Shelf.getInstance(5, 4), TileColor.YELLOW)
                )
        );
    }
}
