package it.polimi.ingsw.model.goal;

import it.polimi.ingsw.model.bookshelf.Shape;
import it.polimi.ingsw.model.evaluator.EightTilesGoalEvaluator;
import it.polimi.ingsw.model.fetcher.ShapeFetcher;
import it.polimi.ingsw.model.filter.AcceptsAllFilter;

public class EightTilesGoal extends CommonGoal {
    public EightTilesGoal(int numPlayers) {
        super(
                new ShapeFetcher(Shape.WHOLE_BOOKSHELF),
                new AcceptsAllFilter(),
                new EightTilesGoalEvaluator(numPlayers)
        );
    }
}
