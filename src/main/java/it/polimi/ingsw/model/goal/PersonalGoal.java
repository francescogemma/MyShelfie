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
}
