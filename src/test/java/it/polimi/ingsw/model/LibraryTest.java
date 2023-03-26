package it.polimi.ingsw.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

public class LibraryTest {
    @Test
    @DisplayName("Check if row index actually inside the library is in the library")
    public void isRowInsideTheLibrary_rowInsideTheLibraryIndex_correctOutput() {
        Assertions.assertTrue(Library.isRowInsideTheLibrary(Library.ROWS / 2));
    }

    @Test
    @DisplayName("Check if negative row index is in the library")
    public void isRowInsideTheLibrary_negativeRowIndex_correctOutput() {
        Assertions.assertFalse(Library.isRowInsideTheLibrary(-1));
    }

    @Test
    @DisplayName("Check if too big row index is in the library")
    public void isRowInsideTheLibrary_tooBigIndex_correctOutput() {
        Assertions.assertFalse(Library.isRowInsideTheLibrary(Library.ROWS));
    }

    @Test
    @DisplayName("Check if column index actually inside the library is in the library")
    public void isColumnInsideTheLibrary_columnInsideTheLibraryIndex_correctOutput() {
        Assertions.assertTrue(Library.isColumnInsideTheLibrary(Library.COLUMNS/2));
    }

    @Test
    @DisplayName("Check if negative column index is in the library")
    public void isColumnInsideTheLibrary_negativeColumnIndex_correctOutput() {
        Assertions.assertFalse(Library.isColumnInsideTheLibrary(-1));
    }

    @Test
    @DisplayName("Check if too big column index is in the library")
    public void isColumnInsideTheLibrary_tooBigColumnIndex_correctOutput() {
        Assertions.assertFalse(Library.isColumnInsideTheLibrary(Library.COLUMNS));
    }

    private Library library;

    @BeforeEach
    public void setUp() {
        library = new Library();
    }

    @Test
    @DisplayName("Try to get tile in null shelf, should throw exception")
    public void get_nullShelf_throwsNullPointerException() {
        Assertions.assertThrows(NullPointerException.class, () -> {
            library.get(null);
        });
    }

    @Test
    @DisplayName("Insert tiles inside the library")
    public void insertTiles_correctInput_correctOutput() {
        ArrayList<Tile> firstInsertion = new ArrayList<>(Arrays.asList(
            Tile.GREEN, Tile.BLUE, Tile.CYAN
        ));

        ArrayList<Tile> secondInsertion = new ArrayList<>(Arrays.asList(
            Tile.YELLOW, Tile.MAGENTA
        ));

        ArrayList<Tile> thirdInsertion = new ArrayList<>(Arrays.asList(
           Tile.BLUE
        ));

        library.insertTiles(firstInsertion, 0);
        library.insertTiles(secondInsertion, 1);
        library.insertTiles(thirdInsertion, 0);

        // This test case also exerts get code
        Assertions.assertEquals(Tile.GREEN,
            library.get(Shelf.getInstance(Library.ROWS - 1, 0)));
        Assertions.assertEquals(Tile.BLUE,
            library.get(Shelf.getInstance(Library.ROWS - 2, 0)));
        Assertions.assertEquals(Tile.CYAN,
            library.get(Shelf.getInstance(Library.ROWS - 3, 0)));
        Assertions.assertEquals(Tile.YELLOW,
            library.get(Shelf.getInstance(Library.ROWS - 1, 1)));
        Assertions.assertEquals(Tile.MAGENTA,
            library.get(Shelf.getInstance(Library.ROWS - 2, 1)));
        Assertions.assertEquals(Tile.BLUE,
            library.get(Shelf.getInstance(Library.ROWS - 4, 0)));
    }

    @Test
    @DisplayName("Try to insert tiles in a column outside of the library, should throw exception")
    public void insertTiles_columnOutsideTheLibrary_throwsIllegalArgumentException() {
        ArrayList<Tile> insertion = new ArrayList<>(Arrays.asList(
           Tile.MAGENTA, Tile.MAGENTA, Tile.MAGENTA
        ));

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            library.insertTiles(insertion, Library.COLUMNS);
        });
    }

    @Test
    @DisplayName("Try to insert more tiles than the maximum insertion size, should throw exception")
    public void insertTiles_tooManyTilesInSingleInsertion_throwsIllegalArgumentException() {
        ArrayList<Tile> insertion = new ArrayList<>(Arrays.asList(
           Tile.MAGENTA, Tile.MAGENTA, Tile.MAGENTA, Tile.MAGENTA
        ));

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            library.insertTiles(insertion, 0);
        });
    }

    @Test
    @DisplayName("Try to insert an empty tile inside the library, should throw exception")
    public void insertTiles_emptyTileInInsertion_throwsIllegalArgumentException() {
        ArrayList<Tile> insertion = new ArrayList<>(Arrays.asList(
            Tile.MAGENTA, Tile.EMPTY, Tile.MAGENTA
        ));

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            library.insertTiles(insertion, 0);
        });
    }

    @Test
    @DisplayName("Try to insert more tiles than the ones which fit inside a column, should throw exception")
    public void insertTiles_moreTilesThanAvailableSpaceInColumn_throwsRuntimeException() {
        ArrayList<Tile> firstInsertion = new ArrayList<>(Arrays.asList(
           Tile.MAGENTA, Tile.MAGENTA, Tile.MAGENTA
        ));

        ArrayList<Tile> secondInsertion = new ArrayList<>(Arrays.asList(
           Tile.MAGENTA, Tile.MAGENTA
        ));

        ArrayList<Tile> thirdInsertion = new ArrayList<>(Arrays.asList(
            Tile.MAGENTA, Tile.MAGENTA
        ));

        library.insertTiles(firstInsertion, 0);
        library.insertTiles(secondInsertion, 0);

        Assertions.assertThrows(RuntimeException.class, () -> {
            library.insertTiles(thirdInsertion, 0);
        });
    }
}
