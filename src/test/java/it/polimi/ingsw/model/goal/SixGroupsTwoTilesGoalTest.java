package it.polimi.ingsw.model.goal;

import it.polimi.ingsw.model.bookshelf.Bookshelf;
import it.polimi.ingsw.model.bookshelf.BookshelfMask;
import it.polimi.ingsw.model.bookshelf.MockBookshelf;
import it.polimi.ingsw.model.bookshelf.MockBookshelfMask;
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
        Assertions.assertEquals(0, goal.getPointMasks().getSize());
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

        BookshelfMask pointMask = new MockBookshelfMask(bookshelf, new int[][]{
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 1 },
                { 0, 0, 0, 0, 1 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
        });
        Assertions.assertTrue(goal.getPointMasks().contains(pointMask));

        pointMask = new MockBookshelfMask(bookshelf, new int[][]{
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 1, 0 },
                { 0, 0, 0, 1, 0 },
        });
        Assertions.assertTrue(goal.getPointMasks().contains(pointMask));

        pointMask = new MockBookshelfMask(bookshelf, new int[][]{
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 1, 0, 0 },
                { 0, 0, 1, 0, 0 },
        });
        Assertions.assertTrue(goal.getPointMasks().contains(pointMask));

        pointMask = new MockBookshelfMask(bookshelf, new int[][]{
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 1, 1, 0, 0, 0 },
        });
        Assertions.assertTrue(goal.getPointMasks().contains(pointMask));

        pointMask = new MockBookshelfMask(bookshelf, new int[][]{
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 1, 1, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
        });
        Assertions.assertTrue(goal.getPointMasks().contains(pointMask));

        pointMask = new MockBookshelfMask(bookshelf, new int[][]{
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 1, 0, 0, 0 },
                { 0, 1, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
        });

        Assertions.assertTrue(goal.getPointMasks().contains(pointMask));
        Assertions.assertEquals(6, goal.getPointMasks().getSize());
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

        int maskCounter = 0;
        BookshelfMask pointMask = new MockBookshelfMask(bookshelf, new int[][]{
                { 0, 0, 0, 0, 0 },
                { 0, 1, 0, 0, 0 },
                { 1, 1, 0, 0, 0 },
                { 1, 0, 0, 0, 0 },
                { 1, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
        });
        if (goal.getPointMasks().contains(pointMask)) {
            maskCounter++;
        }
        pointMask = new MockBookshelfMask(bookshelf, new int[][]{
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 1, 1, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
        });
        if (goal.getPointMasks().contains(pointMask)) {
            maskCounter++;
        }
        pointMask = new MockBookshelfMask(bookshelf, new int[][]{
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 1, 1, 0, 0 },
                { 0, 0, 1, 0, 0 },
                { 0, 0, 0, 0, 0 },
        });
        if (goal.getPointMasks().contains(pointMask)) {
            maskCounter++;
        }
        pointMask = new MockBookshelfMask(bookshelf, new int[][]{
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 1, 0, 0, 0 },
                { 1, 1, 0, 0, 0 },
        });
        if (goal.getPointMasks().contains(pointMask)) {
            maskCounter++;
        }
        pointMask = new MockBookshelfMask(bookshelf, new int[][]{
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 1 },
                { 0, 0, 0, 0, 1 },
                { 0, 0, 0, 0, 0 },
        });
        if (goal.getPointMasks().contains(pointMask)) {
            maskCounter++;
        }
        pointMask = new MockBookshelfMask(bookshelf, new int[][]{
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 1, 0 },
                { 0, 0, 0, 1, 0 },
                { 0, 0, 0, 0, 0 },
        });
        if (goal.getPointMasks().contains(pointMask)) {
            maskCounter++;
        }
        pointMask = new MockBookshelfMask(bookshelf, new int[][]{
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 1, 1 },
        });
        if (goal.getPointMasks().contains(pointMask)) {
            maskCounter++;
        }

        Assertions.assertEquals(6, goal.getPointMasks().getSize());
        Assertions.assertEquals(6, maskCounter);
    }

    @Test
    @DisplayName("Calculating points with two bookshelves: goal is satisfied first time, not second time")
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

        BookshelfMask firstMask = new MockBookshelfMask(firstBookshelf, new int[][]{
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 1, 1, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
        });

        BookshelfMask secondMask = new MockBookshelfMask(firstBookshelf, new int[][]{
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 1, 0, 0, 0, 0 },
                { 1, 0, 0, 0, 0 },
                { 1, 0, 0, 0, 0 },
        });

        BookshelfMask thirdMask = new MockBookshelfMask(firstBookshelf, new int[][]{
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 1, 1, 1, 0 },
                { 0, 1, 0, 1, 0 },
                { 0, 0, 0, 0, 0 },
        });

        BookshelfMask fourthMask = new MockBookshelfMask(firstBookshelf, new int[][]{
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 1, 1, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
        });

        BookshelfMask fifthMask = new MockBookshelfMask(firstBookshelf, new int[][]{
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 1 },
                { 0, 0, 0, 1, 1 },
        });

        BookshelfMask sixthMask = new MockBookshelfMask(firstBookshelf, new int[][]{
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 1, 0, 0 },
                { 0, 1, 1, 0, 0 },
        });

        Assertions.assertEquals(8, goal.calculatePoints(firstBookshelf));
        Assertions.assertEquals(6, goal.calculatePoints(firstBookshelf));
        Assertions.assertEquals(6, goal.getPointMasks().getSize());

        Assertions.assertTrue(goal.getPointMasks().contains(firstMask));
        Assertions.assertTrue(goal.getPointMasks().contains(secondMask));
        Assertions.assertTrue(goal.getPointMasks().contains(thirdMask));
        Assertions.assertTrue(goal.getPointMasks().contains(fourthMask));
        Assertions.assertTrue(goal.getPointMasks().contains(fifthMask));
        Assertions.assertTrue(goal.getPointMasks().contains(sixthMask));

        Assertions.assertEquals(0, goal.calculatePoints(secondBookshelf));
        Assertions.assertEquals(0, goal.getPointMasks().getSize());

        Assertions.assertEquals(4, goal.calculatePoints(firstBookshelf));
        Assertions.assertEquals(2, goal.calculatePoints(firstBookshelf));
        Assertions.assertEquals(6, goal.getPointMasks().getSize());

        Assertions.assertTrue(goal.getPointMasks().contains(firstMask));
        Assertions.assertTrue(goal.getPointMasks().contains(secondMask));
        Assertions.assertTrue(goal.getPointMasks().contains(thirdMask));
        Assertions.assertTrue(goal.getPointMasks().contains(fourthMask));
        Assertions.assertTrue(goal.getPointMasks().contains(fifthMask));
        Assertions.assertTrue(goal.getPointMasks().contains(sixthMask));
    }
}
