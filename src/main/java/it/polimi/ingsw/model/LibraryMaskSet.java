package it.polimi.ingsw.model;

import java.util.ArrayList;

public class LibraryMaskSet {
    private ArrayList<LibraryMask> libraryMasks;

    public LibraryMaskSet() {
        libraryMasks = new ArrayList<>();
    }

    public LibraryMaskSet(LibraryMaskSet libraryMaskSet) {
        libraryMasks = libraryMaskSet.getLibraryMasks();
    }

    public int getSize() {
        return libraryMasks.size();
    }

    public ArrayList<LibraryMask> getLibraryMasks() {
        return new ArrayList<>(libraryMasks);
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