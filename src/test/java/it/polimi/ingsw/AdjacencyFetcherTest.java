package it.polimi.ingsw;

import it.polimi.ingsw.model.*;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class AdjacencyFetcherTest {
    Library library = null;
    Fetcher fetcher = null;
    Filter filter = null;

    @Before
    public void setUp() {
        library = new Library();
        fetcher = new AdjacencyFetcher();
        filter = new NumDifferentColorFilter(1, 1);
    }

    @Test
    public void findGroups_fullLibraryOneColor_correctOutput() {
        System.out.println("findGroups_fullLibraryOneColor_correctOutput");

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
                    System.out.println(mask);
                }
                filter.clear();
                mask.clear();
            }
        } while (!fetcher.hasFinished());
    }

    @Test
    public void findGroups_fullLibraryCircularGroups_correctOutput() {
        System.out.println("findGroups_fullLibraryCircularGroups_correctOutput");

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
                    System.out.println(mask);
                }
                filter.clear();
                mask.clear();
            }
        } while (!fetcher.hasFinished());
    }

    @Test
    public void findGroups_normalLibrary_correctOutput() {
        System.out.println("findGroups_normalLibrary_correctOutput");

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
                    System.out.println(mask);
                }
                filter.clear();
                mask.clear();
            }
        } while (!fetcher.hasFinished());
    }

    @Test
    public void findGroups_fullLibraryAllGroupsOfOneShelf_correctOutput() {
        System.out.println("findGroups_fullLibraryAllGroupsOfOneShelf_correctOutput");

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
            } else mask.add(next);

            if (fetcher.lastShelf()) {
                if (filter.isSatisfied()) {
                    System.out.println(mask);
                }
                filter.clear();
                mask.clear();
            }
        } while (!fetcher.hasFinished());
    }
}
