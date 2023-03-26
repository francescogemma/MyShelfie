package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.function.BiFunction;

/**
 * A simple set of {@link LibraryMask LibraryMask} objects. Uses a custom BiFunction to
 * check if a LibraryMask is compatible with all elements of set.
 * @author Michele Miotti
 */
public class LibraryMaskSet {
    private final ArrayList<LibraryMask> libraryMasks;
    private final BiFunction<LibraryMask, LibraryMask, Boolean> compatible;

    /**
     * Constructor "from scratch". Should be used to get a new empty set.
     * @param compatible defines the set of criteria used for compatibility checking.
     */
    public LibraryMaskSet(BiFunction<LibraryMask, LibraryMask, Boolean> compatible) {
        libraryMasks = new ArrayList<>();
        this.compatible = compatible;
    }

    /**
     * Constructor used as means of copying.
     * @param libraryMaskSet will be used as reference to create an identical object.
     */
    public LibraryMaskSet(LibraryMaskSet libraryMaskSet) {
        libraryMasks = libraryMaskSet.getLibraryMasks();
        this.compatible = libraryMaskSet.getCompatible();
    }

    /**
     * Getter method for the {@link LibraryMaskSet#compatible compatible} BiFunction.
     * @return this instance's {@link LibraryMaskSet#compatible compatible} BiFunction.
     */
    public BiFunction getCompatible() {
        return compatible;
    }

    /**
     * @return the amount of elements of this set.
     */
    public int getSize() {
        return libraryMasks.size();
    }

    /**
     * @return the list of all contained {@link LibraryMask LibraryMasks}.
     */
    public ArrayList<LibraryMask> getLibraryMasks() {
        return new ArrayList<>(libraryMasks);
    }

    /**
     * Add an element to the set.
     * @param libraryMask will be added to the set.
     */
    public void addLibraryMask(LibraryMask libraryMask) {
        libraryMasks.add(libraryMask);
    }

    /**
     * This method checks if the given input is compatible with all other elements of the set.
     * @param libraryMask will be checked against all other elements.
     * @return true if all compatibility criteria are met with all set elements.
     */
    public boolean isCompatible(LibraryMask libraryMask) {
        for (LibraryMask libraryMaskSample : libraryMasks) {
            if (!compatible.apply(libraryMaskSample, libraryMask)) {
                return false;
            }
        }
        return true;
    }
}