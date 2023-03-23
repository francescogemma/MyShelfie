package it.polimi.ingsw.model;

import java.util.ArrayList;

public class AdjacencyEvaluator implements Evaluator {
    private LibraryMaskSet libraryMaskSet;
    private int points;
    public AdjacencyEvaluator() {
        points = 0;
    }
    public boolean add(LibraryMask libraryMask) {
        points = convertSizeToPoints(libraryMask.getShelves().size());
        // always return false since it's always possible to get new points
        return false;
    }

    private int convertSizeToPoints(int size) {
        if (size >= 6) return 8;
        if (size == 5) return 5;
        if (size == 4) return 3;
        if (size == 3) return 2;
        return 0;
    }
    public int getPoints() {
        return points;
    }
}
