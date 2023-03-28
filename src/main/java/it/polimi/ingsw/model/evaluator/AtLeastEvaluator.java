package it.polimi.ingsw.model.evaluator;

import it.polimi.ingsw.model.bookshelf.BookshelfMask;

/**
 * Simple evaluator that counts how many {@link BookshelfMask BookshelfMasks} have been fed
 * into its add method, and checks if the amount is great enough to grant a player their points.
 *
 * @author Michele Miotti
 */
public class AtLeastEvaluator extends CommonGoalEvaluator implements Evaluator {
    /**
     * The amount of masks inserted into the evaluator so far.
     */
    private int maskCounter;

    /**
     * The needed amount of masks give the player a non-zero amount of points.
     */
    private final int targetAmount;

    /**
     * Class constructor.
     * @param playersAmount is this game's player amount (2 to 4).
     * @param targetAmount is the needed quantity of {@link BookshelfMask BookshelfMasks} to
     *                     grant a player a non-zero amount of points.
     */
    public AtLeastEvaluator(int playersAmount, int targetAmount) {
        super(playersAmount);
        this.targetAmount = targetAmount;
        maskCounter = 0;
    }
    @Override
    public boolean add(BookshelfMask bookshelfMask) {
        return ++maskCounter >= targetAmount;
    }

    @Override
    public int getPoints() {
        if (maskCounter < targetAmount) return 0;
        return super.getPoints();
    }
}
