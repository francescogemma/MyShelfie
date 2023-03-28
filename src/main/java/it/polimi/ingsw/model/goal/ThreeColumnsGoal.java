package it.polimi.ingsw.model.goal;

import it.polimi.ingsw.model.bookshelf.Shape;
import it.polimi.ingsw.model.evaluator.AtLeastEvaluator;
import it.polimi.ingsw.model.fetcher.ShapeFetcher;
import it.polimi.ingsw.model.filter.NumDifferentColorFilter;

public class ThreeColumnsGoal extends CommonGoal {
    public ThreeColumnsGoal(int numPlayers) {
        super(
                new ShapeFetcher(Shape.getColumn(6)),
                new NumDifferentColorFilter(1, 3),
                new AtLeastEvaluator(numPlayers, 3)
        );
    }
}
