package it.polimi.ingsw.model.evaluator;

import it.polimi.ingsw.model.Tile;
import it.polimi.ingsw.model.bookshelf.BookshelfMask;
import it.polimi.ingsw.model.goal.EightTilesGoal;

/**
 * This class represents an evaluator for the common goal {@link EightTilesGoal}.
 * <p>
 * It simply counts the number of tiles of the same type to determine if the goal is satisfied or not.
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
