package it.polimi.ingsw.model.goal;

import it.polimi.ingsw.model.bookshelf.Bookshelf;
import it.polimi.ingsw.model.bookshelf.BookshelfMask;
import it.polimi.ingsw.model.bookshelf.MockBookshelf;
import it.polimi.ingsw.model.bookshelf.MockBookshelfMask;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class XPatternGoalTest {
    XPatternGoal goal;

    @BeforeEach
    void setUp () {
        goal = new XPatternGoal(4);
    }

    @Test
    void calculatePoints_OneX_correctOutput () {
        Bookshelf bookshelf = new MockBookshelf(new int[][]{
                { 0, 0, 3, 2, 1 },
                { 1, 1, 1, 4, 1 },
                { 2, 2, 2, 2, 2 },
                { 4, 3, 2, 2, 1 },
                { 1, 2, 5, 2, 1 },
                { 3, 5, 5, 1, 6 },
        });

        BookshelfMask pointMask = new MockBookshelfMask(bookshelf, new int[][]{
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 1, 0, 1, 0 },
                { 0, 0, 1, 0, 0 },
                { 0, 1, 0, 1, 0 },
                { 0, 0, 0, 0, 0 },
        });

        Assertions.assertEquals(8, goal.calculatePoints(bookshelf));
        Assertions.assertEquals(1, goal.getPointMasks().getSize());
        Assertions.assertEquals(pointMask, goal.getPointMasks().getBookshelfMasks().get(0));

        Assertions.assertEquals(6, goal.calculatePoints(bookshelf));
        Assertions.assertEquals(1, goal.getPointMasks().getSize());
        Assertions.assertEquals(pointMask, goal.getPointMasks().getBookshelfMasks().get(0));

        Assertions.assertEquals(4, goal.calculatePoints(bookshelf));
        Assertions.assertEquals(1, goal.getPointMasks().getSize());
        Assertions.assertEquals(pointMask, goal.getPointMasks().getBookshelfMasks().get(0));

        Assertions.assertEquals(2, goal.calculatePoints(bookshelf));
        Assertions.assertEquals(1, goal.getPointMasks().getSize());
        Assertions.assertEquals(pointMask, goal.getPointMasks().getBookshelfMasks().get(0));
    }

    @Test
    void calculatePoints_TwoXIntersect_correctOutput () {
        Bookshelf bookshelf = new MockBookshelf(new int[][]{
                { 0, 0, 3, 2, 1 },
                { 1, 3, 2, 3, 1 },
                { 2, 2, 3, 2, 2 },
                { 4, 3, 5, 3, 1 },
                { 1, 4, 3, 6, 1 },
                { 3, 3, 5, 3, 6 },
        });

        BookshelfMask pointMask = new MockBookshelfMask(bookshelf, new int[][]{
                { 0, 0, 0, 0, 0 },
                { 0, 1, 0, 1, 0 },
                { 0, 0, 1, 0, 0 },
                { 0, 1, 0, 1, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
        });

        Assertions.assertEquals(8, goal.calculatePoints(bookshelf));
        Assertions.assertEquals(1, goal.getPointMasks().getSize());
        Assertions.assertEquals(pointMask, goal.getPointMasks().getBookshelfMasks().get(0));

        Assertions.assertEquals(6, goal.calculatePoints(bookshelf));
        Assertions.assertEquals(1, goal.getPointMasks().getSize());
        Assertions.assertEquals(pointMask, goal.getPointMasks().getBookshelfMasks().get(0));

        Assertions.assertEquals(4, goal.calculatePoints(bookshelf));
        Assertions.assertEquals(1, goal.getPointMasks().getSize());
        Assertions.assertEquals(pointMask, goal.getPointMasks().getBookshelfMasks().get(0));

        Assertions.assertEquals(2, goal.calculatePoints(bookshelf));
        Assertions.assertEquals(1, goal.getPointMasks().getSize());
        Assertions.assertEquals(pointMask, goal.getPointMasks().getBookshelfMasks().get(0));
    }

    @Test
    void calculatePoints__XPatternWithoutTopLeft () {
        Bookshelf bookshelf = new MockBookshelf(new int[][]{
                { 0, 0, 3, 2, 1 },
                { 1, 1, 1, 4, 1 },
                { 2, 4, 2, 2, 2 },
                { 4, 3, 2, 2, 1 },
                { 1, 2, 5, 2, 1 },
                { 3, 5, 5, 1, 6 },
        });

        Assertions.assertEquals(0, goal.calculatePoints(bookshelf));
        Assertions.assertEquals(0, goal.getPointMasks().getSize());
    }
}
