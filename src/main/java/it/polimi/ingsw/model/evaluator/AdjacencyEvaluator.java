package it.polimi.ingsw.model.evaluator;

import it.polimi.ingsw.model.bookshelf.BookshelfMask;
import it.polimi.ingsw.model.bookshelf.BookshelfMaskSet;

/**
 * Reads {@link BookshelfMask a LibaryMask} and notes down its score
 * according to the game rules, based on its size.
 *
 * @author Michele Miotti
 */
public class AdjacencyEvaluator implements Evaluator {
    /**
     * Amount of points currently accumulated.
     */
    private int points;

    /**
     * Set of all masks provided to evaluator so far.
     */
    private BookshelfMaskSet pointMasks;

    /**
     * Object constructor. Sets the current amount of points to 0.
     */
    public AdjacencyEvaluator() {
        pointMasks = new BookshelfMaskSet();
        points = 0;
    }

    @Override
    public boolean add(BookshelfMask bookshelfMask) {
        if (convertSizeToPoints(bookshelfMask.getShelves().size()) > 0) {
            points += convertSizeToPoints(bookshelfMask.getShelves().size());
            pointMasks.add(bookshelfMask);
        }

        // always return false: it's always possible to get new points.
        return false;
    }

    /**
     * Returns the correct amount of points relative to the size of some
     * shelf cluster, based on the amounts specified in the game rules.
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
        pointMasks = new BookshelfMaskSet();
    }

    @Override
    public int getPoints() {
        return points;
    }

    @Override
    public BookshelfMaskSet getPointMasks() {
        return pointMasks;
    }
}
