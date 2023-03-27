package it.polimi.ingsw.model.evaluator;

import it.polimi.ingsw.model.bookshelf.BookshelfMask;
import it.polimi.ingsw.model.bookshelf.Shelf;
import it.polimi.ingsw.model.Tile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This {@link Evaluator evaluator} gets a single full {@link BookshelfMask BookshelfMask} and
 * finds all {@link Tile tiles} that coincide with the specifics provided by a
 * {@link PersonalGoalEvaluator#personalGoal} map, that relates all 6 special
 * shelves to their expected tile.
 *
 * @author Michele Miotti
 */
public class PersonalGoalEvaluator implements Evaluator {
    /**
     * This map relates each one of the 6 special shelves with one
     * specific color. This information has to be provided to the constructor.
     */
    private final Map<Shelf, Tile> personalGoal;
    /**
     * This list maps each natural number up to 6 (x) to another number (y), which
     * represents the score that should be given to a player once x shelves contain
     * the expected color.
     */
    private final List<Integer> pointsMapping;
    /**
     * The amount of shelves that have a color that coincides with the specifics
     * given by the {@link this#personalGoal} object.
     */
    private int successfulShelves;
    private int points;

    /**
     * Type constructor.
     * @param personalGoal is a mapping from the set of shelves to the set of colors.
     *                          It should contain exactly 6 elements.
     * @param pointsMapping is a list of 6 elements, corresponding to the points awarded to a player
     *                      for successfully matching 1, 2, 3, ... tiles to their correct color.
     */
    public PersonalGoalEvaluator(Map personalGoal, ArrayList<Integer> pointsMapping) {
        this.personalGoal = new HashMap<>(personalGoal);
        this.pointsMapping = pointsMapping;

        successfulShelves = 0;
        points = 0;
    }

    /*
     * @param bookshelfMask needs to be a "full mask": all shelves must be contained.
     * @return true, always, since the input should already contain all the information
     * needed to get the score; This function should only be called once.
     */
    @Override
    public boolean add(BookshelfMask bookshelfMask) {
        for (Shelf goalShelf : personalGoal.keySet()) {
            if (bookshelfMask.tileAt(goalShelf) == personalGoal.get(goalShelf)) {
                points = pointsMapping.get(successfulShelves++);
            }
        }
        return true;
    }

    /*
     * @return all points that have been acquired by color matches.
     */
    @Override
    public int getPoints() {
        return points;
    }
}
