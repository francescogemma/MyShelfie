package it.polimi.ingsw.model.goal;

import it.polimi.ingsw.model.bookshelf.Bookshelf;
import it.polimi.ingsw.model.bookshelf.MockBookshelf;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CornersGoalTest {
    private CommonGoal goal;

    @BeforeEach
    public void setUp() {
        goal = new CornersGoal(4);
    }

    @Test
    @DisplayName("Calculating points when goal is satisfied")
    void calculatePoints_fourEqual_goalSatisfied() {
        Bookshelf bookshelf = new MockBookshelf(new int[][]{
            { 5, 0, 0, 0, 5 },
            { 1, 1, 1, 1, 1 },
            { 2, 2, 2, 2, 2 },
            { 3, 3, 3, 3, 3 },
            { 4, 4, 4, 4, 4 },
            { 5, 5, 5, 5, 5 },
        });

        Assertions.assertEquals(8, goal.calculatePoints(bookshelf));
    }

    @Test
    @DisplayName("Calculating points when one tile is empty: goal not satisfied")
    void calculatePoints_oneEmpty_goalNotSatisfied() {
        Bookshelf bookshelf = new MockBookshelf(new int[][]{
            { 0, 0, 0, 0, 5 },
            { 1, 1, 1, 1, 1 },
            { 2, 2, 2, 2, 2 },
            { 3, 1, 6, 1, 6 },
            { 4, 4, 2, 4, 4 },
            { 5, 5, 5, 5, 5 },
        });

        Assertions.assertEquals(0, goal.calculatePoints(bookshelf));
    }

    @Test
    @DisplayName("Calculating points when several tiles are different: goal not satisfied")
    void calculatePoints_severalDifferent_goalNotSatisfied() {
        Bookshelf bookshelf = new MockBookshelf(new int[][] {
            { 1, 0, 1, 2, 3 },
            { 1, 1, 3, 5, 2 },
            { 2, 3, 2, 3, 1 },
            { 1, 1, 2, 3, 4 },
            { 5, 6, 2, 2, 1 },
            { 1, 1, 2, 1, 2 },
        });

        Assertions.assertEquals(0, goal.calculatePoints(bookshelf));
    }
}
