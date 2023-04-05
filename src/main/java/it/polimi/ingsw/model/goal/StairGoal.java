package it.polimi.ingsw.model.goal;

import it.polimi.ingsw.model.Tile;
import it.polimi.ingsw.model.bookshelf.*;
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
    private static final Predicate<BookshelfMask> check = (BookshelfMask bookshelfMask) -> {
        List<Shelf> shelves = bookshelfMask.getShelves();
        for (Shelf s: shelves) {
            if (s.getRow() != 0) {
                Shelf positionUp = s.move(Offset.getInstance(-1, 0));
                if (bookshelfMask.tileAt(positionUp) == Tile.EMPTY)
                    continue;
                if (!shelves.contains(positionUp))
                    return false;
            }
        }
        return true;
    };


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
                                new ShapeFetcher(Shape.STAIR),
                                new ShapeFetcher(Shape.STAIR.verticalFlip())
                        )
                )),
                new NumDifferentColorFilter(1, 6),
                new AtLeastEvaluator(numPlayers, 1, check));
    }
}
