package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.function.BiFunction;

public class CompatibleEvaluator implements Evaluator {
    private final ArrayList<LibraryMaskSet> group;
    private final int points;
    private final int targetSetSize;
    private final BiFunction<LibraryMask, LibraryMask, Boolean> compatible;
    private boolean targetMet;


    public CompatibleEvaluator(int points, int targetGroupSize, BiFunction<LibraryMask, LibraryMask, Boolean> compatible) {
        group = new ArrayList<>();

        this.compatible = compatible;
        this.points = points;
        this.targetSetSize = targetGroupSize;
        this.targetMet = targetSetSize <= 1;
    }
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
    public int getPoints() {
        if (targetMet) { return points; }
        return 0;
    }
}