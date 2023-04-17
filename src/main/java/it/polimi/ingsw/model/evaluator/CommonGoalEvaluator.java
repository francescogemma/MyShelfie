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
    private List<Integer> pointStack;

    @Override
    public int getPoints() {
        if (pointStack.isEmpty()) {
            throw new IllegalStateException("Can't get points: pointStack has no remaining elements.");
        }
        return pointStack.remove(pointStack.size() - 1);
    }

    // TODO: (for Cristiano) complete JavaDoc
    public List<Integer> getPointStack() {
        return new ArrayList<>(pointStack);
    }

    // TODO: (for Cristiano) refactor the point stack setting mechanism
    public void setPointStack(List<Integer> pointStack) {
        this.pointStack = new ArrayList<>(pointStack);
    }
}
