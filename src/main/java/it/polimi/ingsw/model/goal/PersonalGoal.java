package it.polimi.ingsw.model.goal;

import it.polimi.ingsw.model.bookshelf.Shape;
import it.polimi.ingsw.model.bookshelf.Shelf;
import it.polimi.ingsw.model.evaluator.PersonalGoalEvaluator;
import it.polimi.ingsw.model.fetcher.ShapeFetcher;
import it.polimi.ingsw.model.filter.AcceptsAllFilter;
import it.polimi.ingsw.model.tile.TileColor;

import java.util.List;
import java.util.Map;

public class PersonalGoal extends Goal {
    public PersonalGoal(Map<Shelf, TileColor> tilesColorMask) {
        super(  new ShapeFetcher(Shape.WHOLE_BOOKSHELF),
                new AcceptsAllFilter(),
                new PersonalGoalEvaluator(tilesColorMask, List.of(1, 2, 4, 6, 9, 12))
        );
    }

    public static PersonalGoal formIndex(int index) {
        if (index < 0 || index >= 12) {
            throw new IllegalArgumentException("Index must be between 0 and 11 when extracting a personal goal, got: "
                + index);
        }

        // TODO: Add all the personal goals
        return switch (index) {
            default -> new PersonalGoal(
                Map.of(
                    Shelf.getInstance(0, 0), TileColor.MAGENTA,
                    Shelf.getInstance(0, 2), TileColor.BLUE,
                    Shelf.getInstance(1, 4), TileColor.GREEN,
                    Shelf.getInstance(2, 3), TileColor.WHITE,
                    Shelf.getInstance(3, 1), TileColor.YELLOW,
                    Shelf.getInstance(5, 2), TileColor.CYAN
                )
            );
        };
    }
}
