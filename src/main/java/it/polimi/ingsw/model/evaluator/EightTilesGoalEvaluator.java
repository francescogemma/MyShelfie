package it.polimi.ingsw.model.evaluator;

import it.polimi.ingsw.model.Tile;
import it.polimi.ingsw.model.bookshelf.BookshelfMask;
import it.polimi.ingsw.model.bookshelf.Shelf;

public class EightTilesGoalEvaluator extends CommonGoalEvaluator implements Evaluator {
    boolean satisfied;

    public EightTilesGoalEvaluator(int playersAmount) {
        super(playersAmount);
        satisfied = false;
    }

    @Override
    public boolean add(BookshelfMask mask) {
        if(mask.countTilesOfColor(Tile.CYAN) >= 2 ||
           mask.countTilesOfColor(Tile.YELLOW) >= 2 ||
           mask.countTilesOfColor(Tile.BLUE) >= 2 ||
           mask.countTilesOfColor(Tile.MAGENTA) >= 2 ||
           mask.countTilesOfColor(Tile.GREEN) >= 2 ||
           mask.countTilesOfColor(Tile.WHITE) >= 2) {
            satisfied = true;
        }

        return satisfied;
    }

    @Override
    public int getPoints() {
        if(!satisfied) {
            return 0;
        } else {
            return super.getPoints();
        }
    }

    private int countTilesOfColor(BookshelfMask mask, Tile tile) {
        int count = 0;

        if(tile == Tile.EMPTY) {
            throw new IllegalArgumentException("Cannot count empty tiles");
        }

        for(Shelf shelf : mask.getShelves()) {
            if(mask.tileAt(shelf) == tile) {
                count++;
            }
        }

        return count;
    }
}
