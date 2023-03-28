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
        satisfied = mask.countTilesOfColor(Tile.CYAN) >= 8 ||
                mask.countTilesOfColor(Tile.YELLOW) >= 8 ||
                mask.countTilesOfColor(Tile.BLUE) >= 8 ||
                mask.countTilesOfColor(Tile.MAGENTA) >= 8 ||
                mask.countTilesOfColor(Tile.GREEN) >= 8 ||
                mask.countTilesOfColor(Tile.WHITE) >= 8;

        return true;
    }

    @Override
    public int getPoints() {
        if(satisfied) {
            return super.getPoints();
        }
        return 0;
    }
}
