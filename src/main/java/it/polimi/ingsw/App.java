package it.polimi.ingsw;

import it.polimi.ingsw.model.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class App {
    private static void calculatePoints(Library library, Fetcher fetcher, Filter filter) {
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

    public static void main( String[] args ) {
        Library library = new Library();
        library.insertTiles(new ArrayList<>(Arrays.asList(Tile.CYAN, Tile.YELLOW, Tile.BLUE)), 0);
        library.insertTiles(new ArrayList<>(Arrays.asList(Tile.YELLOW, Tile.YELLOW, Tile.BLUE)), 1);
        library.insertTiles(new ArrayList<>(Arrays.asList(Tile.CYAN, Tile.YELLOW, Tile.YELLOW)), 2);
        library.insertTiles(new ArrayList<>(Arrays.asList(Tile.YELLOW, Tile.YELLOW, Tile.BLUE)), 3);
        library.insertTiles(new ArrayList<>(Arrays.asList(Tile.CYAN, Tile.YELLOW, Tile.BLUE)), 4);
        library.insertTiles(new ArrayList<>(Collections.nCopies(3, Tile.GREEN)), 0);
        library.insertTiles(new ArrayList<>(Collections.nCopies(2, Tile.GREEN)), 1);

        Shape fork = new Shape(new ArrayList<>(Arrays.asList(Offset.getInstance(0, 1),
            Offset.getInstance(1, 0), Offset.getInstance(1, 1), Offset.getInstance(1, 2),
            Offset.getInstance(2, 0), Offset.getInstance(2, 2))));

        Fetcher fetcher = new /*ShapeFetcher(fork)*/ AdjacencyFetcher();
        Filter filter = new NumDifferentColorFilter(1, 1);

        calculatePoints(library, fetcher, filter);

        /* ArrayList<Fetcher> dominoFetchers = new ArrayList<>();
        for (Shape domino : Shape.DOMINOES) {
            dominoFetchers.add(new ShapeFetcher(domino));
        }
        UnionFetcher dominoesFetcher = new UnionFetcher(dominoFetchers);

        calculatePoints(library, dominoesFetcher, filter);

        ArrayList<Fetcher> tetrominoFetchers = new ArrayList<>();
        for (Shape tetromino : Shape.TETROMINOES) {
            tetrominoFetchers.add(new ShapeFetcher(tetromino));
        }
        UnionFetcher tetrominoesFetcher = new UnionFetcher(tetrominoFetchers);

        calculatePoints(library, tetrominoesFetcher, filter);

        Shape l = new Shape(new ArrayList<>(Arrays.asList(
            Offset.getInstance(0, 0),
            Offset.getInstance(1, 0),
            Offset.getInstance(2, 0),
            Offset.getInstance(3, 0), Offset.getInstance(3, 1), Offset.getInstance(3, 2)
        )));

        System.out.println(l.verticalFlip());
        System.out.println(l); */
    }
}
