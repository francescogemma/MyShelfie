package it.polimi.ingsw.model.goal;

import it.polimi.ingsw.model.bookshelf.Bookshelf;
import it.polimi.ingsw.model.bookshelf.MockBookshelf;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StairGoalTest {
    StairGoal goal;

    @BeforeEach
    void setUp () {
        goal = new StairGoal(4);
    }

    @Test
    void calculatePoints_stairFirstRow_correctOutput() {
        Bookshelf bookshelf = new MockBookshelf(new int[][]{
                { 5, 0, 0, 0, 0 },
                { 1, 1, 0, 0, 0 },
                { 2, 2, 4, 0, 0 },
                { 4, 3, 5, 2, 0 },
                { 1, 4, 5, 6, 1 },
                { 3, 5, 5, 1, 6 },
        });

        Assertions.assertEquals(8, goal.calculatePoints(bookshelf));
    }

    @Test
    void calculatePoints_stairSecondRow_correctOutput () {
        Bookshelf bookshelf = new MockBookshelf(new int[][]{
                { 0, 0, 0, 0, 0 },
                { 1, 0, 0, 0, 0 },
                { 2, 2, 0, 0, 0 },
                { 4, 3, 5, 0, 0 },
                { 1, 4, 5, 6, 0 },
                { 3, 5, 5, 1, 6 },
        });

        Assertions.assertEquals(8, goal.calculatePoints(bookshelf));
    }

    @Test
    void calculatePoints_stairSecondRowBroken_correctOutput () {
        Bookshelf bookshelf = new MockBookshelf(new int[][]{
                { 0, 0, 0, 0, 0 },
                { 1, 6, 0, 0, 0 },
                { 2, 2, 0, 0, 0 },
                { 4, 3, 5, 0, 0 },
                { 1, 4, 5, 6, 0 },
                { 3, 5, 5, 1, 6 },
        });

        Assertions.assertEquals(0, goal.calculatePoints(bookshelf));
    }
}
