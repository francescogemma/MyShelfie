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
     * It sets {@link EightTilesGoalEvaluator#satisfied} to false.
     */
    public EightTilesGoalEvaluator() {
        satisfied = false;
        pointMasks = new BookshelfMaskSet();
    }

    /**
     * Helper method to clean up a mask based on specified color.
     * @param bookshelfMask will be copied, emptied, and returned with correct tiles.
     * @param tileColor is the color that will characterize the newly created mask
     * @return a mask subset of the input that contains only the specified color.
     */
    private BookshelfMask filterColor(BookshelfMask bookshelfMask, TileColor tileColor) {
        // create a new mask with same properties of original, but completely empty.
        BookshelfMask maskToAdd = new BookshelfMask(bookshelfMask);
        maskToAdd.clear();

        // counter used to break the computation if we've already reached 8 tiles.
        int count = 0;

        // look through all tiles and add the correctly-coloured ones to the mask.
        for (int row = 0; row < Bookshelf.ROWS && count < 8; row++) {
            for (int column = 0; column < Bookshelf.COLUMNS && count < 8; column++) {
                if (maskToAdd.getTileColorAt(Shelf.getInstance(row, column)) == tileColor) {
                    maskToAdd.add(Shelf.getInstance(row, column));
                    count++;
                }
            }
        }

        return maskToAdd;
    }

    @Override
    public boolean add(BookshelfMask mask) {
        for (TileColor tileColor : TileColor.values()) {
            if (tileColor != TileColor.EMPTY && mask.countTilesOfColor(tileColor) >= 8) {
                satisfied = true;

                // add that mask, without unrelated colors.
                pointMasks.add(filterColor(mask, tileColor));
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
