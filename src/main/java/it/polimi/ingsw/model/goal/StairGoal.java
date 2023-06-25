package it.polimi.ingsw.model.goal;

import it.polimi.ingsw.model.evaluator.CommonGoalEvaluator;
import it.polimi.ingsw.model.tile.TileColor;
import it.polimi.ingsw.model.bookshelf.*;
import it.polimi.ingsw.model.evaluator.AtLeastEvaluator;
import it.polimi.ingsw.model.fetcher.Fetcher;
import it.polimi.ingsw.model.fetcher.ShapeFetcher;
import it.polimi.ingsw.model.fetcher.UnionFetcher;
import it.polimi.ingsw.model.filter.Filter;
import it.polimi.ingsw.model.filter.NumDifferentColorFilter;

import java.util.Map;

import java.util.*;
import java.util.function.Predicate;

import static java.util.Map.entry;

/**
 * This class represents a common goal:
 * <p>
 *  The tiles inside the bookshelf must take the form of a staircase.
 * <p>
 * It extends CommonGoal.
 *
 *  @see CommonGoal
 *  @see Goal
 *
 * @author Giacomo Groppi
 * */
public class StairGoal extends CommonGoal{
    /**
     * This predicate checks if a staircase is a valid one.
     */
    private static final Predicate<BookshelfMask> check = (BookshelfMask bookshelfMask) -> {
        List<Shelf> shelves = bookshelfMask.getShelves();
        for (Shelf s: shelves) {
            if (s.getRow() != 0) {
                Shelf positionUp = s.move(Offset.getInstance(-1, 0));
                if (bookshelfMask.getTileColorAt(positionUp) == TileColor.EMPTY)
                    continue;
                if (!shelves.contains(positionUp))
                    return false;
            }
        }
        return true;
    };


    /**
     * Constructor of the class.
     * It takes:
     * <ul>
     *     <li> a {@link UnionFetcher} to fetch all stairs in a bookshelf; </li>
     *     <li> a {@link NumDifferentColorFilter} that consent all colors; </li>
     *     <li> an {@link AtLeastEvaluator} with targetAmount equal to 1 (consenting 1 stair)
     *     and checking if the shape is a valid one </li>
     *     <li> a {@link String} representing the description on the goal; </li>
     *     <li> a {@link Map} representing an example of the goal to display. </li>
     * </ul>
     *
     * @see CommonGoal#CommonGoal(Fetcher, Filter, CommonGoalEvaluator, String, Map)
     */
    public StairGoal() {
        super(new UnionFetcher(List.of(
                    new ShapeFetcher(Shape.STAIR),
                    new ShapeFetcher(Shape.STAIR.verticalFlip())
                )),
                new NumDifferentColorFilter(1, 6),
                new AtLeastEvaluator(1, check),
                "The tiles inside\nthe bookshelf must\ntake the form\nof a staircase.",
                Map.ofEntries(
                        entry(Shelf.getInstance(1, 0), TileColor.GREEN),
                        entry(Shelf.getInstance(2, 0), TileColor.MAGENTA),
                        entry(Shelf.getInstance(2, 1), TileColor.BLUE),
                        entry(Shelf.getInstance(3, 0), TileColor.BLUE),
                        entry(Shelf.getInstance(3, 1), TileColor.WHITE),
                        entry(Shelf.getInstance(3, 2), TileColor.YELLOW),
                        entry(Shelf.getInstance(4, 0), TileColor.CYAN),
                        entry(Shelf.getInstance(4, 1), TileColor.CYAN),
                        entry(Shelf.getInstance(4, 2), TileColor.GREEN),
                        entry(Shelf.getInstance(4, 3), TileColor.MAGENTA),
                        entry(Shelf.getInstance(5, 0), TileColor.WHITE),
                        entry(Shelf.getInstance(5, 1), TileColor.GREEN),
                        entry(Shelf.getInstance(5, 2), TileColor.CYAN),
                        entry(Shelf.getInstance(5, 3), TileColor.WHITE),
                        entry(Shelf.getInstance(5, 4), TileColor.BLUE)
                )
        );
    }
}
