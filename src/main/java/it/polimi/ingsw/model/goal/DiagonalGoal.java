package it.polimi.ingsw.model.goal;

import it.polimi.ingsw.model.bookshelf.Shape;
import it.polimi.ingsw.model.bookshelf.Shelf;
import it.polimi.ingsw.model.evaluator.AtLeastEvaluator;
import it.polimi.ingsw.model.evaluator.CommonGoalEvaluator;
import it.polimi.ingsw.model.fetcher.Fetcher;
import it.polimi.ingsw.model.fetcher.ShapeFetcher;
import it.polimi.ingsw.model.fetcher.UnionFetcher;
import it.polimi.ingsw.model.filter.Filter;
import it.polimi.ingsw.model.filter.NumDifferentColorFilter;
import it.polimi.ingsw.model.tile.TileColor;

import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

/**
 * This class represents a common goal:
 * <p>
 * Five tiles of the same type forming a diagonal.
 * <p>
 * It extends CommonGoal.
 *
 * @see CommonGoal
 * @see Goal
 *
 * @author Francesco Gemma
 */
public class DiagonalGoal extends CommonGoal {
    /**
     * Constructor of the class.
     * It takes:
     * <ul>
     *     <li> an {@link UnionFetcher} to fetch the main diagonal and its vertical flip; </li>
     *     <li> a {@link NumDifferentColorFilter} to filter the tiles with the same color; </li>
     *     <li> an {@link AtLeastEvaluator} with targetAmount equal to 1; </li>
     *     <li> a {@link String} representing the description on the goal; </li>
     *     <li> a {@link Map} representing an example of the goal to display. </li>
     * </ul>
     *
     * @see CommonGoal#CommonGoal(Fetcher, Filter, CommonGoalEvaluator, String, Map)
     */
    public DiagonalGoal() {
        super(
                new UnionFetcher(List.of(
                    new ShapeFetcher(Shape.getMainDiagonal(5)),
                    new ShapeFetcher(Shape.getMainDiagonal(5).verticalFlip())
                )),
                new NumDifferentColorFilter(1, 1),
                new AtLeastEvaluator(1),
                "Five tiles of the same\ntype forming a diagonal.",
                Map.ofEntries(
                        entry(Shelf.getInstance(0, 0), TileColor.MAGENTA),
                        entry(Shelf.getInstance(1, 1), TileColor.MAGENTA),
                        entry(Shelf.getInstance(2, 2), TileColor.MAGENTA),
                        entry(Shelf.getInstance(3, 3), TileColor.MAGENTA),
                        entry(Shelf.getInstance(4, 4), TileColor.MAGENTA)
                )
        );
    }
}
