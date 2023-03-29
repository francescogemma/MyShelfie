package it.polimi.ingsw.model.goal;

import it.polimi.ingsw.model.bookshelf.Bookshelf;
import it.polimi.ingsw.model.bookshelf.MockBookshelf;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SixGroupsTwoTilesGoalTest {
    private CommonGoal goal;

    @BeforeEach
    public void setUp() {
        goal = new SixGroupsTwoTilesGoal(4);
    }

    @Test
    @DisplayName("Calculating points when there are only five groups of two: goal not satisfied")
    void calculatePoints_fiveGroupsOfTwo_goalNotSatisfied() {
        Bookshelf bookshelf = new MockBookshelf(new int[][]{
            { 0, 0, 0, 0, 0 },
            { 0, 0, 6, 0, 0 },
            { 0, 1, 4, 5, 5 },
            { 2, 1, 4, 6, 1 },
            { 2, 3, 3, 2, 3 },
            { 1, 2, 3, 1, 2 },
        });

        Assertions.assertEquals(0, goal.calculatePoints(bookshelf));
    }

    @Test
    @DisplayName("Calculating points when there are exactly six groups of two: goal satisfied")
    void calculatePoints_sixGroupsOfTwo_goalSatisfied() {
        Bookshelf bookshelf = new MockBookshelf(new int[][]{
            { 0, 0, 0, 0, 0 },
            { 0, 0, 5, 0, 5 },
            { 0, 6, 4, 1, 5 },
            { 1, 6, 3, 3, 4 },
            { 3, 5, 1, 2, 3 },
            { 2, 2, 1, 2, 1 },
        });

        Assertions.assertEquals(8, goal.calculatePoints(bookshelf));
        Assertions.assertEquals(6, goal.calculatePoints(bookshelf));
        Assertions.assertEquals(4, goal.calculatePoints(bookshelf));
        Assertions.assertEquals(2, goal.calculatePoints(bookshelf));
    }

    @Test
    @DisplayName("Calculating points when there are more than six groups of more than two: goal satisfied")
    void calculatePoints_moreThanSixGroupsOfMoreThanTwo_goalSatisfied() {
        Bookshelf bookshelf = new MockBookshelf(new int[][]{
            { 0, 0, 0, 0, 0 },
            { 0, 6, 0, 0, 0 },
            { 6, 6, 5, 5, 0 },
            { 6, 2, 2, 3, 1 },
            { 6, 1, 2, 3, 1 },
            { 1, 1, 3, 4, 4 },
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
            { 0, 0, 0, 0, 0 },
            { 6, 6, 5, 5, 0 },
            { 1, 3, 3, 3, 0 },
            { 1, 3, 2, 3, 4 },
            { 1, 2, 2, 4, 4 }
        });

        Bookshelf secondBookshelf = new MockBookshelf(new int[][] {
            { 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0 },
            { 0, 5, 6, 5, 4 },
            { 4, 6, 5, 4, 6 },
            { 1, 3, 3, 3, 5 },
            { 1, 1, 2, 2, 4 }
        });

        Assertions.assertEquals(8, goal.calculatePoints(firstBookshelf));
        Assertions.assertEquals(6, goal.calculatePoints(firstBookshelf));
        Assertions.assertEquals(0, goal.calculatePoints(secondBookshelf));
        Assertions.assertEquals(4, goal.calculatePoints(firstBookshelf));
        Assertions.assertEquals(2, goal.calculatePoints(firstBookshelf));
    }
}
