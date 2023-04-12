package it.polimi.ingsw.model.goal;

import it.polimi.ingsw.model.evaluator.AdjacencyEvaluator;
import it.polimi.ingsw.model.fetcher.AdjacencyFetcher;
import it.polimi.ingsw.model.filter.NumDifferentColorFilter;

public class AdjacencyGoal extends Goal {
    public AdjacencyGoal() {
        super(new AdjacencyFetcher(),
            new NumDifferentColorFilter(1, 1),
            new AdjacencyEvaluator());
    }
}
