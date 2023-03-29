package it.polimi.ingsw.model.goal;

import it.polimi.ingsw.model.bookshelf.Offset;
import it.polimi.ingsw.model.bookshelf.Shape;
import it.polimi.ingsw.model.evaluator.AtLeastEvaluator;
import it.polimi.ingsw.model.fetcher.ShapeFetcher;
import it.polimi.ingsw.model.filter.StairFilter;

import java.util.ArrayList;
import java.util.Arrays;

public class StairGoal extends CommonGoal{

    private static boolean[][] convertInput(int[][] data) {
        boolean[][] res = new boolean[data.length][data.length];
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[0].length; j++) {
                res[i][j] = data[i][j] == 1;
            }
        }

        return res;
    }

    public StairGoal(int numPleyer) {
        super(new ShapeFetcher(Shape.ENLARGED_STAIR),
                new StairFilter(convertInput(new int[][] {
                        {1, 0, 0, 0, 0},
                        {1, 1, 0, 0, 0},
                        {1, 1, 1, 0, 0},
                        {1, 1, 1, 1, 0},
                        {1, 1, 1, 1, 1}
                })),
                new AtLeastEvaluator(numPleyer, 1));
    }
}