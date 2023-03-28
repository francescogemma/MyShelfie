package it.polimi.ingsw.model.goal;

import it.polimi.ingsw.model.evaluator.Evaluator;
import it.polimi.ingsw.model.fetcher.Fetcher;
import it.polimi.ingsw.model.filter.Filter;

public abstract class CommonGoal extends Goal{
    protected CommonGoal(Fetcher fetcher, Filter filter, Evaluator evaluator) {
        super(fetcher, filter, evaluator);
    }
}
