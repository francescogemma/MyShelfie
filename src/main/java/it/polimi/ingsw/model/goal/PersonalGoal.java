package it.polimi.ingsw.model.goal;

import it.polimi.ingsw.model.bookshelf.Shape;
import it.polimi.ingsw.model.bookshelf.Shelf;
import it.polimi.ingsw.model.evaluator.PersonalGoalEvaluator;
import it.polimi.ingsw.model.fetcher.ShapeFetcher;
import it.polimi.ingsw.model.filter.AcceptsAllFilter;
import it.polimi.ingsw.model.tile.TileColor;

import java.util.List;
import java.util.Map;

/**
 * This class represents a personal goal. It extends Goal.
 * {@see Goal}
 *
 * @author Francesco Gemma
 * @author Cristiano Migali
 */
public class PersonalGoal extends IndexedGoal {
    /**
     * Constructor of the class.
     *      It takes:
     *      <ul>
     *          <li> a {@link ShapeFetcher} to fetch the whole bookshelf</li>
     *          <li> an {@link AcceptsAllFilter} because we need to fetch the whole bookshelf; </li>
     *          <li> an {@link PersonalGoalEvaluator} with the mask and a list of points. It will give us the score. </li>
     *      </ul>
     * @param tilesColorMask the map containing exactly 6 pairs,
     *                       each one containing a shelf and the tile color of that shelf.
     */
    private PersonalGoal(Map<Shelf, TileColor> tilesColorMask) {
        super(  new ShapeFetcher(Shape.WHOLE_BOOKSHELF),
                new AcceptsAllFilter(),
                new PersonalGoalEvaluator(tilesColorMask, List.of(1, 2, 4, 6, 9, 12))
        );
    }

