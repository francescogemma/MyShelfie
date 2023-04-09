package it.polimi.ingsw.model.evaluator;

import java.util.ArrayList;
import java.util.List;

/**
 * This class should be extended by all evaluators that have "lowering points".
 * A player might get a high score the first time a target is met, and then progressively
 * get lower and lower scores.
 *
 * @author Michele Miotti
 */
public abstract class CommonGoalEvaluator implements Evaluator {
    /**
     * This attribute contains all points that should be given to a player.
     * The last point to be given is the first element of the array.
     */
    private final List<Integer> pointStack;

    /**
     * This constructor is needed to initiate the pointStack attribute, since its
     * value depends on the game's player amount.
     *
     * @param playersAmount is this game's player amount (2 to 4).
     */
    protected CommonGoalEvaluator(int playersAmount) {
        switch (playersAmount) {
            // These must be ArrayList, List.of returns an ImmutableList on which remove or add are unsupported.
            case 2 -> pointStack = new ArrayList<>(List.of(4, 8));
            case 3 -> pointStack = new ArrayList<>(List.of(4, 6, 8));
            case 4 -> pointStack = new ArrayList<>(List.of(2, 4, 6, 8));

            default -> throw new IllegalArgumentException("Players must be between 2 and 4.");
        }
    }

    @Override
    public int getPoints() {
        if (pointStack.isEmpty()) {
            throw new IllegalStateException("Can't get points: pointStack has no remaining elements.");
        }
        return pointStack.remove(pointStack.size() - 1);
    }
}
