package it.polimi.ingsw.model.goal;

import it.polimi.ingsw.model.bookshelf.Bookshelf;
import it.polimi.ingsw.model.bookshelf.MockBookshelf;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ThreeColumnsGoalTest {
    private CommonGoal goal;

    @BeforeEach
    public void setUp() {
        goal = new ThreeColumnsGoal(4);
    }

    @Test
    @DisplayName("Calculating points when there are only two columns of three: goal not satisfied")
    void calculatePoints_threeRows_goalNotSatisfied() {
        Bookshelf bookshelf = new MockBookshelf(new int[][]{
                { 0, 0, 3, 2, 1 },
                { 0, 0, 3, 2, 2 },
                { 0, 0, 6, 2, 3 },
                { 0, 0, 3, 2, 1 },
                { 0, 0, 3, 1, 1 },
                { 0, 0, 3, 2, 4 },
        });

        Assertions.assertEquals(0, goal.calculatePoints(bookshelf));
    }

    @Test
    @DisplayName("Calculating points when there are exactly three columns: goal satisfied")
    void calculatePoints_fourRows_goalSatisfied() {
        Bookshelf bookshelf = new MockBookshelf(new int[][] {
                { 0, 1, 4, 3, 0 },
                { 0, 2, 4, 2, 0 },
                { 0, 1, 1, 3, 0 },
                { 0, 6, 4, 3, 0 },
                { 1, 1, 4, 3, 1 },
                { 3, 1, 4, 3, 1 },
        });

        Assertions.assertEquals(8, goal.calculatePoints(bookshelf));
        Assertions.assertEquals(6, goal.calculatePoints(bookshelf));
        Assertions.assertEquals(4, goal.calculatePoints(bookshelf));
        Assertions.assertEquals(2, goal.calculatePoints(bookshelf));
    }

    @Test
    @DisplayName("Calculating points when there are more than three columns: goal satisfied")
    void calculatePoints_moreThanFourRows_goalSatisfied() {
        Bookshelf bookshelf = new MockBookshelf(new int[][] {
                { 1, 2, 3, 6, 0 },
                { 1, 2, 3, 6, 0 },
                { 1, 2, 3, 6, 0 },
                { 1, 2, 3, 6, 0 },
                { 1, 2, 3, 6, 0 },
                { 1, 2, 3, 6, 0 },
        });

        Assertions.assertEquals(8, goal.calculatePoints(bookshelf));
        Assertions.assertEquals(6, goal.calculatePoints(bookshelf));
        Assertions.assertEquals(4, goal.calculatePoints(bookshelf));
        Assertions.assertEquals(2, goal.calculatePoints(bookshelf));
    }

    @Test
    @DisplayName("Calculating points with a full columns bookshelf: goal satisfied")
    void calculatePoints_fullBookshelf_goalNotSatisfied() {
        Bookshelf bookshelf = new MockBookshelf(new int[][] {
                { 1, 2, 3, 4, 5 },
                { 1, 2, 3, 4, 5 },
                { 1, 2, 3, 4, 5 },
                { 1, 2, 3, 4, 5 },
                { 1, 2, 3, 4, 5 },
                { 1, 2, 3, 4, 5 },
        });

        Assertions.assertEquals(8, goal.calculatePoints(bookshelf));
        Assertions.assertEquals(6, goal.calculatePoints(bookshelf));
        Assertions.assertEquals(4, goal.calculatePoints(bookshelf));
        Assertions.assertEquals(2, goal.calculatePoints(bookshelf));
    }

    @Test
    @DisplayName("goal satisfied and then not satisfied: points should return to 0")
    void calculatePoints_goalSatisfiedThenNotSatisfied_pointsReturnToZero() {
        Bookshelf bookshelf = new MockBookshelf(new int[][] {
                { 0, 1, 4, 3, 0 },
                { 0, 2, 4, 2, 0 },
                { 0, 1, 1, 3, 0 },
                { 0, 6, 4, 3, 0 },
                { 1, 1, 4, 3, 1 },
                { 3, 1, 4, 3, 1 },
        });

        Assertions.assertEquals(8, goal.calculatePoints(bookshelf));
        Assertions.assertEquals(6, goal.calculatePoints(bookshelf));

        bookshelf = new MockBookshelf(new int[][]{
                { 0, 0, 3, 2, 1 },
                { 0, 0, 3, 2, 2 },
                { 0, 0, 6, 2, 3 },
                { 0, 0, 3, 2, 1 },
                { 0, 0, 3, 1, 1 },
                { 0, 0, 3, 2, 4 },
        });

        Assertions.assertEquals(0, goal.calculatePoints(bookshelf));

        bookshelf = new MockBookshelf(new int[][] {
                { 1, 2, 3, 4, 5 },
                { 1, 2, 3, 4, 5 },
                { 1, 2, 3, 4, 5 },
                { 1, 2, 3, 4, 5 },
                { 1, 2, 3, 4, 5 },
                { 1, 2, 3, 4, 5 },
        });

        Assertions.assertEquals(4, goal.calculatePoints(bookshelf));
        Assertions.assertEquals(2, goal.calculatePoints(bookshelf));
    }
}
