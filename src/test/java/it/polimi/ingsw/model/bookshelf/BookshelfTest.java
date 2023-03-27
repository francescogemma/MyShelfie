package it.polimi.ingsw.model.bookshelf;

import it.polimi.ingsw.model.Tile;
import it.polimi.ingsw.model.bookshelf.Bookshelf;
import it.polimi.ingsw.model.bookshelf.Shelf;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

class BookshelfTest {
    @Test
    @DisplayName("Check if row index actually inside the bookshelf is in the bookshelf")
    void isRowInsideTheBookshelf_rowInsideTheBookshelfIndex_correctOutput() {
        Assertions.assertTrue(Bookshelf.isRowInsideTheBookshelf(Bookshelf.ROWS / 2));
    }

    @Test
    @DisplayName("Check if negative row index is in the bookshelf")
    void isRowInsideTheBookshelf_negativeRowIndex_correctOutput() {
        Assertions.assertFalse(Bookshelf.isRowInsideTheBookshelf(-1));
    }

    @Test
    @DisplayName("Check if too big row index is in the bookshelf")
    void isRowInsideTheBookshelf_tooBigIndex_correctOutput() {
        Assertions.assertFalse(Bookshelf.isRowInsideTheBookshelf(Bookshelf.ROWS));
    }

    @Test
    @DisplayName("Check if column index actually inside the bookshelf is in the bookshelf")
    void isColumnInsideTheBookshelf_columnInsideTheBookshelfIndex_correctOutput() {
        Assertions.assertTrue(Bookshelf.isColumnInsideTheBookshelf(Bookshelf.COLUMNS/2));
    }

    @Test
    @DisplayName("Check if negative column index is in the bookshelf")
    void isColumnInsideTheBookshelf_negativeColumnIndex_correctOutput() {
        Assertions.assertFalse(Bookshelf.isColumnInsideTheBookshelf(-1));
    }

    @Test
    @DisplayName("Check if too big column index is in the bookshelf")
    void isColumnInsideTheBookshelf_tooBigColumnIndex_correctOutput() {
        Assertions.assertFalse(Bookshelf.isColumnInsideTheBookshelf(Bookshelf.COLUMNS));
    }

    private Bookshelf bookshelf;

    @BeforeEach
    public void setUp() {
        bookshelf = new Bookshelf();
    }

    @Test
    @DisplayName("Try to get tile in null shelf, should throw exception")
    void get_nullShelf_throwsNullPointerException() {
        Assertions.assertThrows(NullPointerException.class, () -> {
            bookshelf.get(null);
        });
    }

    @Test
    @DisplayName("Insert tiles inside the bookshelf")
    void insertTiles_correctInput_correctOutput() {
        ArrayList<Tile> firstInsertion = new ArrayList<>(Arrays.asList(
            Tile.GREEN, Tile.BLUE, Tile.CYAN
        ));

        ArrayList<Tile> secondInsertion = new ArrayList<>(Arrays.asList(
            Tile.YELLOW, Tile.MAGENTA
        ));

        ArrayList<Tile> thirdInsertion = new ArrayList<>(Arrays.asList(
           Tile.BLUE
        ));

        bookshelf.insertTiles(firstInsertion, 0);
        bookshelf.insertTiles(secondInsertion, 1);
        bookshelf.insertTiles(thirdInsertion, 0);

        // This test case also exerts get code
        Assertions.assertEquals(Tile.GREEN,
            bookshelf.get(Shelf.getInstance(Bookshelf.ROWS - 1, 0)));
        Assertions.assertEquals(Tile.BLUE,
            bookshelf.get(Shelf.getInstance(Bookshelf.ROWS - 2, 0)));
        Assertions.assertEquals(Tile.CYAN,
            bookshelf.get(Shelf.getInstance(Bookshelf.ROWS - 3, 0)));
        Assertions.assertEquals(Tile.YELLOW,
            bookshelf.get(Shelf.getInstance(Bookshelf.ROWS - 1, 1)));
        Assertions.assertEquals(Tile.MAGENTA,
            bookshelf.get(Shelf.getInstance(Bookshelf.ROWS - 2, 1)));
        Assertions.assertEquals(Tile.BLUE,
            bookshelf.get(Shelf.getInstance(Bookshelf.ROWS - 4, 0)));
    }

    @Test
    @DisplayName("Try to insert tiles in a column outside of the bookshelf, should throw exception")
    void insertTiles_columnOutsideTheBookshelf_throwsIllegalArgumentException() {
        ArrayList<Tile> insertion = new ArrayList<>(Arrays.asList(
           Tile.MAGENTA, Tile.MAGENTA, Tile.MAGENTA
        ));

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            bookshelf.insertTiles(insertion, Bookshelf.COLUMNS);
        });
    }

    @Test
    @DisplayName("Try to insert more tiles than the maximum insertion size, should throw exception")
    void insertTiles_tooManyTilesInSingleInsertion_throwsIllegalArgumentException() {
        ArrayList<Tile> insertion = new ArrayList<>(Arrays.asList(
           Tile.MAGENTA, Tile.MAGENTA, Tile.MAGENTA, Tile.MAGENTA
        ));

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            bookshelf.insertTiles(insertion, 0);
        });
    }

    @Test
    @DisplayName("Try to insert an empty tile inside the bookshelf, should throw exception")
    void insertTiles_emptyTileInInsertion_throwsIllegalArgumentException() {
        ArrayList<Tile> insertion = new ArrayList<>(Arrays.asList(
            Tile.MAGENTA, Tile.EMPTY, Tile.MAGENTA
        ));

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            bookshelf.insertTiles(insertion, 0);
        });
    }

    @Test
    @DisplayName("Try to insert more tiles than the ones which fit inside a column, should throw exception")
    void insertTiles_moreTilesThanAvailableSpaceInColumn_throwsRuntimeException() {
        ArrayList<Tile> firstInsertion = new ArrayList<>(Arrays.asList(
           Tile.MAGENTA, Tile.MAGENTA, Tile.MAGENTA
        ));

        ArrayList<Tile> secondInsertion = new ArrayList<>(Arrays.asList(
           Tile.MAGENTA, Tile.MAGENTA
        ));

        ArrayList<Tile> thirdInsertion = new ArrayList<>(Arrays.asList(
            Tile.MAGENTA, Tile.MAGENTA
        ));

        bookshelf.insertTiles(firstInsertion, 0);
        bookshelf.insertTiles(secondInsertion, 0);

        Assertions.assertThrows(RuntimeException.class, () -> {
            bookshelf.insertTiles(thirdInsertion, 0);
        });
    }
}
