package it.polimi.ingsw.model.goal;

import it.polimi.ingsw.model.evaluator.AdjacencyEvaluator;
import it.polimi.ingsw.model.fetcher.AdjacencyFetcher;
import it.polimi.ingsw.model.filter.NumDifferentColorFilter;

/**
 * This class represents the goal of having all the tiles adjacent to each other.
 * @author Cristiano Migali
 */
public class AdjacencyGoal extends Goal {
    /**
     * Constructor of the class.
     * It takes:
     * <ul>
     *     <li> a {@link AdjacencyFetcher} to fetch all groups of adjacent tiles; </li>
     *     <li> a {@link NumDifferentColorFilter} to filter out groups of tiles with the same color; </li>
     *     <li> an {@link AdjacencyEvaluator} to evaluate the goal. </li>
     * </ul>
     */
    public AdjacencyGoal() {
        super(new AdjacencyFetcher(),
            new NumDifferentColorFilter(1, 1),
            new AdjacencyEvaluator());
    }
}
