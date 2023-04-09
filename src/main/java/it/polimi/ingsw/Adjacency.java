package it.polimi.ingsw;

import it.polimi.ingsw.model.tile.Tile;
import it.polimi.ingsw.model.tile.TileColor;
import it.polimi.ingsw.model.bookshelf.Bookshelf;
import it.polimi.ingsw.model.evaluator.AdjacencyEvaluator;
import it.polimi.ingsw.model.evaluator.Evaluator;
import it.polimi.ingsw.model.fetcher.AdjacencyFetcher;
import it.polimi.ingsw.model.fetcher.Fetcher;
import it.polimi.ingsw.model.filter.Filter;
import it.polimi.ingsw.model.filter.NumDifferentColorFilter;
import it.polimi.ingsw.model.tile.TileVersion;

import java.util.List;

public class Adjacency {
    public static void main(String[] args) {
        Bookshelf bookshelf = new Bookshelf();

        bookshelf.insertTiles(List.of(
            Tile.getInstance(TileColor.GREEN, TileVersion.FIRST),
            Tile.getInstance(TileColor.GREEN, TileVersion.FIRST),
            Tile.getInstance(TileColor.GREEN, TileVersion.FIRST)
        ), 0);

        bookshelf.insertTiles(List.of(
            Tile.getInstance(TileColor.GREEN, TileVersion.FIRST),
            Tile.getInstance(TileColor.BLUE, TileVersion.FIRST)
        ), 0);

        bookshelf.insertTiles(List.of(
            Tile.getInstance(TileColor.GREEN, TileVersion.FIRST),
            Tile.getInstance(TileColor.GREEN, TileVersion.FIRST),
            Tile.getInstance(TileColor.BLUE, TileVersion.FIRST)
        ), 1);

        bookshelf.insertTiles(List.of(
            Tile.getInstance(TileColor.BLUE, TileVersion.FIRST),
            Tile.getInstance(TileColor.BLUE, TileVersion.FIRST),
            Tile.getInstance(TileColor.YELLOW, TileVersion.FIRST)
        ), 1);

        bookshelf.insertTiles(List.of(
            Tile.getInstance(TileColor.BLUE, TileVersion.FIRST),
            Tile.getInstance(TileColor.BLUE, TileVersion.FIRST),
            Tile.getInstance(TileColor.YELLOW, TileVersion.FIRST)
        ), 2);

        bookshelf.insertTiles(List.of(
            Tile.getInstance(TileColor.YELLOW, TileVersion.FIRST),
            Tile.getInstance(TileColor.YELLOW, TileVersion.FIRST),
            Tile.getInstance(TileColor.YELLOW, TileVersion.FIRST)
        ), 2);

        bookshelf.insertTiles(List.of(
            Tile.getInstance(TileColor.WHITE, TileVersion.FIRST),
            Tile.getInstance(TileColor.WHITE, TileVersion.FIRST),
            Tile.getInstance(TileColor.WHITE, TileVersion.FIRST)
        ), 3);

        bookshelf.insertTiles(List.of(
            Tile.getInstance(TileColor.WHITE, TileVersion.FIRST),
            Tile.getInstance(TileColor.CYAN, TileVersion.FIRST)
        ), 3);

        bookshelf.insertTiles(List.of(
            Tile.getInstance(TileColor.MAGENTA, TileVersion.FIRST),
            Tile.getInstance(TileColor.MAGENTA, TileVersion.FIRST),
            Tile.getInstance(TileColor.CYAN, TileVersion.FIRST)
        ), 4);

        bookshelf.insertTiles(List.of(
            Tile.getInstance(TileColor.CYAN, TileVersion.FIRST),
            Tile.getInstance(TileColor.CYAN, TileVersion.FIRST)
        ), 4);

        Fetcher fetcher = new AdjacencyFetcher();

        Filter filter = new NumDifferentColorFilter(1, 1);

        Evaluator evaluator = new AdjacencyEvaluator();

        CalculatePoints.calculatePointsWithEvaluator(bookshelf, fetcher, filter, evaluator);
    }
}
