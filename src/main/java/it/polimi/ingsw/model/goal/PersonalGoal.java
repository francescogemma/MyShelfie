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
public class PersonalGoal extends Goal {
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
    public PersonalGoal(Map<Shelf, TileColor> tilesColorMask) {
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
            case 0 -> new PersonalGoal(
                Map.of(
                    Shelf.getInstance(0, 0), TileColor.MAGENTA,
                    Shelf.getInstance(0, 2), TileColor.BLUE,
                    Shelf.getInstance(1, 4), TileColor.GREEN,
                    Shelf.getInstance(2, 3), TileColor.WHITE,
                    Shelf.getInstance(3, 1), TileColor.YELLOW,
                    Shelf.getInstance(5, 2), TileColor.CYAN
                )
            );

            case 1 -> new PersonalGoal(
                    Map.of(
                            Shelf.getInstance(1, 1), TileColor.MAGENTA,
                            Shelf.getInstance(2, 0), TileColor.GREEN,
                            Shelf.getInstance(2, 2), TileColor.YELLOW,
                            Shelf.getInstance(3, 4), TileColor.WHITE,
                            Shelf.getInstance(4, 3), TileColor.CYAN,
                            Shelf.getInstance(5, 4), TileColor.BLUE
                    )
            );

            case 2 -> new PersonalGoal(
                    Map.of(
                            Shelf.getInstance(1, 0), TileColor.BLUE,
                            Shelf.getInstance(1, 3), TileColor.YELLOW,
                            Shelf.getInstance(2, 2), TileColor.MAGENTA,
                            Shelf.getInstance(3, 1), TileColor.GREEN,
                            Shelf.getInstance(3, 4), TileColor.CYAN,
                            Shelf.getInstance(5, 0), TileColor.WHITE
                    )
            );

            case 3 -> new PersonalGoal(
                    Map.of(
                            Shelf.getInstance(0, 4), TileColor.YELLOW,
                            Shelf.getInstance(2, 0), TileColor.CYAN,
                            Shelf.getInstance(2, 2), TileColor.BLUE,
                            Shelf.getInstance(3, 3), TileColor.MAGENTA,
                            Shelf.getInstance(4, 1), TileColor.WHITE,
                            Shelf.getInstance(4, 2), TileColor.GREEN
                    )
            );

            case 4 -> new PersonalGoal(
                    Map.of(
                            Shelf.getInstance(1, 1), TileColor.CYAN,
                            Shelf.getInstance(3, 1), TileColor.BLUE,
                            Shelf.getInstance(3, 2), TileColor.WHITE,
                            Shelf.getInstance(4, 4), TileColor.MAGENTA,
                            Shelf.getInstance(5, 0), TileColor.YELLOW,
                            Shelf.getInstance(5, 3), TileColor.GREEN
                    )
            );

            case 5 -> new PersonalGoal(
                    Map.of(
                            Shelf.getInstance(0, 2), TileColor.CYAN,
                            Shelf.getInstance(0, 4), TileColor.GREEN,
                            Shelf.getInstance(2, 3), TileColor.WHITE,
                            Shelf.getInstance(4, 1), TileColor.YELLOW,
                            Shelf.getInstance(4, 3), TileColor.BLUE,
                            Shelf.getInstance(5, 0), TileColor.MAGENTA
                    )
            );

            case 6 -> new PersonalGoal(
                    Map.of(
                            Shelf.getInstance(0, 0), TileColor.GREEN,
                            Shelf.getInstance(1, 3), TileColor.BLUE,
                            Shelf.getInstance(2, 1), TileColor.MAGENTA,
                            Shelf.getInstance(3, 0), TileColor.CYAN,
                            Shelf.getInstance(4, 4), TileColor.YELLOW,
                            Shelf.getInstance(5, 2), TileColor.WHITE
                    )
            );

            case 7 -> new PersonalGoal(
                    Map.of(
                            Shelf.getInstance(0, 4), TileColor.BLUE,
                            Shelf.getInstance(1, 1), TileColor.GREEN,
                            Shelf.getInstance(2, 2), TileColor.CYAN,
                            Shelf.getInstance(3, 0), TileColor.MAGENTA,
                            Shelf.getInstance(4, 3), TileColor.WHITE,
                            Shelf.getInstance(5, 3), TileColor.YELLOW
                    )
            );

            case 8 -> new PersonalGoal(
                    Map.of(
                            Shelf.getInstance(0, 2), TileColor.YELLOW,
                            Shelf.getInstance(2, 2), TileColor.GREEN,
                            Shelf.getInstance(3, 4), TileColor.WHITE,
                            Shelf.getInstance(4, 1), TileColor.CYAN,
                            Shelf.getInstance(4, 4), TileColor.MAGENTA,
                            Shelf.getInstance(5, 0), TileColor.BLUE
                    )
            );

            case 9 -> new PersonalGoal(
                    Map.of(
                            Shelf.getInstance(0, 4), TileColor.CYAN,
                            Shelf.getInstance(1, 1), TileColor.YELLOW,
                            Shelf.getInstance(2, 0), TileColor.WHITE,
                            Shelf.getInstance(3, 3), TileColor.GREEN,
                            Shelf.getInstance(4, 1), TileColor.BLUE,
                            Shelf.getInstance(5, 3), TileColor.MAGENTA
                    )
            );

            case 10 -> new PersonalGoal(
                    Map.of(
                            Shelf.getInstance(0, 2), TileColor.MAGENTA,
                            Shelf.getInstance(1, 1), TileColor.WHITE,
                            Shelf.getInstance(2, 0), TileColor.YELLOW,
                            Shelf.getInstance(3, 2), TileColor.BLUE,
                            Shelf.getInstance(4, 4), TileColor.GREEN,
                            Shelf.getInstance(5, 3), TileColor.CYAN
                    )
            );

            case 11 -> new PersonalGoal(
                    Map.of(
                            Shelf.getInstance(0, 2), TileColor.WHITE,
                            Shelf.getInstance(1, 1), TileColor.MAGENTA,
                            Shelf.getInstance(2, 2), TileColor.BLUE,
                            Shelf.getInstance(3, 3), TileColor.CYAN,
                            Shelf.getInstance(4, 4), TileColor.YELLOW,
                            Shelf.getInstance(5, 0), TileColor.GREEN
                    )
            );

            default -> throw new IllegalStateException("Unexpected index value: " + index);
        };
    }
}
