package it.polimi.ingsw.model.evaluator;

import it.polimi.ingsw.model.bookshelf.BookshelfMask;
import it.polimi.ingsw.model.bookshelf.BookshelfMaskSet;

import java.util.function.Predicate;

/**
 * Simple evaluator that counts how many {@link BookshelfMask BookshelfMasks} have been fed
 * into its add method, and checks if the amount is great enough to grant a player their points.
 *
 * @author Michele Miotti
 */
public class AtLeastEvaluator extends CommonGoalEvaluator implements Evaluator {
    /**
     * The needed amount of masks give the player a non-zero amount of points.
     */
    private final int targetAmount;

    /**
     * This set stores all masks gathered so far.
     */
    private BookshelfMaskSet bookshelfMaskSet;

    /**
     * Predicate to determine if the provided {@link BookshelfMask BookshelfMask}
     * has to be actually added to the counter.
     */
    private final Predicate<BookshelfMask> toCount;

    /**
     * Class constructor.
     * @param playersAmount is this game's player amount (2 to 4).
     * @param targetAmount is the needed quantity of {@link BookshelfMask BookshelfMasks} to
     *                     grant a player a non-zero amount of points.
     */
    public AtLeastEvaluator(int playersAmount, int targetAmount, Predicate<BookshelfMask> toCount) {
        super(playersAmount);
        bookshelfMaskSet = new BookshelfMaskSet((a, b) -> true);

        this.toCount = toCount;
        this.targetAmount = targetAmount;
    }

    /**
     * Constructor if we don't require any kind of predicate.
     * A predicate is created, and it always returns true.
     * @param playersAmount is this game's player amount (2 to 4).
     * @param targetAmount is the needed quantity of {@link BookshelfMask BookshelfMasks} to
     *                     grant a player a non-zero amount of points.
     */
    public AtLeastEvaluator(int playersAmount, int targetAmount) {
        super(playersAmount);
        bookshelfMaskSet = new BookshelfMaskSet((a, b) -> true);

        this.toCount = bookshelfMask -> true;
        this.targetAmount = targetAmount;
    }

    @Override
    public boolean add(BookshelfMask bookshelfMask) {
        // no need to add any mask if our target is already met
        if (bookshelfMaskSet.getSize() >= targetAmount) {
            return true;
        }

        // add mask to mask set
        if (toCount.test(bookshelfMask)) {
            bookshelfMaskSet.addBookshelfMask(bookshelfMask);
        }

        return bookshelfMaskSet.getSize() >= targetAmount;
    }

    @Override
    public int getPoints() {
        if (bookshelfMaskSet.getSize() < targetAmount) return 0;
        return super.getPoints();
    }

    @Override
    public BookshelfMaskSet getPointMasks() {
        return bookshelfMaskSet;
    }

    @Override
    public void clear() {
        bookshelfMaskSet = new BookshelfMaskSet((a, b) -> true);
    }
}
