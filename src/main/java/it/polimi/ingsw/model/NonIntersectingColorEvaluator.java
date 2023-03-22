package it.polimi.ingsw.model;

import java.util.ArrayList;

public class NonIntersectingColorEvaluator implements Evaluator {
    private ArrayList<LibraryMaskSet> group;
    private final int points;
    private final int targetSetSize;
    private boolean targetMet;

    public NonIntersectingColorEvaluator(int points, int targetGroupSize) {
        group = new ArrayList<>();

        this.points = points;
        this.targetSetSize = targetGroupSize;
        this.targetMet = targetSetSize <= 1;
    }

    // returns true if a single sampled color from both the set and the LibraryMask is the same
    // precondition: all library masks are color-uniform and not empty
    // precondition: libraryMaskSet is not empty
    private boolean colorCompatibilityCheck(LibraryMask libraryMask, LibraryMaskSet libraryMaskSet) {
        Tile maskTile = libraryMask.getSampleTile();
        Tile setTile = libraryMaskSet.getLibraryMasks().get(0).getSampleTile();

        // TODO: check if "==" could be used instead of equals.
        return maskTile.equals(setTile);
    }
    public boolean add(LibraryMask libraryMask) {
        for (LibraryMaskSet libraryMaskSet : group) {
            boolean intersectionFound = false;

            if (!libraryMaskSet.intersects(libraryMask) &&
                    colorCompatibilityCheck(libraryMask, libraryMaskSet)) {
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