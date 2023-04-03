package it.polimi.ingsw.model.evaluator;

import it.polimi.ingsw.model.bookshelf.BookshelfMask;
import it.polimi.ingsw.model.bookshelf.BookshelfMaskSet;

/**
 * Generalizes a stage of goal checking by reading {@link BookshelfMask BookshelfMasks}
 * one by one, and keeping track of the best possible score that can be awarded to the player
 * starting with that set of masks. Lets other components know when a set is "stuck", as in,
 * there is no possible way to get a better score by adding more elements.
 *
 * @author Michele Miotti
 */
public interface Evaluator {
    /**
     * Add a mask to the current point-evaluation system.
     * @param bookshelfMask will be taken account for, and may have an impact on the current score.
     * @return true iff it's not possible to increase the points returned by {@link Evaluator#getPoints() getPoints},
     * no matter what the input is.
     */
    boolean add(BookshelfMask bookshelfMask);

    /**
     * Getter function for current score.
     * @return the amount of points based on the state so far.
     */
    int getPoints();

    /**
     * Resets the evaluator. This method should be invoked between an evaluation and the following.
     *
     * @author Cristiano Migali
     */
    void clear();

    /**
     * Returns a {@link BookshelfMaskSet set of masks} that consists in the reason for giving the player the current amount of points.
     *
     * @author Francesco Gemma
     * @author Michele Miotti
     */
    BookshelfMaskSet getPointMasks();
}
