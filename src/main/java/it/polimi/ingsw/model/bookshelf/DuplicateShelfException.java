package it.polimi.ingsw.model.bookshelf;

/**
 * Exception thrown when we try to add a {@link Shelf} to a {@link BookshelfMask} which already contains it.
 *
 * @author Cristiano Migali
 */
public class DuplicateShelfException extends RuntimeException {
    /**
     * It is the {@link Shelf} already contained in the {@link BookshelfMask}.
     */
    private final Shelf shelf;

    /**
     * Constructor of the class.
     *
     * @param shelf is the {@link Shelf} which we tried to add to a {@link BookshelfMask} which already contained it.
     */
    public DuplicateShelfException(Shelf shelf) {
        this.shelf = shelf;
    }

    @Override
    public String toString() {
        return "The " + shelf + " has already been inserted in the mask";
    }
}
