package it.polimi.ingsw.model.bookshelf;

/**
 * Exception thrown when we try to shift a {@link Shelf} by an {@link Offset} which would bring it outside of
 * the {@link Bookshelf}.
 *
 * @author Cristiano Migali
 */
public class MoveOutOfBookshelfException extends RuntimeException {
    /**
     * It is the {@link Shelf} that we tried to move out of the {@link Bookshelf}.
     */
    private final Shelf shelf;

    /**
     * It is the {@link Offset} by which we have tried to move the {@link Shelf}.
     */
    private final Offset offset;

    /**
     * Constructor of the class.
     * It initializes the {@link Shelf} and {@link Offset} which led to the exception.
     *
     * @param shelf is the {@link Shelf} that we tried to move out of the {@link Bookshelf}.
     * @param offset is the {@link Offset} by which we have tried to move the {@link Shelf}.
     */
    public MoveOutOfBookshelfException(Shelf shelf, Offset offset) {
        this.shelf = shelf;
        this.offset = offset;
    }

    @Override
    public String toString() {
        return "Trying to move " + shelf + " by " + offset;
    }
}
