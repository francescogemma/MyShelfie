package it.polimi.ingsw.model.evaluator;

import it.polimi.ingsw.model.Tile;
import it.polimi.ingsw.model.bookshelf.BookshelfMask;

public class EightTilesGoalEvaluator extends CommonGoalEvaluator implements Evaluator {
    private boolean satisfied;

    public EightTilesGoalEvaluator(int playersAmount) {
        super(playersAmount);
        satisfied = false;
    }

    @Override
    public boolean add(BookshelfMask mask) {
        for (Tile tile : Tile.values()) {
            satisfied |= tile != Tile.EMPTY && mask.countTilesOfColor(tile) >= 8;
        }

        return true;
    }

    @Override
    public int getPoints() {
        if(satisfied) {
            return super.getPoints();
        }
        return 0;
    }

    @Override
    public void clear() {
        satisfied = false;
    }
}
