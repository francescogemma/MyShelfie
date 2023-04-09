package it.polimi.ingsw.model.bookshelf;

public class DuplicateShelfException extends RuntimeException {
    private final Shelf shelf;

    public DuplicateShelfException(Shelf shelf) {
        this.shelf = shelf;
    }

    @Override
    public String toString() {
        return "The " + shelf + " has already been inserted in the mask";
    }
}
