package it.polimi.ingsw.model.goal;

import it.polimi.ingsw.model.bookshelf.Bookshelf;
import it.polimi.ingsw.model.bookshelf.BookshelfMask;
import it.polimi.ingsw.model.bookshelf.MockBookshelf;
import it.polimi.ingsw.model.bookshelf.MockBookshelfMask;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

class FourGroupsFourTilesGoalTest {
    private CommonGoal goal;

    @BeforeEach
    public void setUp() {
        goal = new FourGroupsFourTilesGoal();
        goal.setPointStack(List.of(2, 4, 6, 8));
    }

    @Test
    @DisplayName("Calculating points with only three groups of four tiles: goal not satisfied")
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
        Assertions.assertEquals(0, goal.getPointMasks().getSize());
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
        Assertions.assertEquals(6, goal.calculatePoints(bookshelf));
        Assertions.assertEquals(4, goal.calculatePoints(bookshelf));
        Assertions.assertEquals(2, goal.calculatePoints(bookshelf));

        BookshelfMask pointMask = new MockBookshelfMask(bookshelf, new int[][]{
                { 0, 0, 0, 0, 0 },
                { 1, 1, 1, 0, 0 },
                { 1, 0, 0, 0, 0 },
                { 1, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
        });
        Assertions.assertTrue(goal.getPointMasks().contains(pointMask));

        pointMask = new MockBookshelfMask(bookshelf, new int[][]{
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 1 },
                { 0, 0, 1, 1, 1 },
                { 0, 0, 1, 1, 1 },
        });
        Assertions.assertTrue(goal.getPointMasks().contains(pointMask));

        pointMask = new MockBookshelfMask(bookshelf, new int[][]{
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 1, 1 },
                { 0, 1, 1, 1, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
        });
        Assertions.assertTrue(goal.getPointMasks().contains(pointMask));

        pointMask = new MockBookshelfMask(bookshelf, new int[][]{
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 1, 1, 0, 0, 0 },
                { 1, 1, 0, 0, 0 },
        });
        Assertions.assertTrue(goal.getPointMasks().contains(pointMask));
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
        Assertions.assertEquals(6, goal.calculatePoints(bookshelf));
        Assertions.assertEquals(4, goal.calculatePoints(bookshelf));
        Assertions.assertEquals(2, goal.calculatePoints(bookshelf));

        int maskCounter = 0;
        BookshelfMask pointMask = new MockBookshelfMask(bookshelf, new int[][]{
                { 0, 1, 1, 0, 0 },
                { 0, 1, 1, 0, 0 },
                { 0, 1, 1, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
        });
        if (goal.getPointMasks().contains(pointMask)) {
            maskCounter++;
        }

        pointMask = new MockBookshelfMask(bookshelf, new int[][]{
                { 0, 0, 0, 1, 1 },
                { 0, 0, 0, 1, 1 },
                { 0, 0, 0, 0, 0 },
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
                { 0, 0, 0, 1, 1 },
                { 0, 0, 0, 0, 1 },
                { 0, 0, 0, 0, 1 },
                { 0, 0, 0, 0, 1 },
        });
        if (goal.getPointMasks().contains(pointMask)) {
            maskCounter++;
        }

        pointMask = new MockBookshelfMask(bookshelf, new int[][]{
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 1, 1, 1, 1, 0 },
                { 1, 0, 0, 1, 0 },
                { 1, 0, 0, 1, 0 },
        });
        if (goal.getPointMasks().contains(pointMask)) {
            maskCounter++;
        }

        pointMask = new MockBookshelfMask(bookshelf, new int[][]{
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 1, 1, 0, 0 },
                { 0, 1, 1, 0, 0 },
        });
        if (goal.getPointMasks().contains(pointMask)) {
            maskCounter++;
        }

        Assertions.assertEquals(4, maskCounter);
    }

    @Test
    @DisplayName("Calculating points with two bookshelves: first time goal is satisfied, second time not")
    void calculatePoints_twoBookshelves_firstGoalSatisfiedSecondGoalNotSatisfied() {
        Bookshelf firstBookshelf = new MockBookshelf(new int[][] {
            { 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0 },
            { 0, 0, 1, 1, 1 },
            { 0, 4, 2, 2, 1 },
            { 0, 4, 2, 2, 3 },
            { 4, 4, 3, 3, 3 }
        });

        Bookshelf secondBookshelf = new MockBookshelf(new int[][] {
            { 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0 },
            { 0, 1, 1, 1, 0 },
            { 3, 2, 2, 2, 0 },
            { 3, 3, 4, 5, 5 },
            { 1, 4, 4, 5, 4 }
        });

        Assertions.assertEquals(8, goal.calculatePoints(firstBookshelf));
        Assertions.assertEquals(6, goal.calculatePoints(firstBookshelf));

        BookshelfMask firstMask = new MockBookshelfMask(firstBookshelf, new int[][]{
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 1, 0, 0, 0 },
                { 0, 1, 0, 0, 0 },
                { 1, 1, 0, 0, 0 },
        });

        BookshelfMask secondMask = new MockBookshelfMask(firstBookshelf, new int[][]{
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 1 },
                { 0, 0, 1, 1, 1 },
        });

        BookshelfMask thirdMask = new MockBookshelfMask(firstBookshelf, new int[][]{
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 1, 1, 0 },
                { 0, 0, 1, 1, 0 },
                { 0, 0, 0, 0, 0 },
        });

        BookshelfMask fourthMask = new MockBookshelfMask(firstBookshelf, new int[][]{
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 1, 1, 1 },
                { 0, 0, 0, 0, 1 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
        });

        Assertions.assertTrue(goal.getPointMasks().contains(firstMask));
        Assertions.assertTrue(goal.getPointMasks().contains(secondMask));
        Assertions.assertTrue(goal.getPointMasks().contains(thirdMask));
        Assertions.assertTrue(goal.getPointMasks().contains(fourthMask));

        Assertions.assertEquals(0, goal.calculatePoints(secondBookshelf));
        Assertions.assertEquals(0, goal.getPointMasks().getSize());

        Assertions.assertEquals(4, goal.calculatePoints(firstBookshelf));
        Assertions.assertEquals(2, goal.calculatePoints(firstBookshelf));

        Assertions.assertTrue(goal.getPointMasks().contains(firstMask));
        Assertions.assertTrue(goal.getPointMasks().contains(secondMask));
        Assertions.assertTrue(goal.getPointMasks().contains(thirdMask));
        Assertions.assertTrue(goal.getPointMasks().contains(fourthMask));
    }
}
