package it.polimi.ingsw.model.evaluator;

import it.polimi.ingsw.model.bookshelf.BookshelfMask;

/**
 * Reads {@link BookshelfMask a LibaryMask} and notes down its score
 * according to the game rules, based on its size.
 *
 * @author Michele Miotti
 */
public class AdjacencyEvaluator implements Evaluator {
    private int points;
    /**
     * Object constructor. Sets the current amount of points to 0.
     */
    public AdjacencyEvaluator() {
        points = 0;
    }
    /**
     * Add a {@link BookshelfMask BookshelfMask} and evaluate it.
     * @param bookshelfMask will be taken account for, and will have an impact on the current score,
     *                    if its size is greater than 2.
     * @return false every time, since it's always possible to gather new points from any state.
     */
    public boolean add(BookshelfMask bookshelfMask) {
        points += convertSizeToPoints(bookshelfMask.getShelves().size());
        // always return false since it's always possible to get new points
        return false;
    }
    /**
     * Returns the correct amount of points relative to the size of some
     * shelf cluster, following the amounts specified in the game rules.
     * @param size indicates the amount of adjacent shelves in a mask.
     * @return the correct score relative to the input size.
     */
    private int convertSizeToPoints(int size) {
        if (size >= 6) return 8;
        if (size == 5) return 5;
        if (size == 4) return 3;
        if (size == 3) return 2;
        return 0;
    }

    @Override
    public void clear() {
        points = 0;
    }

    /**
     * Gets the points gathered so far.
     */
    public int getPoints() {
        return points;
    }
}