package it.polimi.ingsw.model.goal;

import it.polimi.ingsw.model.bookshelf.Offset;
import it.polimi.ingsw.model.bookshelf.Shape;
import it.polimi.ingsw.model.evaluator.AtLeastEvaluator;
import it.polimi.ingsw.model.fetcher.ShapeFetcher;
import it.polimi.ingsw.model.filter.NumDifferentColorFilter;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Five tiles of the same type forming an X
 * @author Giacomo Groppi
 * */
public class XPatternGoal extends CommonGoal{
    public XPatternGoal(int numPlayer) {
        super(new ShapeFetcher(
                new Shape(
                    new ArrayList<>(
                        Arrays.asList(
                            Offset.getInstance(0, 0),
                            Offset.getInstance(0, 2),
                            Offset.getInstance(1, 1),
                            Offset.getInstance(2, 0),
                            Offset.getInstance(2, 2)
                        )
                    )
                )
            ),
            new NumDifferentColorFilter(1, 1),
            new AtLeastEvaluator(numPlayer, 1)
            );
    }
}
