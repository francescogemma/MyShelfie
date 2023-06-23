package it.polimi.ingsw.model.bookshelf;

/**
 * Exception thrown when we try to add too many {@link it.polimi.ingsw.model.tile.Tile}s to a {@link Bookshelf}
 * column with the respect to the number of empty {@link Shelf Shelves}.
 *
 * @author Cristiano Migali
 */
public class NotEnoughSpaceInColumnException extends RuntimeException {
    /**
     * It is the index of the column where we tried to insert too many tiles.
     */
    private final int column;

    /**
     * It is the number of tiles that we tried to insert in the column.
     */
    private final int insertionSize;

    /**
     * Constructor of the class.
     * It initializes the column number and the number of tiles that we tried to insert.
     *
     * @param column is the number of the column where we tried to insert too many tiles.
     * @param insertionSize it is the number of tiles that we tried to insert in the column.
     */
    public NotEnoughSpaceInColumnException(int column, int insertionSize) {
        this.column = column;
        this.insertionSize = insertionSize;
    }

    @Override
    public String toString() {
        return "Column " + column + " has not enough space to insert " + insertionSize + " tiles";
    }
}
