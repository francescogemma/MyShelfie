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
     */
    public AdjacencyGoal() {
        super(new AdjacencyFetcher(),
            new NumDifferentColorFilter(1, 1),
            new AdjacencyEvaluator());
    }
}
