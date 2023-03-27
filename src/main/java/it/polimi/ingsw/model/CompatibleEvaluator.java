package it.polimi.ingsw.model;

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
    private final ArrayList<BookshelfMaskSet> group;
    private final int points;
    private final int targetSetSize;
    private final BiPredicate<BookshelfMask, BookshelfMask> compatible;
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

    /*
     * Adds a {@link BookshelfMask mask} to the object.
     * @param bookshelfMask is a mask that will be added to the object.
     * @return true iff there's a set of added masks that is big enough.
     */
    @Override
    public boolean add(BookshelfMask bookshelfMask) {
        for (BookshelfMaskSet bookshelfMaskSet : group) {
            boolean intersectionFound = false;

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

    /*
     * Getter method for earned points;
     * Returns 0 if target has not been met.
     */
    @Override
    public int getPoints() {
        if (targetMet) { return points; }
        return 0;
    }
}