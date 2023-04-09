package it.polimi.ingsw;

import it.polimi.ingsw.model.tile.Tile;
import it.polimi.ingsw.model.tile.TileColor;
import it.polimi.ingsw.model.bookshelf.Bookshelf;
import it.polimi.ingsw.model.bookshelf.Shape;
import it.polimi.ingsw.model.fetcher.Fetcher;
import it.polimi.ingsw.model.fetcher.ShapeFetcher;
import it.polimi.ingsw.model.fetcher.UnionFetcher;
import it.polimi.ingsw.model.filter.Filter;
import it.polimi.ingsw.model.filter.NumDifferentColorFilter;
import it.polimi.ingsw.model.tile.TileVersion;

import java.util.ArrayList;
import java.util.Arrays;

public class Tetrominoes {
    public static void main(String[] args) {
        Bookshelf bookshelf = new Bookshelf();

        bookshelf.insertTiles(new ArrayList<>(Arrays.asList(
            Tile.getInstance(TileColor.GREEN, TileVersion.FIRST),
            Tile.getInstance(TileColor.GREEN, TileVersion.FIRST),
            Tile.getInstance(TileColor.GREEN, TileVersion.FIRST)
        )), 0);

        bookshelf.insertTiles(new ArrayList<>(Arrays.asList(
            Tile.getInstance(TileColor.YELLOW, TileVersion.FIRST),
            Tile.getInstance(TileColor.BLUE, TileVersion.FIRST)
        )), 0);

        bookshelf.insertTiles(new ArrayList<>(Arrays.asList(
            Tile.getInstance(TileColor.GREEN, TileVersion.FIRST),
            Tile.getInstance(TileColor.GREEN, TileVersion.FIRST),
            Tile.getInstance(TileColor.BLUE, TileVersion.FIRST)
        )), 1);

        bookshelf.insertTiles(new ArrayList<>(Arrays.asList(
            Tile.getInstance(TileColor.YELLOW, TileVersion.FIRST),
            Tile.getInstance(TileColor.BLUE, TileVersion.FIRST),
            Tile.getInstance(TileColor.YELLOW, TileVersion.FIRST)
        )), 1);

        bookshelf.insertTiles(new ArrayList<>(Arrays.asList(
            Tile.getInstance(TileColor.BLUE, TileVersion.FIRST),
            Tile.getInstance(TileColor.GREEN, TileVersion.FIRST),
            Tile.getInstance(TileColor.YELLOW, TileVersion.FIRST)
        )), 2);

        bookshelf.insertTiles(new ArrayList<>(Arrays.asList(
            Tile.getInstance(TileColor.YELLOW, TileVersion.FIRST),
            Tile.getInstance(TileColor.YELLOW, TileVersion.FIRST),
            Tile.getInstance(TileColor.YELLOW, TileVersion.FIRST)
        )), 2);

        bookshelf.insertTiles(new ArrayList<>(Arrays.asList(
            Tile.getInstance(TileColor.WHITE, TileVersion.FIRST),
            Tile.getInstance(TileColor.GREEN, TileVersion.FIRST),
            Tile.getInstance(TileColor.WHITE, TileVersion.FIRST)
        )), 3);

        bookshelf.insertTiles(new ArrayList<>(Arrays.asList(
            Tile.getInstance(TileColor.YELLOW, TileVersion.FIRST),
            Tile.getInstance(TileColor.CYAN, TileVersion.FIRST)
        )), 3);

        bookshelf.insertTiles(new ArrayList<>(Arrays.asList(
            Tile.getInstance(TileColor.WHITE, TileVersion.FIRST),
            Tile.getInstance(TileColor.GREEN, TileVersion.FIRST),
            Tile.getInstance(TileColor.WHITE, TileVersion.FIRST)
        )), 4);

        bookshelf.insertTiles(new ArrayList<>(Arrays.asList(
            Tile.getInstance(TileColor.YELLOW, TileVersion.FIRST),
            Tile.getInstance(TileColor.CYAN, TileVersion.FIRST)
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
