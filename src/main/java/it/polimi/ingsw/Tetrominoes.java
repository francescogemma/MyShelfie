package it.polimi.ingsw;

import it.polimi.ingsw.model.Tile;
import it.polimi.ingsw.model.bookshelf.Bookshelf;
import it.polimi.ingsw.model.bookshelf.Shape;
import it.polimi.ingsw.model.fetcher.Fetcher;
import it.polimi.ingsw.model.fetcher.ShapeFetcher;
import it.polimi.ingsw.model.fetcher.UnionFetcher;
import it.polimi.ingsw.model.filter.Filter;
import it.polimi.ingsw.model.filter.NumDifferentColorFilter;

import java.util.ArrayList;
import java.util.Arrays;

public class Tetrominoes {
    public static void main(String[] args) {
        Bookshelf bookshelf = new Bookshelf();

        bookshelf.insertTiles(new ArrayList<>(Arrays.asList(
            Tile.GREEN, Tile.GREEN, Tile.GREEN
        )), 0);

        bookshelf.insertTiles(new ArrayList<>(Arrays.asList(
            Tile.YELLOW, Tile.BLUE
        )), 0);

        bookshelf.insertTiles(new ArrayList<>(Arrays.asList(
            Tile.GREEN, Tile.GREEN, Tile.BLUE
        )), 1);

        bookshelf.insertTiles(new ArrayList<>(Arrays.asList(
            Tile.YELLOW, Tile.BLUE, Tile.YELLOW
        )), 1);

        bookshelf.insertTiles(new ArrayList<>(Arrays.asList(
            Tile.BLUE, Tile.GREEN, Tile.YELLOW
        )), 2);

        bookshelf.insertTiles(new ArrayList<>(Arrays.asList(
            Tile.YELLOW, Tile.YELLOW, Tile.YELLOW
        )), 2);

        bookshelf.insertTiles(new ArrayList<>(Arrays.asList(
            Tile.WHITE, Tile.GREEN, Tile.WHITE
        )), 3);

        bookshelf.insertTiles(new ArrayList<>(Arrays.asList(
            Tile.YELLOW, Tile.CYAN
        )), 3);

        bookshelf.insertTiles(new ArrayList<>(Arrays.asList(
            Tile.WHITE, Tile.GREEN, Tile.WHITE
        )), 4);

        bookshelf.insertTiles(new ArrayList<>(Arrays.asList(
            Tile.YELLOW, Tile.CYAN
        )), 4);

        ArrayList<Fetcher> fetchers = new ArrayList<>();
        for (Shape tetromino : Shape.TETROMINOES) {
            fetchers.add(new ShapeFetcher(tetromino));
        }

        Fetcher fetcher = new UnionFetcher(fetchers);

        Filter filter = new NumDifferentColorFilter(1, 1);

        CalculatePoints.calculatePoints(bookshelf, fetcher, filter);
    }
}
