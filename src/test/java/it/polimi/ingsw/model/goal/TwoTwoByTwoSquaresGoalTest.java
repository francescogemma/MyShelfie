package it.polimi.ingsw.model.goal;

import it.polimi.ingsw.model.bookshelf.Bookshelf;
import it.polimi.ingsw.model.bookshelf.MockBookshelf;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TwoTwoByTwoSquaresGoalTest {
    private Goal goal;

    @BeforeEach
    public void setUp() {
        goal = new TwoTwoByTwoSquaresGoal(4);
    }

    @Test
    @DisplayName("Calculating points when there is only one two by two square: goal not satisfied")
    void calculatingPoints_oneTwoByTwoSquare_goalNotSatisfied() {
        Bookshelf bookshelf = new MockBookshelf(new int[][]{
            { 0, 0, 0, 0, 0 },
            { 0, 0, 0, 4, 4 },
            { 0, 2, 4, 5, 4 },
            { 3, 1, 1, 6, 1 },
            { 3, 1, 1, 6, 1 },
            { 3, 6, 6, 1, 1 },
        });

        Assertions.assertEquals(0, goal.calculatePoints(bookshelf));
    }

    @Test
    @DisplayName("Calculating points when there are exactly two two by two squares " +
        "adjacent and with tiles of the same type: goal satisfied")
    void calculatePoints_twoTwoByTwoSquaresAdjacentSameType_goalSatisfied() {
        Bookshelf bookshelf = new MockBookshelf(new int[][]{
            { 0, 0, 0, 0, 0 },
            { 0, 6, 6, 0, 0 },
            { 0, 6, 1, 1, 4 },
            { 0, 6, 1, 1, 4 },
            { 2, 1, 1, 3, 4 },
            { 2, 1, 1, 3, 4 },
        });

        Assertions.assertEquals(8, goal.calculatePoints(bookshelf));
        Assertions.assertEquals(6, goal.calculatePoints(bookshelf));
        Assertions.assertEquals(4, goal.calculatePoints(bookshelf));
        Assertions.assertEquals(2, goal.calculatePoints(bookshelf));
    }

    @Test
    @DisplayName("Calculating points when there are exactly two two by two squares " +
        "not adjacent and with tiles of different type: goal satisfied")
    void calculatePoints_twoTwoByTwoSquaresNotAdjacentDifferentTypes_goalSatisfied() {
        Bookshelf bookshelf = new MockBookshelf(new int[][]{
            { 0, 0, 0, 0, 0 },
            { 0, 0, 0, 2, 2 },
            { 0, 6, 6, 2, 2 },
            { 1, 5, 5, 3, 3 },
            { 2, 1, 1, 6, 3 },
            { 3, 1, 1, 1, 4 },
        });

        Assertions.assertEquals(8, goal.calculatePoints(bookshelf));
        Assertions.assertEquals(6, goal.calculatePoints(bookshelf));
        Assertions.assertEquals(4, goal.calculatePoints(bookshelf));
        Assertions.assertEquals(2, goal.calculatePoints(bookshelf));
    }

    @Test
    @DisplayName("Calculating points with two bookshelves: first time goal is satisfied, second time is not")
    void calculatePoints_twoBookshelves_firstGoalSatisfiedSecondGoalNotSatisfied() {
        Bookshelf firstBookshelf = new MockBookshelf(new int[][] {
            { 0, 0, 0, 0, 0 },
            { 0, 0, 0, 2, 2 },
            { 0, 0, 2, 2, 2 },
            { 0, 1, 1, 2, 2 },
            { 0, 1, 1, 3, 4 },
            { 5, 6, 4, 5, 1 }
        });

        Bookshelf secondBookshelf = new MockBookshelf(new int[][] {
            { 0, 0, 0, 0, 0 },
            { 0, 3, 4, 5, 0 },
            { 2, 1, 6, 6, 5 },
            { 2, 1, 6, 6, 5 },
            { 1, 2, 2, 3, 3 },
            { 1, 1, 2, 3, 4 }
        });

        Assertions.assertEquals(8, goal.calculatePoints(firstBookshelf));
        Assertions.assertEquals(6, goal.calculatePoints(firstBookshelf));
        Assertions.assertEquals(4, goal.calculatePoints(firstBookshelf));
        Assertions.assertEquals(0, goal.calculatePoints(secondBookshelf));
        Assertions.assertEquals(2, goal.calculatePoints(firstBookshelf));
    }
}
