package it.polimi.ingsw.model.goal;

import it.polimi.ingsw.model.evaluator.Evaluator;
import it.polimi.ingsw.model.fetcher.Fetcher;
import it.polimi.ingsw.model.filter.Filter;

/**
 * Represents a goal type which has a fixed finite set of kinds, each one of these kinds is identified through a
 * predetermined integer.
 * Examples of IndexedGoals are {@link CommonGoal}s and {@link PersonalGoal}s: there are only 12 predetermined
 * common goals and 12 predetermined personal goals, we can distinguish them through an index.
 * The integer (the index) is mainly used for serialization purpose.
 *
 * @author Cristiano Migali
 */
public abstract class IndexedGoal extends Goal {
    /**
     * It is the index which identifies the kind of this IndexedGoal.
     */
    private int index;

    /**
     * Constructor of the class.
     *
     * @param fetcher is the {@link Fetcher} which allows to retrieve the required {@link it.polimi.ingsw.model.bookshelf.BookshelfMask}s
     *                in order to check if this goal has been completed or not.
     * @param filter is the {@link Filter} which allows to consider only the {@link it.polimi.ingsw.model.bookshelf.BookshelfMask}s
     *               which satisfy a certain coloring criteria among the ones fetched.
     * @param evaluator is the {@link Evaluator} which checks if the goal has been completed or not, given the
     *                  filtered set of {@link it.polimi.ingsw.model.bookshelf.BookshelfMask}s, and calculates the
     *                  corresponding points.
     */
    protected IndexedGoal(Fetcher fetcher, Filter filter, Evaluator evaluator) {
        super(fetcher, filter, evaluator);
    }

    /**
     * Sets the index for this IndexedGoal.
     *
     * @param index is the integer which uniquely identifies the kind of this IndexedGoal.
     * @return this IndexedGoal after its index has been set.
     */
    public IndexedGoal index(int index) {
        this.index = index;

        return this;
    }

    /**
     * @return the integer which uniquely identifies the kind of this IndexedGoal.
     */
    public int getIndex() {
        return index;
    }
}
