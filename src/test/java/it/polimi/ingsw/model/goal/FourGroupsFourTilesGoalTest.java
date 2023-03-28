package it.polimi.ingsw.model.goal;

import it.polimi.ingsw.model.bookshelf.Bookshelf;
import it.polimi.ingsw.model.bookshelf.MockBookshelf;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FourGroupsFourTilesGoalTest {
    private CommonGoal goal;

    @BeforeEach
    public void setUp() {
        goal = new FourGroupsFourTilesGoal(4);
    }

    @Test
    @DisplayName("Calculating points when there are only three groups of four: goal not satisfied")
    void calculatePoints_threeGroupsOfFour_goalNotSatisfied() {
        Bookshelf bookshelf = new MockBookshelf(new int[][]{
            { 0, 0, 0, 0, 0 },
            { 0, 1, 1, 1, 0 },
            { 2, 2, 2, 2, 3 },
            { 1, 4, 4, 4, 3 },
            { 1, 4, 5, 5, 1 },
            { 3, 5, 5, 1, 6 },
        });

        Assertions.assertEquals(0, goal.calculatePoints(bookshelf));
    }

    @Test
    @DisplayName("Calculating points when there are exactly four groups of more than four tiles: goal satisfied")
    void calculatePoints_fourGroupsOfFour_goalSatisfied() {
        Bookshelf bookshelf = new MockBookshelf(new int[][] {
            { 0, 0, 0, 0, 0 },
            { 1, 1, 1, 3, 6 },
            { 1, 3, 2, 4, 4 },
            { 1, 4, 4, 4, 6 },
            { 5, 5, 6, 6, 6 },
            { 5, 5, 6, 6, 6 },
        });

        Assertions.assertEquals(8, goal.calculatePoints(bookshelf));
    }

    @Test
    @DisplayName("Calculating points when there are more than four groups of more than four tiles: goal satisfied")
    void calculatePoints_moreThanFourGroupsOfFour_goalSatisfied() {
        Bookshelf bookshelf = new MockBookshelf(new int[][] {
            { 0, 6, 6, 1, 1 },
            { 0, 6, 6, 1, 1 },
            { 0, 6, 6, 3, 3 },
            { 1, 1, 1, 1, 3 },
            { 1, 2, 2, 1, 3 },
            { 1, 2, 2, 1, 3 },
        });

        Assertions.assertEquals(8, goal.calculatePoints(bookshelf));
    }
}
