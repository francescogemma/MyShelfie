package it.polimi.ingsw.model.goal;

import it.polimi.ingsw.model.bookshelf.Bookshelf;
import it.polimi.ingsw.model.bookshelf.MockBookshelf;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EightTilesGoalTest {
    private CommonGoal goal;

    @BeforeEach
    public void setUp() {
        goal = new EightTilesGoal(4);
    }

    @Test
    @DisplayName("Calculating points when there are only seven tiles: goal not satisfied")
    void calculatePoints_sevenTiles_goalNotSatisfied() {
        Bookshelf bookshelf = new MockBookshelf(new int[][]{
            { 0, 0, 0, 0, 0 },
            { 0, 0, 0, 1, 0 },
            { 1, 1, 1, 1, 3 },
            { 1, 4, 4, 4, 3 },
            { 1, 4, 5, 5, 2 },
            { 3, 5, 5, 2, 6 },
        });

        Assertions.assertEquals(0, goal.calculatePoints(bookshelf));
    }

    @Test
    @DisplayName("Calculating points when there are exactly eight tiles of the same color: goal satisfied")
    void calculatePoints_eightTilesOfTypeOne_goalSatisfied() {
        Bookshelf bookshelf = new MockBookshelf(new int[][] {
            { 1, 0, 0, 0, 0 },
            { 5, 1, 1, 3, 1 },
            { 1, 3, 2, 4, 4 },
            { 1, 4, 4, 4, 1 },
            { 5, 5, 6, 6, 6 },
            { 5, 5, 6, 1, 6 },
        });

        Assertions.assertEquals(8, goal.calculatePoints(bookshelf));
        Assertions.assertEquals(6, goal.calculatePoints(bookshelf));
        Assertions.assertEquals(4, goal.calculatePoints(bookshelf));
        Assertions.assertEquals(2, goal.calculatePoints(bookshelf));
    }

    @Test
    @DisplayName("Calculating points when there are more than eight tiles of the same color: goal satisfied")
    void calculatePoints_moreThanEightTilesOfTypeOne_goalSatisfied() {
        Bookshelf bookshelf = new MockBookshelf(new int[][] {
            { 1, 6, 6, 1, 1 },
            { 5, 6, 6, 1, 1 },
            { 5, 6, 6, 3, 3 },
            { 1, 1, 1, 1, 3 },
            { 1, 2, 2, 1, 3 },
            { 1, 2, 2, 1, 3 },
        });

        Assertions.assertEquals(8, goal.calculatePoints(bookshelf));
        Assertions.assertEquals(6, goal.calculatePoints(bookshelf));
        Assertions.assertEquals(4, goal.calculatePoints(bookshelf));
        Assertions.assertEquals(2, goal.calculatePoints(bookshelf));
    }

    @Test
    @DisplayName("Calculating points with empty bookshelf: goal not satisfied")
    void calculatePoints_emptyBookshelf_goalNotSatisfied() {
        Bookshelf bookshelf = new MockBookshelf(new int[][] {
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
        });

        Assertions.assertEquals(0, goal.calculatePoints(bookshelf));
    }

    @Test
    @DisplayName("Calculating points with full bookshelf: goal satisfied")
    void calculatePoints_fullBookshelf_goalSatisfied() {
        Bookshelf bookshelf = new MockBookshelf(new int[][] {
                { 4, 4, 4, 4, 4 },
                { 4, 4, 4, 4, 4 },
                { 4, 4, 4, 4, 4 },
                { 4, 4, 4, 4, 4 },
                { 4, 4, 4, 4, 4 },
                { 4, 4, 4, 4, 4 },
        });

        Assertions.assertEquals(8, goal.calculatePoints(bookshelf));
        Assertions.assertEquals(6, goal.calculatePoints(bookshelf));
        Assertions.assertEquals(4, goal.calculatePoints(bookshelf));
        Assertions.assertEquals(2, goal.calculatePoints(bookshelf));
    }
}
