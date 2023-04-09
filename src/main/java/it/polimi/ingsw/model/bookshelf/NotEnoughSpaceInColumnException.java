package it.polimi.ingsw.model.bookshelf;

public class NotEnoughSpaceInColumnException extends RuntimeException {
    private final int column;
    private final int insertionSize;

    public NotEnoughSpaceInColumnException(int column, int insertionSize) {
        this.column = column;
        this.insertionSize = insertionSize;
    }

    @Override
    public String toString() {
        return "Column " + column + " has not enough space to insert " + insertionSize + " tiles";
    }
}
