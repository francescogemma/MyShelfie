package it.polimi.ingsw.model.goal;

import it.polimi.ingsw.model.bookshelf.Bookshelf;
import it.polimi.ingsw.model.bookshelf.MockBookshelf;
import jdk.jfr.Description;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ColumnDifferentGoalTest {
    ColumnDifferentGoal goal;

    @BeforeEach
    void setUp () {
        goal = new ColumnDifferentGoal(4);
    }

    @Test
    @Description("Check that if there are 4 tiles per column the function returns 0 points")
    void calculatePoints_FourDifferentSingle_correctOutput () {
        Bookshelf bookshelf = new MockBookshelf(new int[][]{
                { 0, 0, 3, 2, 1 },
                { 1, 1, 1, 4, 1 },
                { 2, 2, 4, 2, 2 },
                { 4, 2, 5, 2, 1 },
                { 1, 4, 5, 6, 1 },
                { 3, 5, 4, 1, 6 },
        });

        Assertions.assertEquals(0, goal.calculatePoints(bookshelf));
    }

    @Test
    @Description("If there is only one column made of all different tiles it returns 0 points")
    void calculatePoints_FiveDifferentSingle_correctOutput () {
        Bookshelf bookshelf = new MockBookshelf(new int[][]{
                { 0, 0, 3, 2, 1 },
                { 1, 1, 1, 4, 1 },
                { 2, 2, 4, 2, 2 },
                { 4, 3, 5, 2, 1 },
                { 1, 4, 5, 6, 1 },
                { 3, 5, 4, 1, 6 },
        });

        Assertions.assertEquals(0, goal.calculatePoints(bookshelf));
    }

    @Test
    @Description("If there are two columns made of all different tiles, the function returns 8 points.")
    void calculatePoints_FiveDifferentDouble_correctOutput () {
        Bookshelf bookshelf = new MockBookshelf(new int[][]{
                { 6, 6, 0, 0, 0 },
                { 1, 1, 1, 1, 1 },
                { 2, 2, 2, 2, 2 },
                { 3, 3, 6, 3, 3 },
                { 4, 4, 4, 2, 4 },
                { 5, 5, 5, 5, 1 },
        });

        Assertions.assertEquals(8, goal.calculatePoints(bookshelf));
        Assertions.assertEquals(6, goal.calculatePoints(bookshelf));
        Assertions.assertEquals(4, goal.calculatePoints(bookshelf));
        Assertions.assertEquals(2, goal.calculatePoints(bookshelf));
    }

    @Test
    void calculatePoints_FiveDifferentDoubleWithEmpty_correctOutput () {
        Bookshelf bookshelf = new MockBookshelf(new int[][]{
                { 0, 6, 0, 0, 0 },
                { 1, 1, 1, 1, 1 },
                { 2, 2, 2, 2, 2 },
                { 3, 3, 6, 3, 3 },
                { 4, 4, 4, 5, 4 },
                { 5, 5, 5, 5, 1 },
        });

        Assertions.assertEquals(0, goal.calculatePoints(bookshelf));
    }
}
