package it.polimi.ingsw.model.goal;

import it.polimi.ingsw.model.bookshelf.Shape;
import it.polimi.ingsw.model.evaluator.AtLeastEvaluator;
import it.polimi.ingsw.model.fetcher.ShapeFetcher;
import it.polimi.ingsw.model.fetcher.UnionFetcher;
import it.polimi.ingsw.model.filter.NumDifferentColorFilter;

import java.util.ArrayList;
import java.util.List;

public class DiagonalGoal extends CommonGoal {
    public DiagonalGoal(int numPlayers) {
        super(
                new UnionFetcher(new ArrayList<>(
                        List.of(
                                new ShapeFetcher(Shape.getMainDiagonal(5)),
                                new ShapeFetcher(Shape.getMainDiagonal(5).verticalFlip())
                        )
                )),
                new NumDifferentColorFilter(1, 1),
                new AtLeastEvaluator(numPlayers, 1)
        );
    }
}
