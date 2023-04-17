package it.polimi.ingsw.model.goal;

import it.polimi.ingsw.model.evaluator.Evaluator;
import it.polimi.ingsw.model.fetcher.Fetcher;
import it.polimi.ingsw.model.filter.Filter;

// TODO: (For Cristiano) add JavaDoc to this class

public abstract class IndexedGoal extends Goal {
    private int index;

    protected IndexedGoal(Fetcher fetcher, Filter filter, Evaluator evaluator) {
        super(fetcher, filter, evaluator);
    }

    public IndexedGoal index(int index) {
        this.index = index;

        return this;
    }

    public int getIndex() {
        return index;
    }
}
