package it.polimi.ingsw.model.goal;

import it.polimi.ingsw.model.Tile;
import it.polimi.ingsw.model.bookshelf.BookshelfMask;
import it.polimi.ingsw.model.bookshelf.Offset;
import it.polimi.ingsw.model.bookshelf.Shape;
import it.polimi.ingsw.model.bookshelf.Shelf;
import it.polimi.ingsw.model.evaluator.AtLeastEvaluator;
import it.polimi.ingsw.model.evaluator.Evaluator;
import it.polimi.ingsw.model.fetcher.Fetcher;
import it.polimi.ingsw.model.fetcher.ShapeFetcher;
import it.polimi.ingsw.model.fetcher.UnionFetcher;
import it.polimi.ingsw.model.filter.Filter;
import it.polimi.ingsw.model.filter.NumDifferentColorFilter;

import java.util.*;
import java.util.function.Predicate;

/**
 * This class represents a common goal:
 * <p>
 *  The tiles inside the library must take the form of a staircase.
 * <p>
 * It extends CommonGoal.
 *
 *  @see CommonGoal
 *  @see Goal
 *
 * @author Giacomo Groppi
 * */
public class StairGoal extends CommonGoal{
    /**
     * Groups of coordinates which, if null within the bookshelf, satisfy the Goal.
     * @see #check
     * */
    private static final List<List<Shelf>> DATA_STAIR = new ArrayList<>();

    /**
     * @return true iff the number of points satisfying the first map is equal to its size,
     *  or this number is 0 and the number of points satisfying the second condition is equal to its size.
     * */
    private static boolean isSatisfied (List<Shelf> first, List<Shelf> second, BookshelfMask bookshelfMask) {
        long sat;

        Predicate<Shelf> match = shelf -> bookshelfMask.tileAt(shelf) != Tile.EMPTY;

        sat = first
                .stream()
                .filter(match).count();

        if (sat == 0)
            return true;

        if (sat != first.size())
            return false;

        return second
                .stream().noneMatch(match);
    }

    private static final Predicate<BookshelfMask> check = (BookshelfMask bookshelfMask) -> {
        if (isSatisfied(DATA_STAIR.get(1), DATA_STAIR.get(0), bookshelfMask))
            return true;
        return isSatisfied(DATA_STAIR.get(3), DATA_STAIR.get(2), bookshelfMask);
    };

    static {
        for (int i = 0; i < 4; i++)
            StairGoal.DATA_STAIR.add(new ArrayList<>());

        for (int i = 0; i < 4; i++)
            StairGoal.DATA_STAIR.get(0).add(
                            Shelf.getInstance(i, i+1)
                    );

        for (int i = 0; i < 5; i++)
            StairGoal.DATA_STAIR.get(1).add(
                    Shelf.getInstance(i, i)
            );

        for (int i = 0; i < 4; i++)
            StairGoal.DATA_STAIR.get(2).add(
                    Shelf.getInstance(i, 3 - i)
            );

        for (int i = 0; i < 5; i++)
            StairGoal.DATA_STAIR.get(3).add(
                    Shelf.getInstance(i, 4 - i)
            );
    }


    /**
     * Constructor of the class.
     *
     * @see CommonGoal#CommonGoal(Fetcher, Filter, Evaluator)
     *
     * @param numPlayers the number of players in the game
     * */
    public StairGoal(int numPlayers) {
        super(new UnionFetcher(new ArrayList<>(
                        List.of(
                                new ShapeFetcher(Shape.ENLARGED_STAIR),
                                new ShapeFetcher(Shape.ENLARGED_STAIR.verticalFlip())
                        )
                )),
                new NumDifferentColorFilter(1, 6),
                new AtLeastEvaluator(numPlayers, 1, check));
    }
}
