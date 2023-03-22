package it.polimi.ingsw.model;

import java.util.ArrayList;

public class LibraryMaskSet {
    private ArrayList<LibraryMask> libraryMasks;

    public LibraryMaskSet() {
        libraryMasks = new ArrayList<>();
    }

    public void addLibraryMask(LibraryMask libraryMask) {
        libraryMasks.add(libraryMask);
    }

    public boolean intersects(LibraryMask libraryMask) {
        for (LibraryMask libraryMaskSample : libraryMasks) {
            if (libraryMaskSample.intersects(libraryMask)) {
                return true;
            }
        }
        return false;
    }
}