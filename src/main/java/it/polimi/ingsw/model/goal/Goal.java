package it.polimi.ingsw.model.goal;

import it.polimi.ingsw.model.bookshelf.Bookshelf;
import it.polimi.ingsw.model.bookshelf.BookshelfMask;
import it.polimi.ingsw.model.bookshelf.BookshelfMaskSet;
import it.polimi.ingsw.model.bookshelf.Shelf;
import it.polimi.ingsw.model.evaluator.Evaluator;
import it.polimi.ingsw.model.fetcher.Fetcher;
import it.polimi.ingsw.model.filter.Filter;

/**
 * This abstract class represents a generic goal,
 * and provides a general implementation of the method used to calculate the points and
 * retrieve all the {@link BookshelfMask masks} that satisfy the goal.
 *
 * @author Cristiano Migali
 * @author Francesco Gemma
 */
public abstract class Goal {
    private final Fetcher fetcher;
    private final Filter filter;
    private final Evaluator evaluator;

    // Set of masks used to store all the masks that satisfy the last goal computed.
    private BookshelfMaskSet pointMasks;

    /**
     * Constructor of the class.
     * <p>
     * It initializes the fetcher, the filter, the evaluator and the set of masks.
     * <p>
     * The set of masks is initialized with a {@link BookshelfMaskSet} that accepts all the masks added to it.
     *
     * @param fetcher the {@link Fetcher} used to fetch the tiles;
     * @param filter the {@link Filter} used to filter the tiles;
     * @param evaluator the {@link Evaluator} used to evaluate the goal, calculating the points and the masks.
     */
    protected Goal(Fetcher fetcher, Filter filter, Evaluator evaluator) {
        this.fetcher = fetcher;
        this.filter = filter;
        this.evaluator = evaluator;
        pointMasks = new BookshelfMaskSet((a, b) -> true);
    }

    /**
     * This method calculates the points obtained by the goal, and stores all the masks that satisfy that goal in
     * {@link Goal#pointMasks}.
     * <p>
     * It adds the tiles fetched by the fetcher to the filter, and if the filter accepts the tile, it adds the shelf
     * to the mask, and the mask to the evaluator.
     * Once the fetcher has finished, it retrieves the points and the mask set from the evaluator.
     * <p>
     * With this method we can check all types of goals,
     * including: common goals, personal goals and adjacency goals.
     *
     * @param bookshelf the bookshelf to inspect for the goal;
     * @return the number of points obtained by the goal.
     * @throws IllegalStateException if the evaluator returns a negative number of points.
     */
    public final int calculatePoints(Bookshelf bookshelf) {
        BookshelfMask mask = new BookshelfMask(bookshelf);

        // fetches all the shapes until the equilibrium state is reached.
        do {
            Shelf next = fetcher.next();

            if (filter.add(bookshelf.get(next))) {
                if (fetcher.canFix()) {
                    filter.forgetLastTile();
                } else {
                    filter.clear();
                    mask.clear();
                    continue;
                }
            } else mask.add(next);

            // if the shape has been fetched, it adds the mask to the evaluator and clears the filter and the mask.
            if (fetcher.lastShelf()) {
                if (filter.isSatisfied() && evaluator.add(mask)) {
                    fetcher.clear();
                    break;
                }
                filter.clear();
                mask.clear();
            }
        } while (!fetcher.hasFinished());

        filter.clear();

        // gets the points, updates the set of masks and returns the points. Finally, clears the evaluator.
        try {
            int points = evaluator.getPoints();
            if(points == 0) {
                pointMasks = new BookshelfMaskSet((a, b) -> true);
            } else if(points > 0) {
                pointMasks = evaluator.getPointMasks();
            } else {
                throw new IllegalStateException("Evaluator returned a negative number of points");
            }
            return points;
        } finally {
            evaluator.clear();
        }
    }

    /**
     * This method must be called between two calls to {@link Goal#calculatePoints(Bookshelf)}.
     *
     * @return the set of masks that satisfy the last goal computed.
     */
    public final BookshelfMaskSet getPointMasks() {
        return pointMasks;
    }
}
