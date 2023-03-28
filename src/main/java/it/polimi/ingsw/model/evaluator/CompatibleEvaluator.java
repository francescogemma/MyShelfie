package it.polimi.ingsw.model.evaluator;

import it.polimi.ingsw.model.bookshelf.BookshelfMask;
import it.polimi.ingsw.model.bookshelf.BookshelfMaskSet;

import java.util.ArrayList;
import java.util.function.BiPredicate;

/**
 * This class reads and stores information about {@link BookshelfMask BookshelfMasks} that
 * are fed into its {@link CompatibleEvaluator#add(BookshelfMask) add} method, providing
 * useful information regarding mask compatibility. Compatibility definitions can be customized
 * by defining the necessary {@link CompatibleEvaluator#compatible compatible} BiPredicate.
 *
 * @author Michele Miotti
 */
public class CompatibleEvaluator implements Evaluator {
    /**
     * This ArrayList contains {@link BookshelfMaskSet BookshelfMaskSets}.
     * It represents the set of all possible compatible sets that can be made
     * with all {@link BookshelfMask masks} given so far.
     */
    private final ArrayList<BookshelfMaskSet> group;

    /**
     * If the target is met, the player will receive these points,
     * otherwise, they will receive zero points.
     */
    private final int points;

    /**
     * This is how big the biggest set in our group needs to be in order
     * for this evaluator to consider its target met, and give the player
     * their non-zero amount of points.
     */
    private final int targetSetSize;

    /**
     * This BiPredicate dictates the type of CompatibleEvaluator we're using.
     * By providing this object to the constructor, it is possible to customize the
     * set of criteria that elements of the same set must follow in order to be added.
     */
    private final BiPredicate<BookshelfMask, BookshelfMask> compatible;

    /**
     * Simple boolean value to determine if the player can receive a non-zero amount of points.
     */
    private boolean targetMet;

    /**
     * Class constructor.
     * @param points are returned by the {@link CompatibleEvaluator#getPoints() getPoints}
     *               method if the {@link CompatibleEvaluator#targetSetSize target} has been met.
     * @param targetGroupSize is the cluster size to reach in order to allow the
     *                        {@link CompatibleEvaluator#getPoints() getPoints} method to return a non-zero result.
     * @param compatible is a BiPredicate that defines the meaning of "compatibility" within elements of a set.
     *                   Said {@link BookshelfMaskSet sets} are stored in the {@link CompatibleEvaluator#group} variable.
     */
    public CompatibleEvaluator(int points, int targetGroupSize, BiPredicate<BookshelfMask, BookshelfMask> compatible) {
        group = new ArrayList<>();

        this.compatible = compatible;
        this.points = points;
        this.targetSetSize = targetGroupSize;
        this.targetMet = targetSetSize <= 1;
    }

    @Override
    public boolean add(BookshelfMask bookshelfMask) {
        for (BookshelfMaskSet bookshelfMaskSet : group) {
            if (bookshelfMaskSet.isCompatible(bookshelfMask)) {
                // duplicate current BookshelfMaskSet in group
                // then, add non-intersecting mask to one of them
                group.add(new BookshelfMaskSet(bookshelfMaskSet));
                bookshelfMaskSet.addBookshelfMask(bookshelfMask);

                // note down if this addition meets out target size
                targetMet |= bookshelfMaskSet.getSize() >= targetSetSize;
                if (targetMet) {
                    return true;
                }
            }
        }
        // create and add one more set with a single BookshelfMask
        BookshelfMaskSet bookshelfMaskSetLast = new BookshelfMaskSet(compatible);
        bookshelfMaskSetLast.addBookshelfMask(bookshelfMask);
        group.add(bookshelfMaskSetLast);

        return targetMet;
    }

    @Override
    public int getPoints() {
        if (targetMet) { return points; }
        return 0;
    }
}