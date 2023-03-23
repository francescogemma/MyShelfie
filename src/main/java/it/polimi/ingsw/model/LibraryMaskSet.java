package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.function.BiFunction;

public class LibraryMaskSet {
    private ArrayList<LibraryMask> libraryMasks;
    private BiFunction<LibraryMask, LibraryMask, Boolean> compatible;


    public LibraryMaskSet(BiFunction<LibraryMask, LibraryMask, Boolean> compatible) {
        libraryMasks = new ArrayList<>();
        this.compatible = compatible;
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

    public boolean isCompatible(LibraryMask libraryMask) {
        for (LibraryMask libraryMaskSample : libraryMasks) {
            if (!compatible.apply(libraryMaskSample, libraryMask)) {
                return false;
            }
        }
        return true;
    }
}