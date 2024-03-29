package it.polimi.ingsw.model.fetcher;

import it.polimi.ingsw.model.tile.Tile;
import it.polimi.ingsw.model.tile.TileColor;
import it.polimi.ingsw.model.bookshelf.*;
import it.polimi.ingsw.model.filter.Filter;
import it.polimi.ingsw.model.filter.NumDifferentColorFilter;
import it.polimi.ingsw.model.tile.TileVersion;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

class SearchGroupsInABookshelfTest {
    private Bookshelf bookshelf;
    private Fetcher fetcher;
    private Filter filter;
    private int numGroups;

    @BeforeEach
    public void setUp() {
        fetcher = new AdjacencyFetcher();
        filter = new NumDifferentColorFilter(1, 1);
        numGroups = 0;
    }

    @Test
    @DisplayName("One single group of 30 shelves with one color")
    void findGroups_fullBookshelfOneColor_correctOutput() {
        int count = 0;

        bookshelf = new MockBookshelf(new int[][]{
                { 1, 1, 1, 1, 1 },
                { 1, 1, 1, 1, 1 },
                { 1, 1, 1, 1, 1 },
                { 1, 1, 1, 1, 1 },
                { 1, 1, 1, 1, 1 },
                { 1, 1, 1, 1, 1 }
        });

        BookshelfMask mask = new BookshelfMask(bookshelf);

        do {
            Shelf next = fetcher.next();
            if (filter.add(bookshelf.getTileColorAt(next))) {
                count = 0;
                if (fetcher.canFix()) {
                    filter.forgetLastTile();
                } else {
                    filter.clear();
                    mask.clear();
                    continue;
                }
            } else {
                mask.add(next);
                count++;
            }

            if (fetcher.lastShelf()) {
                if (filter.isSatisfied()) {
                    numGroups++;

                    BookshelfMask exactMask = new MockBookshelfMask(bookshelf, new int[][]{
                            { 1, 1, 1, 1, 1 },
                            { 1, 1, 1, 1, 1 },
                            { 1, 1, 1, 1, 1 },
                            { 1, 1, 1, 1, 1 },
                            { 1, 1, 1, 1, 1 },
                            { 1, 1, 1, 1, 1 }
                    });

                    Assertions.assertEquals(exactMask, mask);
                }
                filter.clear();
                mask.clear();
            }
        } while (!fetcher.hasFinished());

        Assertions.assertEquals(1, numGroups);
        Assertions.assertEquals(30, count);
    }

    @Test
    @DisplayName("3 concentric rectangles like groups")
    void findGroups_fullBookshelfConcentricRectanglesGroups_correctOutput() {
        bookshelf = new MockBookshelf(new int[][]{
                { 1, 1, 1, 1, 1 },
                { 1, 2, 2, 2, 1 },
                { 1, 2, 3, 2, 1 },
                { 1, 2, 3, 2, 1 },
                { 1, 2, 2, 2, 1 },
                { 1, 1, 1, 1, 1 }
        });

        BookshelfMask mask = new BookshelfMask(bookshelf);

        do {
            Shelf next = fetcher.next();
            if (filter.add(bookshelf.getTileColorAt(next))) {
                if (fetcher.canFix()) {
                    filter.forgetLastTile();
                } else {
                    filter.clear();
                    mask.clear();
                    continue;
                }
            } else mask.add(next);

            if (fetcher.lastShelf()) {
                if (filter.isSatisfied()) {
                    numGroups++;

                    BookshelfMask exactMask = new BookshelfMask(bookshelf);

                    for (int row = 0; row < Bookshelf.ROWS; row++) {
                        for (int column = 0; column < Bookshelf.COLUMNS; column++) {
                            if(numGroups == 1 && (row == 0 || row == 5 || column == 0 || column == 4)) {
                                exactMask.add(Shelf.getInstance(row, column));
                            } else if(numGroups == 2 && ((row == 1 && column != 0 && column != 4) || (row == 4 && column != 0 && column != 4) || (column == 1 && row != 0 && row != 5) || (column == 3 && row != 0 && row != 5))) {
                                exactMask.add(Shelf.getInstance(row, column));
                            } else if(numGroups == 3 && column == 2 && (row == 2 || row == 3)) {
                                exactMask.add(Shelf.getInstance(row, column));
                            }
                        }
                    }

                    Assertions.assertEquals(exactMask, mask);
                }
                filter.clear();
                mask.clear();
            }
        } while (!fetcher.hasFinished());

        Assertions.assertEquals(3, numGroups);
    }

