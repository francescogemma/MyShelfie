package it.polimi.ingsw.model.fetcher;

import it.polimi.ingsw.model.Tile;
import it.polimi.ingsw.model.bookshelf.Bookshelf;
import it.polimi.ingsw.model.bookshelf.BookshelfMask;
import it.polimi.ingsw.model.bookshelf.Shelf;
import it.polimi.ingsw.model.fetcher.AdjacencyFetcher;
import it.polimi.ingsw.model.fetcher.Fetcher;
import it.polimi.ingsw.model.filter.Filter;
import it.polimi.ingsw.model.filter.NumDifferentColorFilter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class SearchGroupsInABookshelfTest {
    private Bookshelf bookshelf;
    private Fetcher fetcher;
    private Filter filter;
    private int numGroups;

    @BeforeEach
    public void setUp() {
        bookshelf = new Bookshelf();
        fetcher = new AdjacencyFetcher();
        filter = new NumDifferentColorFilter(1, 1);
        numGroups = 0;
    }

    @Test
    @DisplayName("One single group of 30 shelves with one color")
    void findGroups_fullBookshelfOneColor_correctOutput() {
        int count = 0;

        bookshelf.insertTiles(new ArrayList<>(Arrays.asList(Tile.CYAN, Tile.CYAN, Tile.CYAN)), 0);
        bookshelf.insertTiles(new ArrayList<>(Arrays.asList(Tile.CYAN, Tile.CYAN, Tile.CYAN)), 1);
        bookshelf.insertTiles(new ArrayList<>(Arrays.asList(Tile.CYAN, Tile.CYAN, Tile.CYAN)), 2);
        bookshelf.insertTiles(new ArrayList<>(Arrays.asList(Tile.CYAN, Tile.CYAN, Tile.CYAN)), 3);
        bookshelf.insertTiles(new ArrayList<>(Arrays.asList(Tile.CYAN, Tile.CYAN, Tile.CYAN)), 4);
        bookshelf.insertTiles(new ArrayList<>(Arrays.asList(Tile.CYAN, Tile.CYAN, Tile.CYAN)), 0);
        bookshelf.insertTiles(new ArrayList<>(Arrays.asList(Tile.CYAN, Tile.CYAN, Tile.CYAN)), 1);
        bookshelf.insertTiles(new ArrayList<>(Arrays.asList(Tile.CYAN, Tile.CYAN, Tile.CYAN)), 2);
        bookshelf.insertTiles(new ArrayList<>(Arrays.asList(Tile.CYAN, Tile.CYAN, Tile.CYAN)), 3);
        bookshelf.insertTiles(new ArrayList<>(Arrays.asList(Tile.CYAN, Tile.CYAN, Tile.CYAN)), 4);

        BookshelfMask mask = new BookshelfMask(bookshelf);

        do {
            Shelf next = fetcher.next();
            if (filter.add(bookshelf.get(next))) {
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

                    StringBuilder result = new StringBuilder("---------------\n");

                    for (int row = 0; row < Bookshelf.ROWS; row++) {
                        for (int column = 0; column < Bookshelf.COLUMNS; column++) {
                            Shelf currentShelf = Shelf.getInstance(row, column);
                            String toColor = "#";

                            result.append("[").append(bookshelf.get(currentShelf).color(toColor)).append("]");
                        }

                        result.append("\n---------------\n");
                    }

                    Assertions.assertEquals(result.toString(), mask.toString());
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
        bookshelf.insertTiles(new ArrayList<>(Arrays.asList(Tile.CYAN, Tile.CYAN, Tile.CYAN)), 0);
        bookshelf.insertTiles(new ArrayList<>(Arrays.asList(Tile.CYAN, Tile.WHITE, Tile.WHITE)), 1);
        bookshelf.insertTiles(new ArrayList<>(Arrays.asList(Tile.CYAN, Tile.WHITE, Tile.BLUE)), 2);
        bookshelf.insertTiles(new ArrayList<>(Arrays.asList(Tile.CYAN, Tile.WHITE, Tile.WHITE)), 3);
        bookshelf.insertTiles(new ArrayList<>(Arrays.asList(Tile.CYAN, Tile.CYAN, Tile.CYAN)), 4);
        bookshelf.insertTiles(new ArrayList<>(Arrays.asList(Tile.CYAN, Tile.CYAN, Tile.CYAN)), 0);
        bookshelf.insertTiles(new ArrayList<>(Arrays.asList(Tile.WHITE, Tile.WHITE, Tile.CYAN)), 1);
        bookshelf.insertTiles(new ArrayList<>(Arrays.asList(Tile.BLUE, Tile.WHITE, Tile.CYAN)), 2);
        bookshelf.insertTiles(new ArrayList<>(Arrays.asList(Tile.WHITE, Tile.WHITE, Tile.CYAN)), 3);
        bookshelf.insertTiles(new ArrayList<>(Arrays.asList(Tile.CYAN, Tile.CYAN, Tile.CYAN)), 4);

        BookshelfMask mask = new BookshelfMask(bookshelf);

        do {
            Shelf next = fetcher.next();
            if (filter.add(bookshelf.get(next))) {
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

                    StringBuilder result = new StringBuilder("---------------\n");

                    for (int row = 0; row < Bookshelf.ROWS; row++) {
                        for (int column = 0; column < Bookshelf.COLUMNS; column++) {
                            Shelf currentShelf = Shelf.getInstance(row, column);
                            String toColor = " ";
                            if(numGroups == 1 && (row == 0 || row == 5 || column == 0 || column == 4)) {
                                toColor = "#";
                            } else if(numGroups == 2 && ((row == 1 && column != 0 && column != 4) || (row == 4 && column != 0 && column != 4) || (column == 1 && row != 0 && row != 5) || (column == 3 && row != 0 && row != 5))) {
                                toColor = "#";
                            } else if(numGroups == 3 && column == 2 && (row == 2 || row == 3)) {
                                toColor = "#";
                            }

                            result.append("[").append(bookshelf.get(currentShelf).color(toColor)).append("]");
                        }

                        result.append("\n---------------\n");
                    }

                    Assertions.assertEquals(result.toString(), mask.toString());
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
        bookshelf.insertTiles(new ArrayList<>(Arrays.asList(Tile.CYAN, Tile.YELLOW, Tile.BLUE)), 0);
        bookshelf.insertTiles(new ArrayList<>(Arrays.asList(Tile.YELLOW, Tile.YELLOW, Tile.BLUE)), 1);
        bookshelf.insertTiles(new ArrayList<>(Arrays.asList(Tile.CYAN, Tile.YELLOW, Tile.YELLOW)), 2);
        bookshelf.insertTiles(new ArrayList<>(Arrays.asList(Tile.YELLOW, Tile.YELLOW, Tile.BLUE)), 3);
        bookshelf.insertTiles(new ArrayList<>(Arrays.asList(Tile.CYAN, Tile.YELLOW, Tile.BLUE)), 4);
        bookshelf.insertTiles(new ArrayList<>(Collections.nCopies(3, Tile.GREEN)), 0);
        bookshelf.insertTiles(new ArrayList<>(Collections.nCopies(2, Tile.GREEN)), 1);

        BookshelfMask mask = new BookshelfMask(bookshelf);

        do {
            Shelf next = fetcher.next();
            if (filter.add(bookshelf.get(next))) {
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

        Assertions.assertEquals(7, numGroups);
    }

    @ParameterizedTest(name = "in column {0}")
    @DisplayName("Bookshelf with only one tile")
    @ValueSource(ints = {0, 1, 2, 3, 4})
    void findGroups_oneTileBookshelf_correctOutput(int col) {
        bookshelf.insertTiles(new ArrayList<>(List.of(Tile.BLUE)), col);

        BookshelfMask mask = new BookshelfMask(bookshelf);

        do {
            Shelf next = fetcher.next();
            if (filter.add(bookshelf.get(next))) {
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

                    StringBuilder result = new StringBuilder("---------------\n");

                    for (int row = 0; row < Bookshelf.ROWS; row++) {
                        for (int column = 0; column < Bookshelf.COLUMNS; column++) {
                            Shelf currentShelf = Shelf.getInstance(row, column);
                            String toColor = " ";
                            if(numGroups == 1 && row == 5 && column == col) {
                                toColor = "#";
                            }

                            result.append("[").append(bookshelf.get(currentShelf).color(toColor)).append("]");
                        }

                        result.append("\n---------------\n");
                    }

                    Assertions.assertEquals(result.toString(), mask.toString());
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
        BookshelfMask mask = new BookshelfMask(bookshelf);

        do {
            Shelf next = fetcher.next();
            if (filter.add(bookshelf.get(next))) {
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

                    StringBuilder result = new StringBuilder("---------------\n");

                    for (int row = 0; row < Bookshelf.ROWS; row++) {
                        for (int column = 0; column < Bookshelf.COLUMNS; column++) {
                            Shelf currentShelf = Shelf.getInstance(row, column);
                            String toColor = " ";

                            result.append("[").append(bookshelf.get(currentShelf).color(toColor)).append("]");
                        }

                        result.append("\n---------------\n");
                    }

                    Assertions.assertEquals(result.toString(), mask.toString());
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
        bookshelf.insertTiles(new ArrayList<>(Arrays.asList(Tile.MAGENTA, Tile.CYAN, Tile.MAGENTA)), 0);
        bookshelf.insertTiles(new ArrayList<>(Arrays.asList(Tile.GREEN, Tile.WHITE, Tile.GREEN)), 1);
        bookshelf.insertTiles(new ArrayList<>(Arrays.asList(Tile.MAGENTA, Tile.CYAN, Tile.MAGENTA)), 2);
        bookshelf.insertTiles(new ArrayList<>(Arrays.asList(Tile.GREEN, Tile.WHITE, Tile.GREEN)), 3);
        bookshelf.insertTiles(new ArrayList<>(Arrays.asList(Tile.MAGENTA, Tile.BLUE, Tile.MAGENTA)), 4);
        bookshelf.insertTiles(new ArrayList<>(Arrays.asList(Tile.CYAN, Tile.MAGENTA, Tile.CYAN)), 0);
        bookshelf.insertTiles(new ArrayList<>(Arrays.asList(Tile.WHITE, Tile.GREEN, Tile.WHITE)), 1);
        bookshelf.insertTiles(new ArrayList<>(Arrays.asList(Tile.CYAN, Tile.MAGENTA, Tile.CYAN)), 2);
        bookshelf.insertTiles(new ArrayList<>(Arrays.asList(Tile.WHITE, Tile.GREEN, Tile.WHITE)), 3);
        bookshelf.insertTiles(new ArrayList<>(Arrays.asList(Tile.BLUE, Tile.MAGENTA, Tile.BLUE)), 4);

        BookshelfMask mask = new BookshelfMask(bookshelf);

        do {
            Shelf next = fetcher.next();
            if (filter.add(bookshelf.get(next))) {
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

                    StringBuilder result = new StringBuilder("---------------\n");

                    int count = 1;
                    for (int row = 0; row < Bookshelf.ROWS; row++) {
                        for (int column = 0; column < Bookshelf.COLUMNS; column++) {
                            Shelf currentShelf = Shelf.getInstance(row, column);
                            String toColor = " ";
                            if(numGroups == count){
                                toColor = "#";
                            }

                            result.append("[").append(bookshelf.get(currentShelf).color(toColor)).append("]");
                            count++;
                        }

                        result.append("\n---------------\n");
                    }

                    Assertions.assertEquals(result.toString(), mask.toString());
                }
                filter.clear();
                mask.clear();
            }
        } while (!fetcher.hasFinished());

        Assertions.assertEquals(30, numGroups);
    }
}
