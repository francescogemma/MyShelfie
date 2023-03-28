package it.polimi.ingsw.model.bookshelf;

import it.polimi.ingsw.model.Tile;
import it.polimi.ingsw.model.bookshelf.Bookshelf;
import it.polimi.ingsw.model.bookshelf.BookshelfMask;
import it.polimi.ingsw.model.bookshelf.Offset;
import it.polimi.ingsw.model.bookshelf.Shelf;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

class BookshelfMaskTest {
    private BookshelfMask bookshelfMask;

    @BeforeEach
    public void setUp() {
        Bookshelf bookshelf = new Bookshelf();

        bookshelf.insertTiles(new ArrayList<>(Arrays.asList(
            Tile.MAGENTA, Tile.YELLOW, Tile.GREEN
        )), 0);

        bookshelf.insertTiles(new ArrayList<>(Arrays.asList(
            Tile.BLUE, Tile.CYAN
        )), 0);

        bookshelf.insertTiles(new ArrayList<>(Arrays.asList(
            Tile.GREEN, Tile.MAGENTA, Tile.WHITE
        )), 1);

        bookshelf.insertTiles(new ArrayList<>(Arrays.asList(
            Tile.WHITE, Tile.WHITE, Tile.YELLOW
        )), 2);

        bookshelf.insertTiles(new ArrayList<>(Arrays.asList(
            Tile.BLUE
        )), 2);

        bookshelf.insertTiles(new ArrayList<>(Arrays.asList(
            Tile.BLUE, Tile.CYAN
        )), 3);

        bookshelf.insertTiles(new ArrayList<>(Arrays.asList(
            Tile.MAGENTA, Tile.YELLOW, Tile.GREEN
        )), 4);

        bookshelf.insertTiles(new ArrayList<>(Arrays.asList(
            Tile.BLUE, Tile.CYAN, Tile.WHITE
        )), 4);

        bookshelfMask = new BookshelfMask(bookshelf);
    }

    @Test
    @DisplayName("Clear mask")
    void clear_correctOutput() {
        bookshelfMask.add(Shelf.origin());
        bookshelfMask.add(Shelf.origin().move(Offset.right()));
        bookshelfMask.add(Shelf.origin().move(Offset.down()));

        bookshelfMask.clear();

        Assertions.assertEquals(0, bookshelfMask.getShelves().size());
    }

    @Test
    @DisplayName("Try to insert duplicate shelf in the mask")
    void add_duplicateShelf_throwsRuntimeException() {
        bookshelfMask.add(Shelf.origin());
        bookshelfMask.add(Shelf.origin().move(Offset.right()));
        bookshelfMask.add(Shelf.origin().move(Offset.down()));

        Assertions.assertThrows(RuntimeException.class, () -> {
           bookshelfMask.add(Shelf.origin());
        });
    }

    @Test
    @DisplayName("Fill the mask with several shelves and get them back")
    void addAndGetShelves_correctInput_correctOutput() {
        ArrayList<Shelf> toAdd = new ArrayList<>(Arrays.asList(
           Shelf.getInstance(1, 1), Shelf.getInstance(1, 2),
           Shelf.getInstance(3, 4), Shelf.getInstance(2, 2)
        ));

        for (Shelf shelf : toAdd) {
            bookshelfMask.add(shelf);
        }

        toAdd.sort((firstShelf, secondShelf) -> {
            if (firstShelf.equals(secondShelf)) {
                return 0;
            }

            return firstShelf.before(secondShelf) ? -1 : 1;
        });

        Assertions.assertEquals(toAdd, bookshelfMask.getShelves());
    }

    @Test
    @DisplayName("Get sample tile from the mask")
    void getSampleTile_correctOutput() {
        bookshelfMask.add(Shelf.origin());

        Assertions.assertEquals(Tile.EMPTY, bookshelfMask.getSampleTile());
    }

    @Test
    @DisplayName("Count magenta tiles in the mask")
    void countTilesOfColorMagenta_correctOutput() {
        populateFullMask();
        Assertions.assertEquals(3, bookshelfMask.countTilesOfColor(Tile.MAGENTA));
    }

    @Test
    @DisplayName("Count yellow tiles in the mask")
    void countTilesOfColorYellow_correctOutput() {
        populateFullMask();
        Assertions.assertEquals(3, bookshelfMask.countTilesOfColor(Tile.YELLOW));
    }

    @Test
    @DisplayName("Count green tiles in the mask")
    void countTilesOfColorGreen_correctOutput() {
        populateFullMask();
        Assertions.assertEquals(3, bookshelfMask.countTilesOfColor(Tile.GREEN));
    }

    @Test
    @DisplayName("Count blue tiles in the mask")
    void countTilesOfColorBlue_correctOutput() {
        populateFullMask();
        Assertions.assertEquals(4, bookshelfMask.countTilesOfColor(Tile.BLUE));
    }

    @Test
    @DisplayName("Count cyan tiles in the mask")
    void countTilesOfColorCyan_correctOutput() {
        populateFullMask();
        Assertions.assertEquals(3, bookshelfMask.countTilesOfColor(Tile.CYAN));
    }

    @Test
    @DisplayName("Count white tiles in the mask")
    void countTilesOfColorWhite_correctOutput() {
        populateFullMask();
        Assertions.assertEquals(4, bookshelfMask.countTilesOfColor(Tile.WHITE));
    }

    @Test
    @DisplayName("Try to count empty tiles in the mask, should throw an IllegalArgumentException")
    void countTilesOfColorEmpty_throwsIllegalArgumentException() {
        populateFullMask();
        Assertions.assertThrows(IllegalArgumentException.class, () -> bookshelfMask.countTilesOfColor(Tile.EMPTY));
    }

    private void populateFullMask() {
        for(int row = 0; row < Bookshelf.ROWS; row++) {
            for(int column = 0; column < Bookshelf.COLUMNS; column++) {
                bookshelfMask.add(Shelf.getInstance(row, column));
            }
        }
    }
}
