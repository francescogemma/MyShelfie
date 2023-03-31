package it.polimi.ingsw.model.goal;

import it.polimi.ingsw.model.Tile;
import it.polimi.ingsw.model.bookshelf.BookshelfMask;
import it.polimi.ingsw.model.bookshelf.Offset;
import it.polimi.ingsw.model.bookshelf.Shape;
import it.polimi.ingsw.model.bookshelf.Shelf;
import it.polimi.ingsw.model.evaluator.AtLeastEvaluator;
import it.polimi.ingsw.model.fetcher.ShapeFetcher;
import it.polimi.ingsw.model.fetcher.UnionFetcher;
import it.polimi.ingsw.model.filter.NumDifferentColorFilter;

import java.awt.print.Book;
import java.util.*;
import java.util.function.Predicate;

/**
 * @author Giacomo Groppi
 * */

public class StairGoal extends CommonGoal{
    private static final List<Map<Shelf, Tile>> DATA_STAIR = new ArrayList<>();
    private static final Shape SHAPE_STAIR;

    private static boolean isSatisfied (Map<Shelf, Tile> first, Map<Shelf, Tile> second, BookshelfMask bookshelfMask) {
        long sat;

        Predicate<Map.Entry<Shelf, Tile>> match = (object) -> bookshelfMask.tileAt(object.getKey()) != object.getValue();

        sat = first
                .entrySet()
                .stream()
                .filter(match).count();

        if (sat == 0)
            return true;

        if (sat != first.size())
            return false;

        return second
                .entrySet()
                .stream().noneMatch(match);
    }

    private static final Predicate<BookshelfMask> check = (BookshelfMask bookshelfMask) -> {
        if (isSatisfied(DATA_STAIR.get(1), DATA_STAIR.get(0), bookshelfMask))
            return true;
        return isSatisfied(DATA_STAIR.get(3), DATA_STAIR.get(2), bookshelfMask);
    };

    static {
        for (int i = 0; i < 4; i++)
            StairGoal.DATA_STAIR.add(new HashMap<>());

        for (int i = 0; i < 4; i++)
            StairGoal.DATA_STAIR.get(0).put(
                            Shelf.getInstance(i, i+1),
                            Tile.EMPTY
                    );

        for (int i = 0; i < 5; i++)
            StairGoal.DATA_STAIR.get(1).put(
                    Shelf.getInstance(i, i),
                    Tile.EMPTY
            );

        for (int i = 0; i < 4; i++)
            StairGoal.DATA_STAIR.get(2).put(
                    Shelf.getInstance(i, 3 - i),
                    Tile.EMPTY
            );

        for (int i = 0; i < 5; i++)
            StairGoal.DATA_STAIR.get(3).put(
                    Shelf.getInstance(i, 4 - i),
                    Tile.EMPTY
            );
    }

    static {
        ArrayList<Offset> res = new ArrayList<>();
        for (int i = 0; i <= 4; i++) {
            for (int j = 0; j <= i; j++) {
                res.add(Offset.getInstance(i, j));
            }
        }

        SHAPE_STAIR = new Shape(res);
    }

    public StairGoal(int numPlayer) {
        super(new UnionFetcher(new ArrayList<>(
                        List.of(
                                new ShapeFetcher(SHAPE_STAIR),
                                new ShapeFetcher(SHAPE_STAIR.verticalFlip())
                        )
                )),
                new NumDifferentColorFilter(1, 6),
                new AtLeastEvaluator(numPlayer, 1, check));
    }
}