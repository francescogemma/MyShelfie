package it.polimi.ingsw.model.evaluator;

import it.polimi.ingsw.model.tile.TileColor;
import it.polimi.ingsw.model.bookshelf.Bookshelf;
import it.polimi.ingsw.model.bookshelf.BookshelfMask;
import it.polimi.ingsw.model.bookshelf.BookshelfMaskSet;
import it.polimi.ingsw.model.bookshelf.Shelf;
import it.polimi.ingsw.model.goal.EightTilesGoal;

/**
 * This class represents an evaluator for the common goal {@link EightTilesGoal}.
 * <p>
 * It simply counts the number of tiles of the same color to determine if the goal is satisfied or not.
 * <p>
 * It extends CommonGoalEvaluator and implements Evaluator.
 *
 * @see CommonGoalEvaluator
 * @see Evaluator
 * @see EightTilesGoal
 *
 * @author Francesco Gemma
 */
public class EightTilesGoalEvaluator extends CommonGoalEvaluator implements Evaluator {
    private boolean satisfied;
    private BookshelfMaskSet pointMasks;

    /**
     * Constructor of the class.
     * It calls the constructor of the superclass with the given playersAmount,
     * and sets {@link EightTilesGoalEvaluator#satisfied} to false.
     *
     * @see CommonGoalEvaluator#CommonGoalEvaluator(int)
     *
     * @param playersAmount the number of players in the game.
     */
    public EightTilesGoalEvaluator(int playersAmount) {
        super(playersAmount);
        satisfied = false;
        pointMasks = new BookshelfMaskSet();
    }

    @Override
    public boolean add(BookshelfMask mask) {
        for(TileColor tileColor : TileColor.values()) {
            if(tileColor != TileColor.EMPTY && mask.countTilesOfColor(tileColor) >= 8) {
                satisfied = true;

                BookshelfMask maskToAdd = new BookshelfMask(mask);
                maskToAdd.clear();

                int count = 0;
                for(int row = 0; row < Bookshelf.ROWS && count < 8; row++) {
                    for(int column = 0; column < Bookshelf.COLUMNS && count < 8; column++) {
                        if(maskToAdd.getTileColorAt(Shelf.getInstance(row, column)) == tileColor) {
                            maskToAdd.add(Shelf.getInstance(row, column));
                            count++;
                        }
                    }
                }
                pointMasks.add(maskToAdd);

                break;
            }
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
        pointMasks = new BookshelfMaskSet();
    }

    @Override
    public BookshelfMaskSet getPointMasks() {
        return pointMasks;
    }
}
