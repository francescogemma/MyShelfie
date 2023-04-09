package it.polimi.ingsw.model.bookshelf;

public class MoveOutOfBookshelfException extends RuntimeException {
    private final Shelf shelf;
    private final Offset offset;

    public MoveOutOfBookshelfException(Shelf shelf, Offset offset) {
        this.shelf = shelf;
        this.offset = offset;
    }

    @Override
    public String toString() {
        return "Trying to move " + shelf + " by " + offset;
    }
}
