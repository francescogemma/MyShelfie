package it.polimi.ingsw.model.goal;

import it.polimi.ingsw.model.evaluator.Evaluator;
import it.polimi.ingsw.model.fetcher.Fetcher;
import it.polimi.ingsw.model.filter.Filter;

/**
 * This abstract class represents a common goal. It extends Goal.
 * <p>
 * All common goals should extend this class.
 *
 * {@see Goal}
 *
 * @author Francesco Gemma
 */
public abstract class CommonGoal extends Goal{
    /**
     * Constructor of the class. It simply calls the constructor of the superclass {@link Goal}.
     *
     * @param fetcher the {@link Fetcher} type used to fetch the shape of the goal;
     * @param filter the {@link Filter} type used for the goal;
     * @param evaluator the {@link Evaluator} type used to evaluate the goal.
     */
    protected CommonGoal(Fetcher fetcher, Filter filter, Evaluator evaluator) {
        super(fetcher, filter, evaluator);
    }
}
