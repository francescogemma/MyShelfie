package it.polimi.ingsw.model.bookshelf;

import it.polimi.ingsw.model.tile.Tile;
import it.polimi.ingsw.model.tile.TileColor;
import it.polimi.ingsw.model.tile.TileVersion;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class BookshelfMaskTest {
    private BookshelfMask bookshelfMask;

    @BeforeEach
    public void setUp() {
        Bookshelf bookshelf = new Bookshelf();

        bookshelf.insertTiles(List.of(
            Tile.getInstance(TileColor.MAGENTA, TileVersion.FIRST),
            Tile.getInstance(TileColor.YELLOW, TileVersion.FIRST),
            Tile.getInstance(TileColor.GREEN, TileVersion.FIRST)
        ), 0);

        bookshelf.insertTiles(List.of(
            Tile.getInstance(TileColor.BLUE, TileVersion.FIRST),
            Tile.getInstance(TileColor.CYAN, TileVersion.FIRST)
        ), 0);

        bookshelf.insertTiles(List.of(
            Tile.getInstance(TileColor.GREEN, TileVersion.FIRST),
            Tile.getInstance(TileColor.MAGENTA, TileVersion.FIRST),
            Tile.getInstance(TileColor.WHITE, TileVersion.FIRST)
        ), 1);

        bookshelf.insertTiles(List.of(
            Tile.getInstance(TileColor.WHITE, TileVersion.FIRST),
            Tile.getInstance(TileColor.WHITE, TileVersion.FIRST),
            Tile.getInstance(TileColor.YELLOW, TileVersion.FIRST)
        ), 2);

        bookshelf.insertTiles(List.of(
            Tile.getInstance(TileColor.BLUE, TileVersion.FIRST)
        ), 2);

        bookshelf.insertTiles(List.of(
            Tile.getInstance(TileColor.BLUE, TileVersion.FIRST),
            Tile.getInstance(TileColor.CYAN, TileVersion.FIRST)
        ), 3);

        bookshelf.insertTiles(List.of(
            Tile.getInstance(TileColor.MAGENTA, TileVersion.FIRST),
            Tile.getInstance(TileColor.YELLOW, TileVersion.FIRST),
            Tile.getInstance(TileColor.GREEN, TileVersion.FIRST)
        ), 4);

        bookshelf.insertTiles(List.of(
            Tile.getInstance(TileColor.BLUE, TileVersion.FIRST),
            Tile.getInstance(TileColor.CYAN, TileVersion.FIRST),
            Tile.getInstance(TileColor.WHITE, TileVersion.FIRST)
        ), 4);

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
        // This must be an ArrayList, sort is not supported on the ImmutableList returned by List.of.
        List<Shelf> toAdd = new ArrayList<>(List.of(
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

        Assertions.assertEquals(TileColor.EMPTY, bookshelfMask.getSampleTile());
    }

    @Test
    @DisplayName("Count magenta tiles in the mask")
    void countTilesOfColorMagenta_correctOutput() {
        populateFullMask();
        Assertions.assertEquals(3, bookshelfMask.countTilesOfColor(TileColor.MAGENTA));
    }

    @Test
    @DisplayName("Count yellow tiles in the mask")
    void countTilesOfColorYellow_correctOutput() {
        populateFullMask();
        Assertions.assertEquals(3, bookshelfMask.countTilesOfColor(TileColor.YELLOW));
    }

    @Test
    @DisplayName("Count green tiles in the mask")
    void countTilesOfColorGreen_correctOutput() {
        populateFullMask();
        Assertions.assertEquals(3, bookshelfMask.countTilesOfColor(TileColor.GREEN));
    }

    @Test
    @DisplayName("Count blue tiles in the mask")
    void countTilesOfColorBlue_correctOutput() {
        populateFullMask();
        Assertions.assertEquals(4, bookshelfMask.countTilesOfColor(TileColor.BLUE));
    }

    @Test
    @DisplayName("Count cyan tiles in the mask")
    void countTilesOfColorCyan_correctOutput() {
        populateFullMask();
        Assertions.assertEquals(3, bookshelfMask.countTilesOfColor(TileColor.CYAN));
    }

    @Test
    @DisplayName("Count white tiles in the mask")
    void countTilesOfColorWhite_correctOutput() {
        populateFullMask();
        Assertions.assertEquals(4, bookshelfMask.countTilesOfColor(TileColor.WHITE));
    }

    @Test
    @DisplayName("Try to count empty tiles in the mask, should throw an IllegalArgumentException")
    void countTilesOfColorEmpty_throwsIllegalArgumentException() {
        populateFullMask();
        Assertions.assertThrows(IllegalArgumentException.class, () -> bookshelfMask.countTilesOfColor(TileColor.EMPTY));
    }

    private void populateFullMask() {
        for(int row = 0; row < Bookshelf.ROWS; row++) {
            for(int column = 0; column < Bookshelf.COLUMNS; column++) {
                bookshelfMask.add(Shelf.getInstance(row, column));
            }
        }
    }
}
