package it.polimi.ingsw;

import it.polimi.ingsw.model.Tile;
import it.polimi.ingsw.model.bookshelf.Bookshelf;
import it.polimi.ingsw.model.evaluator.AdjacencyEvaluator;
import it.polimi.ingsw.model.evaluator.Evaluator;
import it.polimi.ingsw.model.fetcher.AdjacencyFetcher;
import it.polimi.ingsw.model.fetcher.Fetcher;
import it.polimi.ingsw.model.filter.Filter;
import it.polimi.ingsw.model.filter.NumDifferentColorFilter;

import java.util.ArrayList;
import java.util.Arrays;

public class Adjacency {
    public static void main(String[] args) {
        Bookshelf bookshelf = new Bookshelf();

        bookshelf.insertTiles(new ArrayList<>(Arrays.asList(
            Tile.GREEN, Tile.GREEN, Tile.GREEN
        )), 0);

        bookshelf.insertTiles(new ArrayList<>(Arrays.asList(
            Tile.GREEN, Tile.BLUE
        )), 0);

        bookshelf.insertTiles(new ArrayList<>(Arrays.asList(
            Tile.GREEN, Tile.GREEN, Tile.BLUE
        )), 1);

        bookshelf.insertTiles(new ArrayList<>(Arrays.asList(
            Tile.BLUE, Tile.BLUE, Tile.YELLOW
        )), 1);

        bookshelf.insertTiles(new ArrayList<>(Arrays.asList(
            Tile.BLUE, Tile.BLUE, Tile.YELLOW
        )), 2);

        bookshelf.insertTiles(new ArrayList<>(Arrays.asList(
            Tile.YELLOW, Tile.YELLOW, Tile.YELLOW
        )), 2);

        bookshelf.insertTiles(new ArrayList<>(Arrays.asList(
            Tile.WHITE, Tile.WHITE, Tile.WHITE
        )), 3);

        bookshelf.insertTiles(new ArrayList<>(Arrays.asList(
            Tile.WHITE, Tile.CYAN
        )), 3);

        bookshelf.insertTiles(new ArrayList<>(Arrays.asList(
            Tile.MAGENTA, Tile.MAGENTA, Tile.CYAN
        )), 4);

        bookshelf.insertTiles(new ArrayList<>(Arrays.asList(
            Tile.CYAN, Tile.CYAN
        )), 4);

        Fetcher fetcher = new AdjacencyFetcher();

        Filter filter = new NumDifferentColorFilter(1, 1);

        Evaluator evaluator = new AdjacencyEvaluator();

        CalculatePoints.calculatePointsWithEvaluator(bookshelf, fetcher, filter, evaluator);
    }
}
