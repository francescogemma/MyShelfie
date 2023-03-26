package it.polimi.ingsw.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

public class LibraryMaskTest {
    private LibraryMask libraryMask;

    @BeforeEach
    public void setUp() {
        Library library = new Library();

        library.insertTiles(new ArrayList<>(Arrays.asList(
            Tile.MAGENTA, Tile.YELLOW, Tile.GREEN
        )), 0);

        library.insertTiles(new ArrayList<>(Arrays.asList(
            Tile.BLUE, Tile.CYAN
        )), 0);

        library.insertTiles(new ArrayList<>(Arrays.asList(
            Tile.GREEN, Tile.MAGENTA, Tile.WHITE
        )), 1);

        library.insertTiles(new ArrayList<>(Arrays.asList(
            Tile.WHITE, Tile.WHITE, Tile.YELLOW
        )), 2);

        library.insertTiles(new ArrayList<>(Arrays.asList(
            Tile.BLUE
        )), 2);

        library.insertTiles(new ArrayList<>(Arrays.asList(
            Tile.BLUE, Tile.CYAN
        )), 3);

        library.insertTiles(new ArrayList<>(Arrays.asList(
            Tile.MAGENTA, Tile.YELLOW, Tile.GREEN
        )), 4);

        library.insertTiles(new ArrayList<>(Arrays.asList(
            Tile.BLUE, Tile.CYAN, Tile.WHITE
        )), 4);

        libraryMask = new LibraryMask(library);
    }

    @Test
    @DisplayName("Clear mask")
    public void clear_correctOutput() {
        libraryMask.add(Shelf.origin());
        libraryMask.add(Shelf.origin().move(Offset.right()));
        libraryMask.add(Shelf.origin().move(Offset.down()));

        libraryMask.clear();

        Assertions.assertEquals(0, libraryMask.getShelves().size());
    }

    @Test
    @DisplayName("Try to insert duplicate shelf in the mask")
    public void add_duplicateShelf_throwsRuntimeException() {
        libraryMask.add(Shelf.origin());
        libraryMask.add(Shelf.origin().move(Offset.right()));
        libraryMask.add(Shelf.origin().move(Offset.down()));

        Assertions.assertThrows(RuntimeException.class, () -> {
           libraryMask.add(Shelf.origin());
        });
    }

    @Test
    @DisplayName("Fill the mask with several shelves and get them back")
    public void addAndGetShelves_correctInput_correctOutput() {
        ArrayList<Shelf> toAdd = new ArrayList<>(Arrays.asList(
           Shelf.getInstance(1, 1), Shelf.getInstance(1, 2),
           Shelf.getInstance(3, 4), Shelf.getInstance(2, 2)
        ));

        for (Shelf shelf : toAdd) {
            libraryMask.add(shelf);
        }

        toAdd.sort((firstShelf, secondShelf) -> {
            if (firstShelf.equals(secondShelf)) {
                return 0;
            }

            return firstShelf.before(secondShelf) ? -1 : 1;
        });

        Assertions.assertEquals(toAdd, libraryMask.getShelves());
    }

    @Test
    @DisplayName("Get sample tile from the mask")
    public void getSampleTile_correctOutput() {
        libraryMask.add(Shelf.origin());

        Assertions.assertEquals(Tile.EMPTY, libraryMask.getSampleTile());
    }
}
