package it.polimi.ingsw.model;

import java.util.ArrayList;

public class NonIntersectingEvaluator implements Evaluator {
    private ArrayList<ArrayList<LibraryMask>> groups;

    public NonIntersectingEvaluator() {
        groups = new ArrayList<>();
    }
    public boolean add(LibraryMask libraryMask) {
        // TODO: delete all of this and start fresh using LibraryMaskSet

        for (ArrayList<LibraryMask> group : groups) {
            boolean intersectionFound = false;
            for (LibraryMask libraryMaskSample : group) {
                if (areIntersecting(libraryMask, libraryMaskSample)) {
                    intersectionFound = true;
                    break;
                }
            }
            if (!intersectionFound) {
                group.add(libraryMask);
            }
        }
        ArrayList<LibraryMask> newGroup = new ArrayList<>();
        newGroup.add(libraryMask);
        groups.add(newGroup);

        return false;
    }
    public int getPoints() {
        return 0;
    }

    private boolean areIntersecting(LibraryMask a, LibraryMask b) {
        // TODO: delete all of this and start fresh using LibraryMaskSet
        for (Shelf shelfA : a.getShelves()) {
            for (Shelf shelfB : b.getShelves()) {
                if (shelfA.getRow() == shelfB.getRow() && shelfA.getColumn() == shelfB.getColumn()) {
                    return true;
                }
            }
        }
        return false;
    }
}