package it.polimi.ingsw.model.bookshelf;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 * A simple set of {@link BookshelfMask BookshelfMask} objects. Uses predicates and biPredicates to
 * check if a BookshelfMask is compatible with the set. If it isn't, the object is not added.
 * Predicates are used to check if an element can be added to the set or not.
 * BiPredicates are used to check if an element can be added to the set or not, relatively to a property of
 * all other elements of the set.
 * Here's an example: adding an element to a set only if that element is red would require a predicate. That predicate would
 * check if the given element is red. On the other hand, adding an element to a set only if that element shares the same
 * color with all other contained elements, without knowing the color, would require a biPredicate, to check all possible
 * couples of elements.
 *
 * @author Michele Miotti
 */
public class BookshelfMaskSet {
    /**
     * This list contains copies of allowed {@link BookshelfMask BookshelfMasks},
     * given as input by the user during this set's existence.
     */
    private final List<BookshelfMask> bookshelfMasks;

    /**
     * This predicate will be used to check if an object can be added to the set or not.
     */
    private Predicate<BookshelfMask> predicate;

    /**
     * This helper method converts a biPredicate to a predicate, by implementing the algorithm for "couples checking"
     * @param biPredicate is the biPredicate that will be transformed into a predicate.
     * @return a predicate that calculates the same constrain implied by the biPredicate.
     */
    private Predicate<BookshelfMask> toPredicate(BiPredicate<BookshelfMask, BookshelfMask> biPredicate) {
        return (bookshelfMask -> {
            for (BookshelfMask containedBookshelfMask : bookshelfMasks) {
                if (!biPredicate.test(containedBookshelfMask, bookshelfMask)) {
                    return false;
                }
            }
            return true;
        });
    }

    // TODO: Refactor all the code using deprecated methods inside this class

    /**
     * @deprecated constructor. Please use the normal constructor, and add your biPredicate with the addBiPredicate method.
     * @param compatible defines the set of criteria used for compatibility checking.
     */
    @Deprecated public BookshelfMaskSet(BiPredicate<BookshelfMask, BookshelfMask> compatible) {
        bookshelfMasks = new ArrayList<>();
        predicate = a -> true;
        addBiPredicate(compatible);
    }

    /**
     * Constructor, with no set constraints applied.
     */
    public BookshelfMaskSet() {
        bookshelfMasks = new ArrayList<>();
        predicate = a -> true;
    }

    /**
     * Method for adding a predicate to the current set rules.
     * @param predicate will be added as a logic "and" to the current predicate.
     * @throws IllegalStateException if this method is called with a non-empty set.
     */
    public void addPredicate(Predicate<BookshelfMask> predicate) throws IllegalStateException {
        if (getSize() != 0) {
            throw new IllegalStateException("Predicates can only be added to empty sets.");
        }
        this.predicate = this.predicate.and(predicate);
    }

    /**
     * Method for adding a biPredicate to the current set rules.
     * @param biPredicate will be converted and added as a logic "and" to the current predicate.
     * @throws IllegalStateException if this method is called with a non-empty set.
     */
    public void addBiPredicate(BiPredicate<BookshelfMask, BookshelfMask> biPredicate) throws IllegalStateException {
        if (getSize() != 0) {
            throw new IllegalStateException("BiPredicates can only be added to empty sets.");
        }
        addPredicate(toPredicate(biPredicate));
    }

    /**
     * Constructor used as means of copying.
     * @param bookshelfMaskSet will be used as reference to create an identical object.
     */
    public BookshelfMaskSet(BookshelfMaskSet bookshelfMaskSet) {
        bookshelfMasks = bookshelfMaskSet.getBookshelfMasks();
        this.predicate = bookshelfMaskSet.getPredicate();
    }

    /**
     * Getter method for the internal predicate object.
     * @return the predicate object.
     */
    public Predicate<BookshelfMask> getPredicate() { return predicate; }

    /**
     * @deprecated getter method for a non-existent biPredicate object.
     * @return a synthetic biPredicate object.
     */
    @Deprecated public BiPredicate<BookshelfMask, BookshelfMask> getCompatible() {
        return (a, b) -> getPredicate().test(b);
    }

    /**
     * @return the amount of elements in this set.
     */
    public int getSize() {
        return bookshelfMasks.size();
    }

    /**
     * @return a copy of the list of all contained {@link BookshelfMask BookshelfMasks}.
     */
    public List<BookshelfMask> getBookshelfMasks() {
        return new ArrayList<>(bookshelfMasks);
    }

    /**
     * Add an element to the set (by copying it).
     * @param bookshelfMask will be added to the set.
     * @deprecated please use "add".
     */
    @Deprecated public void addBookshelfMask(BookshelfMask bookshelfMask) {
        bookshelfMasks.add(new BookshelfMask(bookshelfMask));
    }

    /**
     * Adds a mask to the set if the mask respects the set criteria.
     * @param bookshelfMask is the mask we want to add to the set.
     * @return true if the mask was added, false if not.
     */
    public boolean add(BookshelfMask bookshelfMask) {
        if (predicate.test(bookshelfMask)) {
            bookshelfMasks.add(new BookshelfMask(bookshelfMask));
            return true;
        }
        return false;
    }

    /**
     * This method checks if the given input is compatible with all other elements of the set.
     * @param bookshelfMask will be checked against all other elements.
     * @return true if all compatibility criteria are met with all set elements.
     * @deprecated because compatibility checks are an internal process of this object. Use the "add" method instead.
     */
    @Deprecated public boolean isCompatible(BookshelfMask bookshelfMask) { return predicate.test(bookshelfMask); }

    /**
     * This method tells the user if the provided mask is inside the set or not.
     * @param bookshelfMask will be provided in order to be compared to all other masks in set.
     * @return true if the provided mask is inside the set or not.
     */
    public boolean contains(BookshelfMask bookshelfMask) {
        for (BookshelfMask containedBookshelfMask : bookshelfMasks) {
            if (bookshelfMask.equals(containedBookshelfMask)) return true;
        }
        return false;
    }

    /**
     * Clears the set by removing all elements.
     * @author Francesco Gemma
     */
    public void clear() {
        bookshelfMasks.clear();
    }
}