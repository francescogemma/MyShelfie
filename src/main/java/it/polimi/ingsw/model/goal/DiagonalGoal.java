package it.polimi.ingsw.model.goal;

import it.polimi.ingsw.model.bookshelf.Shape;
import it.polimi.ingsw.model.evaluator.AtLeastEvaluator;
import it.polimi.ingsw.model.evaluator.Evaluator;
import it.polimi.ingsw.model.fetcher.Fetcher;
import it.polimi.ingsw.model.fetcher.ShapeFetcher;
import it.polimi.ingsw.model.fetcher.UnionFetcher;
import it.polimi.ingsw.model.filter.Filter;
import it.polimi.ingsw.model.filter.NumDifferentColorFilter;

import java.util.List;

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
     *     <li> an {@link AtLeastEvaluator} with targetAmount equal to 1. </li>
     * </ul>
     *
     * @see CommonGoal#CommonGoal(Fetcher, Filter, Evaluator)
     *
     * @param numPlayers the number of players in the game
     */
    public DiagonalGoal(int numPlayers) {
        super(
                new UnionFetcher(List.of(
                    new ShapeFetcher(Shape.getMainDiagonal(5)),
                    new ShapeFetcher(Shape.getMainDiagonal(5).verticalFlip())
                )),
                new NumDifferentColorFilter(1, 1),
                new AtLeastEvaluator(numPlayers, 1)
        );
    }
}
