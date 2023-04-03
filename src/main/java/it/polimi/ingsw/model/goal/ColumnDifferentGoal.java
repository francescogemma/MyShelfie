package it.polimi.ingsw.model.goal;

import it.polimi.ingsw.model.bookshelf.Shape;
import it.polimi.ingsw.model.evaluator.AtLeastEvaluator;
import it.polimi.ingsw.model.evaluator.Evaluator;
import it.polimi.ingsw.model.fetcher.Fetcher;
import it.polimi.ingsw.model.fetcher.ShapeFetcher;
import it.polimi.ingsw.model.filter.Filter;
import it.polimi.ingsw.model.filter.NumDifferentColorFilter;

/**
 * This class represents a common goal: two columns made up of all different colors.
 * @author Giacomo Groppi
 * */
public class ColumnDifferentGoal extends CommonGoal {
    /**
     * Constructor of the class.
     * It takes:
     * <ul>
     *     <li> a {@link ShapeFetcher} to fetch a column of width 6. </li>
     *     <li> a {@link NumDifferentColorFilter} to filter the column with at least six different colors. </li>
     *     <li> an {@link AtLeastEvaluator} with targetAmount equal to 2, because we need to find at least 2 column. </li>
     * </ul>
     *
     * @see CommonGoal#CommonGoal(Fetcher, Filter, Evaluator)
     *
     * @param numPlayers the number of players in the game
     * */
    public ColumnDifferentGoal(int numPlayers) {
        super(
                new ShapeFetcher(Shape.getColumn(6)),
                new NumDifferentColorFilter(6, 6),
                new AtLeastEvaluator(numPlayers, 2, bookshelfMask -> bookshelfMask.getShelves().size() >= 2)
        );
    }
}
