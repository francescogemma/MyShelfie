package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.function.BiFunction;

/**
 * This class reads and stores information about {@link LibraryMask LibraryMasks} that
 * are fed into its {@link CompatibleEvaluator#add(LibraryMask) add} method, providing
 * useful information regarding mask compatibility. Compatibility definitions can be customized
 * by defining the necessary {@link CompatibleEvaluator#compatible compatible} BiFunction.
 *
 * @author Michele Miotti
 */
public class CompatibleEvaluator implements Evaluator {
    private final ArrayList<LibraryMaskSet> group;
    private final int points;
    private final int targetSetSize;
    private final BiFunction<LibraryMask, LibraryMask, Boolean> compatible;
    private boolean targetMet;

    /**
     * Class constructor.
     * @param points are returned by the {@link CompatibleEvaluator#getPoints() getPoints}
     *               method if the {@link CompatibleEvaluator#targetSetSize target} has been met.
     * @param targetGroupSize is the cluster size to reach in order to allow the
     *                        {@link CompatibleEvaluator#getPoints() getPoints} method to return a non-zero result.
     * @param compatible is a BiFunction that defines the meaning of "compatibility" within elements of a set.
     *                   Said {@link LibraryMaskSet sets} are stored in the {@link CompatibleEvaluator#group} variable.
     */
    public CompatibleEvaluator(int points, int targetGroupSize, BiFunction<LibraryMask, LibraryMask, Boolean> compatible) {
        group = new ArrayList<>();

        this.compatible = compatible;
        this.points = points;
        this.targetSetSize = targetGroupSize;
        this.targetMet = targetSetSize <= 1;
    }

    /**
     * Adds a {@link LibraryMask mask} to the object.
     * @param libraryMask is a mask that will be added to the object.
     * @return true iif there's a set of added masks that is big enough.
     */
    @Override
    public boolean add(LibraryMask libraryMask) {
        for (LibraryMaskSet libraryMaskSet : group) {
            boolean intersectionFound = false;

            if (!libraryMaskSet.isCompatible(libraryMask)) {
                // duplicate current LibraryMaskSet in group
                // then, add non-intersecting mask to one of them
                group.add(new LibraryMaskSet(libraryMaskSet));
                libraryMaskSet.addLibraryMask(libraryMask);

                // note down if this addition meets out target size
                targetMet = libraryMaskSet.getSize() >= targetSetSize;
            }
        }
        // create and add one more set with a single LibraryMask
        LibraryMaskSet libraryMaskSetLast = new LibraryMaskSet(compatible);
        libraryMaskSetLast.addLibraryMask(libraryMask);
        group.add(libraryMaskSetLast);

        return targetMet;
    }
    @Override
    /**
     * Getter method for earned points;
     * Returns 0 if target has not been met.
     */
    public int getPoints() {
        if (targetMet) { return points; }
        return 0;
    }
}