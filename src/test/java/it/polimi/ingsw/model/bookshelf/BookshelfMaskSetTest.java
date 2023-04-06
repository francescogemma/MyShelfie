package it.polimi.ingsw.model.bookshelf;

import it.polimi.ingsw.model.Tile;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.function.BiPredicate;

class BookshelfMaskSetTest {
    private BookshelfMaskSet bookshelfMaskSet;

    @Test
    @DisplayName("Add a mask and check that it is actually a copy")
    void addBookshelfMask_maskCopy_correctOutput() {
        bookshelfMaskSet = new BookshelfMaskSet((a, b) -> true);

        BookshelfMask bookshelfMask = new BookshelfMask(new Bookshelf());
        bookshelfMaskSet.addBookshelfMask(bookshelfMask);

        Assertions.assertNotSame(bookshelfMask, bookshelfMaskSet.getBookshelfMasks().get(0));
    }

    @Test
    @DisplayName("Create a set from another set and check that content is the same.")
    void BookshelfMaskSet_BookshelfMaskSet_correctOutput() {
        bookshelfMaskSet = new BookshelfMaskSet((a, b) -> true);
        BookshelfMaskSet bookshelfMaskSetCopy = new BookshelfMaskSet(bookshelfMaskSet);

        Assertions.assertEquals(bookshelfMaskSet.getBookshelfMasks(), bookshelfMaskSetCopy.getBookshelfMasks());
    }

    @ParameterizedTest
    @DisplayName("Check all getter methods.")
    @ValueSource(ints = {0, 4, 8, 12})
    void getters_correctOutput(int n) {
        BiPredicate<BookshelfMask, BookshelfMask> predicate = (a, b) -> true;
        bookshelfMaskSet = new BookshelfMaskSet(predicate);

        for (int i = 0; i < n; i++) {
            bookshelfMaskSet.addBookshelfMask(new BookshelfMask(new Bookshelf()));
        }

        // add more articulate BookshelfMask from this bookshelf
        Bookshelf bookshelf = new MockBookshelf(new int[][]{
                { 0, 0, 0, 0, 0 },
                { 1, 1, 1, 1, 1 },
                { 2, 2, 2, 2, 2 },
                { 3, 3, 3, 3, 3 },
                { 4, 4, 4, 4, 4 },
                { 5, 5, 5, 5, 5 },
        });
        bookshelfMaskSet.addBookshelfMask(new BookshelfMask(bookshelf));

        ArrayList<BookshelfMask> receivedList = (ArrayList<BookshelfMask>) bookshelfMaskSet.getBookshelfMasks();
        Assertions.assertEquals(Tile.EMPTY, receivedList.get(receivedList.size() - 1).tileAt(Shelf.getInstance(0, 0)));
        Assertions.assertEquals(n + 1, bookshelfMaskSet.getSize());
    }

    @Test
    @DisplayName("Control if compatibility checks work correctly")
    void isCompatible_variousMasks_correctOutput() {
        bookshelfMaskSet = new BookshelfMaskSet(
                (a, b) -> a.tileAt(Shelf.getInstance(1, 1)) == b.tileAt(Shelf.getInstance(2, 2))
        );

        Bookshelf bookshelfFirst = new MockBookshelf(new int[][]{
                { 0, 0, 0, 0, 0 },
                { 1, 1, 1, 1, 1 },
                { 2, 2, 2, 2, 2 },
                { 3, 3, 3, 3, 3 },
                { 4, 4, 4, 4, 4 },
                { 5, 5, 5, 5, 5 },
        });

        Bookshelf bookshelfSecond = new MockBookshelf(new int[][]{
                { 0, 1, 2, 3, 2 },
                { 1, 1, 1, 1, 1 },
                { 2, 2, 1, 2, 2 },
                { 2, 3, 2, 3, 1 },
                { 4, 4, 4, 4, 4 },
                { 5, 4, 3, 2, 3 },
        });

        Bookshelf bookshelfThird = new MockBookshelf(new int[][]{
                { 4, 5, 4, 3, 1 },
                { 1, 1, 1, 1, 1 },
                { 2, 2, 2, 2, 2 },
                { 3, 3, 3, 3, 3 },
                { 1, 3, 2, 5, 1 },
                { 5, 4, 5, 2, 1 },
        });

        bookshelfMaskSet.addBookshelfMask(new BookshelfMask(bookshelfFirst));
        Assertions.assertTrue(bookshelfMaskSet.isCompatible(new BookshelfMask(bookshelfSecond)));
        Assertions.assertFalse(bookshelfMaskSet.isCompatible(new BookshelfMask(bookshelfThird)));
    }

    @ParameterizedTest
    @DisplayName("Check clearSet method")
    @ValueSource(ints = {0, 4, 8, 12})
    void clearSet_correctOutput(int n) {
        BiPredicate<BookshelfMask, BookshelfMask> predicate = (a, b) -> true;
        bookshelfMaskSet = new BookshelfMaskSet(predicate);

        for (int i = 0; i < n; i++) {
            bookshelfMaskSet.addBookshelfMask(new BookshelfMask(new Bookshelf()));
        }

        // add more articulate BookshelfMask from this bookshelf
        Bookshelf bookshelf = new MockBookshelf(new int[][]{
                { 0, 0, 0, 0, 0 },
                { 1, 1, 1, 1, 1 },
                { 2, 2, 2, 2, 2 },
                { 3, 3, 3, 3, 3 },
                { 4, 4, 4, 4, 4 },
                { 5, 5, 5, 5, 5 },
        });
        bookshelfMaskSet.addBookshelfMask(new BookshelfMask(bookshelf));

        Assertions.assertEquals(n + 1, bookshelfMaskSet.getSize());

        bookshelfMaskSet.clearSet();
        Assertions.assertEquals(0, bookshelfMaskSet.getSize());
    }
}
