package it.polimi.ingsw.model.goal;

import it.polimi.ingsw.model.bookshelf.Bookshelf;
import it.polimi.ingsw.model.bookshelf.MockBookshelf;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DiagonalGoalTest {
    private CommonGoal goal;

    @BeforeEach
    public void setUp() {
        goal = new DiagonalGoal(4);
    }

    @Test
    @DisplayName("Calculating points when there are no diagonals: goal not satisfied")
    void calculatePoints_noDiagonals_goalNotSatisfied() {
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
    @DisplayName("Calculating points when there is a main diagonal (first): goal satisfied")
    void calculatePoints_firstMainDiagonal_goalSatisfied() {
        Bookshelf bookshelf;

        bookshelf = new MockBookshelf(new int[][] {
                    { 2, 0, 0, 0, 0 },
                    { 6, 2, 1, 1, 0 },
                    { 2, 2, 2, 2, 3 },
                    { 1, 4, 4, 2, 3 },
                    { 1, 4, 5, 5, 2 },
                    { 3, 5, 5, 1, 6 },
        });

        Assertions.assertEquals(8, goal.calculatePoints(bookshelf));
        Assertions.assertEquals(6, goal.calculatePoints(bookshelf));
        Assertions.assertEquals(4, goal.calculatePoints(bookshelf));
        Assertions.assertEquals(2, goal.calculatePoints(bookshelf));
    }

    @Test
    @DisplayName("Calculating points when there is a main diagonal (second): goal satisfied")
    void calculatePoints_secondMainDiagonal_goalSatisfied() {
        Bookshelf bookshelf;

        bookshelf = new MockBookshelf(new int[][] {
                    { 0, 0, 0, 0, 0 },
                    { 4, 1, 1, 1, 0 },
                    { 2, 4, 2, 2, 3 },
                    { 1, 4, 4, 5, 3 },
                    { 1, 4, 5, 4, 2 },
                    { 3, 5, 5, 1, 4 },
        });

        Assertions.assertEquals(8, goal.calculatePoints(bookshelf));
        Assertions.assertEquals(6, goal.calculatePoints(bookshelf));
        Assertions.assertEquals(4, goal.calculatePoints(bookshelf));
        Assertions.assertEquals(2, goal.calculatePoints(bookshelf));
    }

    @Test
    @DisplayName("Calculating points when there is a second diagonal (first): goal satisfied")
    void calculatePoints_firstSecondDiagonal_goalSatisfied() {
        Bookshelf bookshelf;

        bookshelf = new MockBookshelf(new int[][] {
                    { 4, 0, 0, 0, 1 },
                    { 6, 2, 1, 1, 6 },
                    { 2, 2, 1, 2, 3 },
                    { 1, 1, 4, 2, 3 },
                    { 1, 4, 5, 5, 2 },
                    { 3, 5, 5, 1, 6 },
        });

        Assertions.assertEquals(8, goal.calculatePoints(bookshelf));
        Assertions.assertEquals(6, goal.calculatePoints(bookshelf));
        Assertions.assertEquals(4, goal.calculatePoints(bookshelf));
        Assertions.assertEquals(2, goal.calculatePoints(bookshelf));
    }

    @Test
    @DisplayName("Calculating points when there is a second diagonal (second): goal satisfied")
    void calculatePoints_secondSecondDiagonal_goalSatisfied() {
        Bookshelf bookshelf;

        bookshelf = new MockBookshelf(new int[][] {
                    { 0, 0, 0, 0, 0 },
                    { 4, 1, 1, 1, 6 },
                    { 2, 4, 2, 6, 3 },
                    { 1, 4, 6, 5, 3 },
                    { 1, 6, 5, 4, 2 },
                    { 6, 5, 5, 1, 4 },
        });

        Assertions.assertEquals(8, goal.calculatePoints(bookshelf));
        Assertions.assertEquals(6, goal.calculatePoints(bookshelf));
        Assertions.assertEquals(4, goal.calculatePoints(bookshelf));
        Assertions.assertEquals(2, goal.calculatePoints(bookshelf));
    }

    @Test
    @DisplayName("Calculating points when there are both diagonals: goal satisfied")
    void calculatePoints_bothDiagonals_goalSatisfied() {
        Bookshelf bookshelf = new MockBookshelf(new int[][] {
                { 4, 0, 0, 0, 2 },
                { 6, 4, 1, 2, 1 },
                { 2, 2, 4, 1, 3 },
                { 1, 1, 1, 4, 3 },
                { 1, 1, 5, 5, 4 },
                { 1, 5, 5, 1, 4 },
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
                { 4, 0, 0, 0, 2 },
                { 6, 4, 1, 2, 1 },
                { 2, 2, 4, 1, 3 },
                { 1, 1, 1, 4, 3 },
                { 1, 1, 5, 5, 4 },
                { 1, 5, 5, 1, 4 },
        });

        Assertions.assertEquals(8, goal.calculatePoints(bookshelf));
        Assertions.assertEquals(6, goal.calculatePoints(bookshelf));

        bookshelf = new MockBookshelf(new int[][]{
                { 0, 0, 0, 0, 0 },
                { 0, 1, 1, 1, 0 },
                { 2, 2, 2, 2, 3 },
                { 1, 4, 4, 4, 3 },
                { 1, 4, 5, 5, 1 },
                { 3, 5, 5, 1, 6 },
        });

        Assertions.assertEquals(0, goal.calculatePoints(bookshelf));

        bookshelf = new MockBookshelf(new int[][] {
                { 4, 0, 0, 0, 2 },
                { 6, 4, 1, 2, 1 },
                { 2, 2, 4, 1, 3 },
                { 1, 1, 1, 4, 3 },
                { 1, 1, 5, 5, 4 },
                { 1, 5, 5, 1, 4 },
        });

        Assertions.assertEquals(4, goal.calculatePoints(bookshelf));
        Assertions.assertEquals(2, goal.calculatePoints(bookshelf));
    }
}
