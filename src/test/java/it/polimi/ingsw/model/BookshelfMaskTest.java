package it.polimi.ingsw.model;

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
}