    @Test
    @DisplayName("Normal bookshelf")
    void findGroups_normalBookshelf_correctOutput() {
        bookshelf = new MockBookshelf(new int[][]{
                { 3, 0, 0, 0, 0 },
                { 3, 1, 6, 0, 0 },
                { 3, 1, 6, 1, 0 },
                { 3, 3, 6, 6, 4 },
                { 3, 4, 1, 1, 5 },
                { 3, 3, 3, 3, 2 }
        });

        BookshelfMask mask = new BookshelfMask(bookshelf);

        do {
            Shelf next = fetcher.next();
            if (filter.add(bookshelf.getTileColorAt(next))) {
                if (fetcher.canFix()) {
                    filter.forgetLastTile();
                } else {
                    filter.clear();
                    mask.clear();
                    continue;
                }
            } else mask.add(next);

            if (fetcher.lastShelf()) {
                if (filter.isSatisfied()) {
                    numGroups++;
                }
                filter.clear();
                mask.clear();
            }
        } while (!fetcher.hasFinished());

        Assertions.assertEquals(9, numGroups);
    }

    @ParameterizedTest(name = "in column {0}")
    @DisplayName("Bookshelf with only one tile")
    @ValueSource(ints = {0, 1, 2, 3, 4})
    void findGroups_oneTileBookshelf_correctOutput(int col) {
        bookshelf = new MockBookshelf(new int[][]{
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
        });
        bookshelf.insertTiles(List.of(
            Tile.getInstance(TileColor.BLUE, TileVersion.FIRST)
        ), col);

        BookshelfMask mask = new BookshelfMask(bookshelf);

        do {
            Shelf next = fetcher.next();
            if (filter.add(bookshelf.getTileColorAt(next))) {
                if (fetcher.canFix()) {
                    filter.forgetLastTile();
                } else {
                    filter.clear();
                    mask.clear();
                    continue;
                }
            } else mask.add(next);

            if (fetcher.lastShelf()) {
                if (filter.isSatisfied()) {
                    numGroups++;

                    BookshelfMask exactMask = new BookshelfMask(bookshelf);
                    exactMask.add(Shelf.getInstance(5, col));

                    Assertions.assertEquals(exactMask, mask);
                }
                filter.clear();
                mask.clear();
            }
        } while (!fetcher.hasFinished());

        Assertions.assertEquals(1, numGroups);
    }

    @Test
    @DisplayName("Empty bookshelf")
    void findGroups_emptyBookshelf_correctOutput() {
        bookshelf = new MockBookshelf(new int[][]{
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
        });

        BookshelfMask mask = new BookshelfMask(bookshelf);

        do {
            Shelf next = fetcher.next();
            if (filter.add(bookshelf.getTileColorAt(next))) {
                if (fetcher.canFix()) {
                    filter.forgetLastTile();
                } else {
                    filter.clear();
                    mask.clear();
                    continue;
                }
            } else mask.add(next);

            if (fetcher.lastShelf()) {
                if (filter.isSatisfied()) {
                    numGroups++;

                    BookshelfMask exactMask = new BookshelfMask(bookshelf);
                    Assertions.assertEquals(exactMask, mask);
                }
                filter.clear();
                mask.clear();
            }
        } while (!fetcher.hasFinished());

        Assertions.assertEquals(0, numGroups);
    }

    @Test
    @DisplayName("30 groups of one shelf")
    void findGroups_fullBookshelfAllGroupsOfOneShelf_correctOutput() {
        bookshelf = new MockBookshelf(new int[][]{
                { 1, 2, 3, 4, 5 },
                { 5, 4, 6, 3, 1 },
                { 1, 2, 3, 4, 5 },
                { 5, 4, 6, 3, 1 },
                { 1, 2, 3, 4, 5 },
                { 5, 4, 6, 3, 1 },
        });

        BookshelfMask mask = new BookshelfMask(bookshelf);

        do {
            Shelf next = fetcher.next();
            if (filter.add(bookshelf.getTileColorAt(next))) {
                if (fetcher.canFix()) {
                    filter.forgetLastTile();
                } else {
                    filter.clear();
                    mask.clear();
                    continue;
                }
            } else {
                mask.add(next);
            }

            if (fetcher.lastShelf()) {
                if (filter.isSatisfied()) {
                    numGroups++;
                    Assertions.assertEquals(1, mask.getShelves().size());

                    BookshelfMask exactMask = new BookshelfMask(bookshelf);

                    int count = 1;
                    for (int row = 0; row < Bookshelf.ROWS; row++) {
                        for (int column = 0; column < Bookshelf.COLUMNS; column++) {
                            if(numGroups == count){
                                exactMask.add(Shelf.getInstance(row, column));
                            }
                            count++;
                        }
                    }

                    Assertions.assertEquals(exactMask, mask);
                }
                filter.clear();
                mask.clear();
            }
        } while (!fetcher.hasFinished());

        Assertions.assertEquals(30, numGroups);
    }
}
