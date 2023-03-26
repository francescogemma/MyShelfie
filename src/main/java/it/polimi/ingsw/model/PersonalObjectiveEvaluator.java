package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This {@link Evaluator evaluator} gets a single full {@link LibraryMask LibraryMask} and
 * finds all {@link Tile tiles} that coincide with the specifics provided by a
 * {@link PersonalObjectiveEvaluator#personalObjective} map, that relates all 6 special
 * shelves to their expected tile.
 *
 * @author Michele Miotti
 */
public class PersonalObjectiveEvaluator implements Evaluator {
    /**
     * This map relates each one of the 6 special shelves with one
     * specific color. This information has to be provived to the constructor.
     */
    private final Map<Shelf, Tile> personalObjective;
    /**
     * This list maps each natural number up to 6 (x) to another number (y), which
     * represents the score that should be given to a player once x shelves containt
     * the expected color.
     */
    private final List<Integer> pointsMapping;
    /**
     * The amount of shelves that have a color that coincides with the specifics
     * given by the {@link this#personalObjective} object.
     */
    private int successfulShelves;
    private int points;

    /**
     * Type constructor.
     * @param personalObjective is a mapping from the set of shelves to the set of colors.
     *                          It should contain exactly 6 elements.
     * @param pointsMapping is a list of 6 elements, corresponding to the points awarded to a player
     *                      for successfully matching 1, 2, 3, ... tiles to their correct color.
     */
    public PersonalObjectiveEvaluator(Map personalObjective, ArrayList<Integer> pointsMapping) {
        this.personalObjective = new HashMap<>(personalObjective);
        this.pointsMapping = pointsMapping;

        successfulShelves = 0;
        points = 0;
    }

    /**
     * @param libraryMask needs to be a "full mask": all shelves must be contained.
     * @return true, always, since the input should already contain all the information
     * needed to get the score; This function should only be called once.
     */
    @Override
    public boolean add(LibraryMask libraryMask) {
        for (Shelf objectiveShelf : personalObjective.keySet()) {
            if (libraryMask.tileAt(objectiveShelf) == personalObjective.get(objectiveShelf)) {
                points = pointsMapping.get(successfulShelves++);
            }
        }
        return true;
    }

    /**
     * @return all points that have been acquired by color matches.
     */
    @Override
    public int getPoints() {
        return points;
    }
}
