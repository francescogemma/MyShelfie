package it.polimi.ingsw;

import it.polimi.ingsw.model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class SearchGroupsInALibraryTest {
    private Library library;
    private Fetcher fetcher;
    private Filter filter;
    private int numGroups;

    @BeforeEach
    public void setUp() {
        library = new Library();
        fetcher = new AdjacencyFetcher();
        filter = new NumDifferentColorFilter(1, 1);
        numGroups = 0;
    }

    @Test
    @DisplayName("One single group of 30 shelves with one color")
    public void findGroups_fullLibraryOneColor_correctOutput() {
        int count = 0;

        library.insertTiles(new ArrayList<>(Arrays.asList(Tile.CYAN, Tile.CYAN, Tile.CYAN)), 0);
        library.insertTiles(new ArrayList<>(Arrays.asList(Tile.CYAN, Tile.CYAN, Tile.CYAN)), 1);
        library.insertTiles(new ArrayList<>(Arrays.asList(Tile.CYAN, Tile.CYAN, Tile.CYAN)), 2);
        library.insertTiles(new ArrayList<>(Arrays.asList(Tile.CYAN, Tile.CYAN, Tile.CYAN)), 3);
        library.insertTiles(new ArrayList<>(Arrays.asList(Tile.CYAN, Tile.CYAN, Tile.CYAN)), 4);
        library.insertTiles(new ArrayList<>(Arrays.asList(Tile.CYAN, Tile.CYAN, Tile.CYAN)), 0);
        library.insertTiles(new ArrayList<>(Arrays.asList(Tile.CYAN, Tile.CYAN, Tile.CYAN)), 1);
        library.insertTiles(new ArrayList<>(Arrays.asList(Tile.CYAN, Tile.CYAN, Tile.CYAN)), 2);
        library.insertTiles(new ArrayList<>(Arrays.asList(Tile.CYAN, Tile.CYAN, Tile.CYAN)), 3);
        library.insertTiles(new ArrayList<>(Arrays.asList(Tile.CYAN, Tile.CYAN, Tile.CYAN)), 4);

        LibraryMask mask = new LibraryMask(library);

        do {
            Shelf next = fetcher.next();
            if (filter.add(library.get(next))) {
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

                    for (int row = 0; row < Library.ROWS; row++) {
                        for (int column = 0; column < Library.COLUMNS; column++) {
                            Shelf currentShelf = Shelf.getInstance(row, column);
                            String toColor = "#";

                            result.append("[").append(library.get(currentShelf).color(toColor)).append("]");
                        }

                        result.append("\n---------------\n");
                    }

                    Assertions.assertEquals(mask.toString(), result.toString());
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
    public void findGroups_fullLibraryConcentricRectanglesGroups_correctOutput() {
        library.insertTiles(new ArrayList<>(Arrays.asList(Tile.CYAN, Tile.CYAN, Tile.CYAN)), 0);
        library.insertTiles(new ArrayList<>(Arrays.asList(Tile.CYAN, Tile.WHITE, Tile.WHITE)), 1);
        library.insertTiles(new ArrayList<>(Arrays.asList(Tile.CYAN, Tile.WHITE, Tile.BLUE)), 2);
        library.insertTiles(new ArrayList<>(Arrays.asList(Tile.CYAN, Tile.WHITE, Tile.WHITE)), 3);
        library.insertTiles(new ArrayList<>(Arrays.asList(Tile.CYAN, Tile.CYAN, Tile.CYAN)), 4);
        library.insertTiles(new ArrayList<>(Arrays.asList(Tile.CYAN, Tile.CYAN, Tile.CYAN)), 0);
        library.insertTiles(new ArrayList<>(Arrays.asList(Tile.WHITE, Tile.WHITE, Tile.CYAN)), 1);
        library.insertTiles(new ArrayList<>(Arrays.asList(Tile.BLUE, Tile.WHITE, Tile.CYAN)), 2);
        library.insertTiles(new ArrayList<>(Arrays.asList(Tile.WHITE, Tile.WHITE, Tile.CYAN)), 3);
        library.insertTiles(new ArrayList<>(Arrays.asList(Tile.CYAN, Tile.CYAN, Tile.CYAN)), 4);

        LibraryMask mask = new LibraryMask(library);

        do {
            Shelf next = fetcher.next();
            if (filter.add(library.get(next))) {
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

                    for (int row = 0; row < Library.ROWS; row++) {
                        for (int column = 0; column < Library.COLUMNS; column++) {
                            Shelf currentShelf = Shelf.getInstance(row, column);
                            String toColor = " ";
                            if(numGroups == 1 && (row == 0 || row == 5 || column == 0 || column == 4)) {
                                toColor = "#";
                            } else if(numGroups == 2 && ((row == 1 && column != 0 && column != 4) || (row == 4 && column != 0 && column != 4) || (column == 1 && row != 0 && row != 5) || (column == 3 && row != 0 && row != 5))) {
                                toColor = "#";
                            } else if(numGroups == 3 && column == 2 && (row == 2 || row == 3)) {
                                toColor = "#";
                            }

                            result.append("[").append(library.get(currentShelf).color(toColor)).append("]");
                        }

                        result.append("\n---------------\n");
                    }

                    Assertions.assertEquals(mask.toString(), result.toString());
                }
                filter.clear();
                mask.clear();
            }
        } while (!fetcher.hasFinished());

        Assertions.assertEquals(3, numGroups);
    }

    @Test
    @DisplayName("Normal library")
    public void findGroups_normalLibrary_correctOutput() {
        library.insertTiles(new ArrayList<>(Arrays.asList(Tile.CYAN, Tile.YELLOW, Tile.BLUE)), 0);
        library.insertTiles(new ArrayList<>(Arrays.asList(Tile.YELLOW, Tile.YELLOW, Tile.BLUE)), 1);
        library.insertTiles(new ArrayList<>(Arrays.asList(Tile.CYAN, Tile.YELLOW, Tile.YELLOW)), 2);
        library.insertTiles(new ArrayList<>(Arrays.asList(Tile.YELLOW, Tile.YELLOW, Tile.BLUE)), 3);
        library.insertTiles(new ArrayList<>(Arrays.asList(Tile.CYAN, Tile.YELLOW, Tile.BLUE)), 4);
        library.insertTiles(new ArrayList<>(Collections.nCopies(3, Tile.GREEN)), 0);
        library.insertTiles(new ArrayList<>(Collections.nCopies(2, Tile.GREEN)), 1);

        LibraryMask mask = new LibraryMask(library);

        do {
            Shelf next = fetcher.next();
            if (filter.add(library.get(next))) {
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

        Assertions.assertEquals(8, numGroups);
    }

    @Test
    @DisplayName("30 groups of one shelf")
    public void findGroups_fullLibraryAllGroupsOfOneShelf_correctOutput() {
        library.insertTiles(new ArrayList<>(Arrays.asList(Tile.MAGENTA, Tile.CYAN, Tile.MAGENTA)), 0);
        library.insertTiles(new ArrayList<>(Arrays.asList(Tile.GREEN, Tile.WHITE, Tile.GREEN)), 1);
        library.insertTiles(new ArrayList<>(Arrays.asList(Tile.MAGENTA, Tile.CYAN, Tile.MAGENTA)), 2);
        library.insertTiles(new ArrayList<>(Arrays.asList(Tile.GREEN, Tile.WHITE, Tile.GREEN)), 3);
        library.insertTiles(new ArrayList<>(Arrays.asList(Tile.MAGENTA, Tile.BLUE, Tile.MAGENTA)), 4);
        library.insertTiles(new ArrayList<>(Arrays.asList(Tile.CYAN, Tile.MAGENTA, Tile.CYAN)), 0);
        library.insertTiles(new ArrayList<>(Arrays.asList(Tile.WHITE, Tile.GREEN, Tile.WHITE)), 1);
        library.insertTiles(new ArrayList<>(Arrays.asList(Tile.CYAN, Tile.MAGENTA, Tile.CYAN)), 2);
        library.insertTiles(new ArrayList<>(Arrays.asList(Tile.WHITE, Tile.GREEN, Tile.WHITE)), 3);
        library.insertTiles(new ArrayList<>(Arrays.asList(Tile.BLUE, Tile.MAGENTA, Tile.BLUE)), 4);

        LibraryMask mask = new LibraryMask(library);

        do {
            Shelf next = fetcher.next();
            if (filter.add(library.get(next))) {
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
                    for (int row = 0; row < Library.ROWS; row++) {
                        for (int column = 0; column < Library.COLUMNS; column++) {
                            Shelf currentShelf = Shelf.getInstance(row, column);
                            String toColor = " ";
                            if(numGroups == count){
                                toColor = "#";
                            }

                            result.append("[").append(library.get(currentShelf).color(toColor)).append("]");
                            count++;
                        }

                        result.append("\n---------------\n");
                    }

                    Assertions.assertEquals(mask.toString(), result.toString());
                }
                filter.clear();
                mask.clear();
            }
        } while (!fetcher.hasFinished());

        Assertions.assertEquals(30, numGroups);
    }
}
