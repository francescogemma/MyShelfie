package it.polimi.ingsw.model.bookshelf;

import it.polimi.ingsw.model.tile.Tile;
import it.polimi.ingsw.model.tile.TileColor;
import it.polimi.ingsw.model.tile.TileVersion;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

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
            bookshelf.getTileColorAt(null);
        });
    }

    @Test
    @DisplayName("Insert tiles inside the bookshelf")
    void insertTiles_correctInput_correctOutput() {
        List<Tile> firstInsertion = List.of(
            Tile.getInstance(TileColor.GREEN, TileVersion.FIRST),
            Tile.getInstance(TileColor.BLUE, TileVersion.FIRST),
            Tile.getInstance(TileColor.CYAN, TileVersion.FIRST)
        );

        List<Tile> secondInsertion = List.of(
            Tile.getInstance(TileColor.YELLOW, TileVersion.FIRST),
            Tile.getInstance(TileColor.MAGENTA, TileVersion.FIRST)
        );

        List<Tile> thirdInsertion = List.of(
            Tile.getInstance(TileColor.BLUE, TileVersion.FIRST)
        );

        bookshelf.insertTiles(firstInsertion, 0);
        bookshelf.insertTiles(secondInsertion, 1);
        bookshelf.insertTiles(thirdInsertion, 0);

        // This test case also exerts get code
        Assertions.assertEquals(TileColor.GREEN,
            bookshelf.getTileColorAt(Shelf.getInstance(Bookshelf.ROWS - 1, 0)));
        Assertions.assertEquals(TileColor.BLUE,
            bookshelf.getTileColorAt(Shelf.getInstance(Bookshelf.ROWS - 2, 0)));
        Assertions.assertEquals(TileColor.CYAN,
            bookshelf.getTileColorAt(Shelf.getInstance(Bookshelf.ROWS - 3, 0)));
        Assertions.assertEquals(TileColor.YELLOW,
            bookshelf.getTileColorAt(Shelf.getInstance(Bookshelf.ROWS - 1, 1)));
        Assertions.assertEquals(TileColor.MAGENTA,
            bookshelf.getTileColorAt(Shelf.getInstance(Bookshelf.ROWS - 2, 1)));
        Assertions.assertEquals(TileColor.BLUE,
            bookshelf.getTileColorAt(Shelf.getInstance(Bookshelf.ROWS - 4, 0)));
    }

    @Test
    @DisplayName("Try to insert tiles in a column outside of the bookshelf, should throw exception")
    void insertTiles_columnOutsideTheBookshelf_throwsIllegalArgumentException() {
        List<Tile> insertion = List.of(
            Tile.getInstance(TileColor.MAGENTA, TileVersion.FIRST),
            Tile.getInstance(TileColor.MAGENTA, TileVersion.FIRST),
            Tile.getInstance(TileColor.MAGENTA, TileVersion.FIRST)
        );

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            bookshelf.insertTiles(insertion, Bookshelf.COLUMNS);
        });
    }

    @Test
    @DisplayName("Try to insert more tiles than the maximum insertion size, should throw exception")
    void insertTiles_tooManyTilesInSingleInsertion_throwsIllegalArgumentException() {
        List<Tile> insertion = List.of(
            Tile.getInstance(TileColor.MAGENTA, TileVersion.FIRST),
           Tile.getInstance(TileColor.MAGENTA, TileVersion.FIRST),
           Tile.getInstance(TileColor.MAGENTA, TileVersion.FIRST),
           Tile.getInstance(TileColor.MAGENTA, TileVersion.FIRST)
        );

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            bookshelf.insertTiles(insertion, 0);
        });
    }

    @Test
    @DisplayName("Try to insert an empty tile inside the bookshelf, should throw exception")
    void insertTiles_emptyTileInInsertion_throwsIllegalArgumentException() {
        List<Tile> insertion = List.of(
            Tile.getInstance(TileColor.MAGENTA, TileVersion.FIRST),
            Tile.getInstance(TileColor.EMPTY, TileVersion.FIRST),
            Tile.getInstance(TileColor.MAGENTA, TileVersion.FIRST)
        );

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            bookshelf.insertTiles(insertion, 0);
        });
    }

    @Test
    @DisplayName("Try to insert more tiles than the ones which fit inside a column, should throw exception")
    void insertTiles_moreTilesThanAvailableSpaceInColumn_throwsRuntimeException() {
        List<Tile> firstInsertion = List.of(
            Tile.getInstance(TileColor.MAGENTA, TileVersion.FIRST),
            Tile.getInstance(TileColor.MAGENTA, TileVersion.FIRST),
            Tile.getInstance(TileColor.MAGENTA, TileVersion.FIRST)
        );

        List<Tile> secondInsertion = List.of(
            Tile.getInstance(TileColor.MAGENTA, TileVersion.FIRST),
            Tile.getInstance(TileColor.MAGENTA, TileVersion.FIRST)
        );

        List<Tile> thirdInsertion = List.of(
            Tile.getInstance(TileColor.MAGENTA, TileVersion.FIRST),
            Tile.getInstance(TileColor.MAGENTA, TileVersion.FIRST)
        );

        bookshelf.insertTiles(firstInsertion, 0);
        bookshelf.insertTiles(secondInsertion, 0);

        Assertions.assertThrows(RuntimeException.class, () -> {
            bookshelf.insertTiles(thirdInsertion, 0);
        });
    }
}
