package it.polimi.ingsw.model;

import java.util.ArrayList;

public class NonIntersectingEvaluator implements Evaluator {
    private ArrayList<LibraryMaskSet> group;
    private final int points;
    private final int targetSetSize;
    private boolean targetMet;

    public NonIntersectingEvaluator(int points, int targetGroupSize) {
        group = new ArrayList<>();

        this.points = points;
        this.targetSetSize = targetGroupSize;
        this.targetMet = targetSetSize <= 1;
    }
    public boolean add(LibraryMask libraryMask) {
        for (LibraryMaskSet libraryMaskSet : group) {
            boolean intersectionFound = false;

            if (!libraryMaskSet.intersects(libraryMask)) {
                // duplicate current LibraryMaskSet in group
                // then, add non-intersecting mask to one of them
                group.add(new LibraryMaskSet(libraryMaskSet));
                libraryMaskSet.addLibraryMask(libraryMask);

                // note down if this addition meets out target size
                targetMet = libraryMaskSet.getSize() >= targetSetSize;
            }
        }
        // create and add one more set with a single LibraryMask
        LibraryMaskSet libraryMaskSetLast = new LibraryMaskSet();
        libraryMaskSetLast.addLibraryMask(libraryMask);
        group.add(libraryMaskSetLast);

        return targetMet;
    }
    public int getPoints() {
        if (targetMet) { return points; }
        return 0;
    }
}