    /**
     * This method gives an index between 0 and 11 for every personal goal in the game,
     * and creates a new personal goal based on that index passed as parameter.
     * @param index an index between 0 and 11 representing the personal goal to extract
     * @return the personal goal corresponding to the given index
     * @throws IllegalArgumentException if the index is not between 0 and 11
     * @throws IllegalStateException if we are trying to create a personal goal which index is not between 0 and 11
     */
    public static PersonalGoal fromIndex(int index) {
        if (index < 0 || index >= 12) {
            throw new IllegalArgumentException("Index must be between 0 and 11 when extracting a personal goal, got: "
                + index);
        }

        return switch (index) {
            case 0 -> (PersonalGoal) new PersonalGoal(
                Map.of(
                    Shelf.getInstance(0, 0), TileColor.MAGENTA,
                    Shelf.getInstance(0, 2), TileColor.BLUE,
                    Shelf.getInstance(1, 4), TileColor.GREEN,
                    Shelf.getInstance(2, 3), TileColor.WHITE,
                    Shelf.getInstance(3, 1), TileColor.YELLOW,
                    Shelf.getInstance(5, 2), TileColor.CYAN
                )
            ).index(0);

            case 1 -> (PersonalGoal) new PersonalGoal(
                    Map.of(
                            Shelf.getInstance(1, 1), TileColor.MAGENTA,
                            Shelf.getInstance(2, 0), TileColor.GREEN,
                            Shelf.getInstance(2, 2), TileColor.YELLOW,
                            Shelf.getInstance(3, 4), TileColor.WHITE,
                            Shelf.getInstance(4, 3), TileColor.CYAN,
                            Shelf.getInstance(5, 4), TileColor.BLUE
                    )
            ).index(1);

            case 2 -> (PersonalGoal) new PersonalGoal(
                    Map.of(
                            Shelf.getInstance(1, 0), TileColor.BLUE,
                            Shelf.getInstance(1, 3), TileColor.YELLOW,
                            Shelf.getInstance(2, 2), TileColor.MAGENTA,
                            Shelf.getInstance(3, 1), TileColor.GREEN,
                            Shelf.getInstance(3, 4), TileColor.CYAN,
                            Shelf.getInstance(5, 0), TileColor.WHITE
                    )
            ).index(2);

            case 3 -> (PersonalGoal) new PersonalGoal(
                    Map.of(
                            Shelf.getInstance(0, 4), TileColor.YELLOW,
                            Shelf.getInstance(2, 0), TileColor.CYAN,
                            Shelf.getInstance(2, 2), TileColor.BLUE,
                            Shelf.getInstance(3, 3), TileColor.MAGENTA,
                            Shelf.getInstance(4, 1), TileColor.WHITE,
                            Shelf.getInstance(4, 2), TileColor.GREEN
                    )
            ).index(3);

            case 4 -> (PersonalGoal) new PersonalGoal(
                    Map.of(
                            Shelf.getInstance(1, 1), TileColor.CYAN,
                            Shelf.getInstance(3, 1), TileColor.BLUE,
                            Shelf.getInstance(3, 2), TileColor.WHITE,
                            Shelf.getInstance(4, 4), TileColor.MAGENTA,
                            Shelf.getInstance(5, 0), TileColor.YELLOW,
                            Shelf.getInstance(5, 3), TileColor.GREEN
                    )
            ).index(4);

            case 5 -> (PersonalGoal) new PersonalGoal(
                    Map.of(
                            Shelf.getInstance(0, 2), TileColor.CYAN,
                            Shelf.getInstance(0, 4), TileColor.GREEN,
                            Shelf.getInstance(2, 3), TileColor.WHITE,
                            Shelf.getInstance(4, 1), TileColor.YELLOW,
                            Shelf.getInstance(4, 3), TileColor.BLUE,
                            Shelf.getInstance(5, 0), TileColor.MAGENTA
                    )
            ).index(5);

            case 6 -> (PersonalGoal) new PersonalGoal(
                    Map.of(
                            Shelf.getInstance(0, 0), TileColor.GREEN,
                            Shelf.getInstance(1, 3), TileColor.BLUE,
                            Shelf.getInstance(2, 1), TileColor.MAGENTA,
                            Shelf.getInstance(3, 0), TileColor.CYAN,
                            Shelf.getInstance(4, 4), TileColor.YELLOW,
                            Shelf.getInstance(5, 2), TileColor.WHITE
                    )
            ).index(6);

            case 7 -> (PersonalGoal) new PersonalGoal(
                    Map.of(
                            Shelf.getInstance(0, 4), TileColor.BLUE,
                            Shelf.getInstance(1, 1), TileColor.GREEN,
                            Shelf.getInstance(2, 2), TileColor.CYAN,
                            Shelf.getInstance(3, 0), TileColor.MAGENTA,
                            Shelf.getInstance(4, 3), TileColor.WHITE,
                            Shelf.getInstance(5, 3), TileColor.YELLOW
                    )
            ).index(7);

            case 8 -> (PersonalGoal) new PersonalGoal(
                    Map.of(
                            Shelf.getInstance(0, 2), TileColor.YELLOW,
                            Shelf.getInstance(2, 2), TileColor.GREEN,
                            Shelf.getInstance(3, 4), TileColor.WHITE,
                            Shelf.getInstance(4, 1), TileColor.CYAN,
                            Shelf.getInstance(4, 4), TileColor.MAGENTA,
                            Shelf.getInstance(5, 0), TileColor.BLUE
                    )
            ).index(8);

            case 9 -> (PersonalGoal) new PersonalGoal(
                    Map.of(
                            Shelf.getInstance(0, 4), TileColor.CYAN,
                            Shelf.getInstance(1, 1), TileColor.YELLOW,
                            Shelf.getInstance(2, 0), TileColor.WHITE,
                            Shelf.getInstance(3, 3), TileColor.GREEN,
                            Shelf.getInstance(4, 1), TileColor.BLUE,
                            Shelf.getInstance(5, 3), TileColor.MAGENTA
                    )
            ).index(9);

            case 10 -> (PersonalGoal) new PersonalGoal(
                    Map.of(
                            Shelf.getInstance(0, 2), TileColor.MAGENTA,
                            Shelf.getInstance(1, 1), TileColor.WHITE,
                            Shelf.getInstance(2, 0), TileColor.YELLOW,
                            Shelf.getInstance(3, 2), TileColor.BLUE,
                            Shelf.getInstance(4, 4), TileColor.GREEN,
                            Shelf.getInstance(5, 3), TileColor.CYAN
                    )
            ).index(10);

            case 11 -> (PersonalGoal) new PersonalGoal(
                    Map.of(
                            Shelf.getInstance(0, 2), TileColor.WHITE,
                            Shelf.getInstance(1, 1), TileColor.MAGENTA,
                            Shelf.getInstance(2, 2), TileColor.BLUE,
                            Shelf.getInstance(3, 3), TileColor.CYAN,
                            Shelf.getInstance(4, 4), TileColor.YELLOW,
                            Shelf.getInstance(5, 0), TileColor.GREEN
                    )
            ).index(11);

            default -> throw new IllegalStateException("Unexpected index value: " + index);
        };
    }

    /**
     * @return the map which associates the {@link Shelf shelves} in the {@link it.polimi.ingsw.model.bookshelf.Bookshelf}
     * with the required {@link TileColor} needed in order to complete the personal goal.
     */
    public Map<Shelf, TileColor> getTilesColorMask() {
        return ((PersonalGoalEvaluator) evaluator).getTilesColorMask();
    }
}
