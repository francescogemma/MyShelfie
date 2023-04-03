package it.polimi.ingsw.model.bookshelf;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;

/**
 * A simple set of {@link BookshelfMask BookshelfMask} objects. Uses a custom BiPredicate to
 * check if a BookshelfMask is compatible with all elements of set.
 *
 * @author Michele Miotti
 */
public class BookshelfMaskSet {
    /**
     * This list contains all copies of {@link BookshelfMask BookshelfMasks}
     * that are contained in this set.
     */
    private final List<BookshelfMask> bookshelfMasks;

    /**
     * This BiPredicate will be used to check if an object can be added to the set
     * via the isCompatible method. It checks if two {@link BookshelfMask Bookshelfmasks}
     * are compatible relative to each other.
     */
    private final BiPredicate<BookshelfMask, BookshelfMask> compatible;

    /**
     * Constructor "from scratch". Should be used to get a new empty set.
     * @param compatible defines the set of criteria used for compatibility checking.
     */
    public BookshelfMaskSet(BiPredicate<BookshelfMask, BookshelfMask> compatible) {
        bookshelfMasks = new ArrayList<>();
        this.compatible = compatible;
    }

    /**
     * Constructor used as means of copying.
     * @param bookshelfMaskSet will be used as reference to create an identical object.
     */
    public BookshelfMaskSet(BookshelfMaskSet bookshelfMaskSet) {
        bookshelfMasks = bookshelfMaskSet.getBookshelfMasks();
        this.compatible = bookshelfMaskSet.getCompatible();
    }

    /**
     * Getter method for the {@link BookshelfMaskSet#compatible compatible} BiFunction.
     * @return this instance's {@link BookshelfMaskSet#compatible compatible} BiFunction.
     */
    public BiPredicate<BookshelfMask, BookshelfMask> getCompatible() {
        return compatible;
    }

    /**
     * @return the amount of elements of this set.
     */
    public int getSize() {
        return bookshelfMasks.size();
    }

    /**
     * @return the list of all contained {@link BookshelfMask BookshelfMasks}.
     */
    public List<BookshelfMask> getBookshelfMasks() {
        return new ArrayList<>(bookshelfMasks);
    }

    /**
     * Add an element to the set (by copying it).
     * @param bookshelfMask will be added to the set.
     */
    public void addBookshelfMask(BookshelfMask bookshelfMask) {
        bookshelfMasks.add(new BookshelfMask(bookshelfMask));
    }

    /**
     * This method checks if the given input is compatible with all other elements of the set.
     * @param bookshelfMask will be checked against all other elements.
     * @return true if all compatibility criteria are met with all set elements.
     */
    public boolean isCompatible(BookshelfMask bookshelfMask) {
        for (BookshelfMask bookshelfMaskSample : bookshelfMasks) {
            if (!compatible.test(bookshelfMaskSample, bookshelfMask)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Clears the set by removing all elements.
     * @author Francesco Gemma
     */
    public void clearSet() {
        bookshelfMasks.clear();
    }
}