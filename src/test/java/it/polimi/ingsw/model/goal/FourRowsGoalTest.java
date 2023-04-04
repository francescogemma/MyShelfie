package it.polimi.ingsw.model.goal;

import it.polimi.ingsw.model.bookshelf.Bookshelf;
import it.polimi.ingsw.model.bookshelf.BookshelfMask;
import it.polimi.ingsw.model.bookshelf.MockBookshelf;
import it.polimi.ingsw.model.bookshelf.MockBookshelfMask;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FourRowsGoalTest {
    private CommonGoal goal;

    @BeforeEach
    public void setUp() {
        goal = new FourRowsGoal(4);
    }

    @Test
    @DisplayName("Calculating points when there are only three rows of four: goal not satisfied")
    void calculatePoints_threeRows_goalNotSatisfied() {
        Bookshelf bookshelf = new MockBookshelf(new int[][]{
            { 0, 0, 3, 2, 1 },
            { 1, 1, 1, 4, 1 },
            { 2, 2, 4, 2, 2 },
            { 4, 4, 5, 2, 4 },
            { 1, 4, 5, 6, 1 },
            { 3, 5, 5, 1, 6 },
        });

        Assertions.assertEquals(0, goal.calculatePoints(bookshelf));
        Assertions.assertEquals(0, goal.getPointMasks().getSize());
    }

    @Test
    @DisplayName("Calculating points when there are exactly four rows: goal satisfied")
    void calculatePoints_fourRows_goalSatisfied() {
        Bookshelf bookshelf = new MockBookshelf(new int[][] {
            { 0, 0, 0, 0, 0 },
            { 1, 3, 1, 2, 1 },
            { 2, 2, 2, 2, 2 },
            { 3, 3, 6, 3, 3 },
            { 4, 4, 4, 4, 4 },
            { 5, 4, 3, 2, 1 },
        });

        BookshelfMask pointMask1 = new MockBookshelfMask(bookshelf, new int[][]{
                { 0, 0, 0, 0, 0 },
                { 1, 1, 1, 1, 1 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
        });
        BookshelfMask pointMask2 = new MockBookshelfMask(bookshelf, new int[][]{
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 1, 1, 1, 1, 1 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
        });
        BookshelfMask pointMask3 = new MockBookshelfMask(bookshelf, new int[][]{
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 1, 1, 1, 1, 1 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
        });
        BookshelfMask pointMask4 = new MockBookshelfMask(bookshelf, new int[][]{
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 1, 1, 1, 1, 1 },
                { 0, 0, 0, 0, 0 },
        });

        Assertions.assertEquals(8, goal.calculatePoints(bookshelf));
        Assertions.assertEquals(4, goal.getPointMasks().getSize());
        Assertions.assertEquals(pointMask1, goal.getPointMasks().getBookshelfMasks().get(0));
        Assertions.assertEquals(pointMask2, goal.getPointMasks().getBookshelfMasks().get(1));
        Assertions.assertEquals(pointMask3, goal.getPointMasks().getBookshelfMasks().get(2));
        Assertions.assertEquals(pointMask4, goal.getPointMasks().getBookshelfMasks().get(3));


        Assertions.assertEquals(6, goal.calculatePoints(bookshelf));
        Assertions.assertEquals(4, goal.getPointMasks().getSize());
        Assertions.assertEquals(pointMask1, goal.getPointMasks().getBookshelfMasks().get(0));
        Assertions.assertEquals(pointMask2, goal.getPointMasks().getBookshelfMasks().get(1));
        Assertions.assertEquals(pointMask3, goal.getPointMasks().getBookshelfMasks().get(2));
        Assertions.assertEquals(pointMask4, goal.getPointMasks().getBookshelfMasks().get(3));

        Assertions.assertEquals(4, goal.calculatePoints(bookshelf));
        Assertions.assertEquals(4, goal.getPointMasks().getSize());
        Assertions.assertEquals(pointMask1, goal.getPointMasks().getBookshelfMasks().get(0));
        Assertions.assertEquals(pointMask2, goal.getPointMasks().getBookshelfMasks().get(1));
        Assertions.assertEquals(pointMask3, goal.getPointMasks().getBookshelfMasks().get(2));
        Assertions.assertEquals(pointMask4, goal.getPointMasks().getBookshelfMasks().get(3));

        Assertions.assertEquals(2, goal.calculatePoints(bookshelf));
        Assertions.assertEquals(4, goal.getPointMasks().getSize());
        Assertions.assertEquals(pointMask1, goal.getPointMasks().getBookshelfMasks().get(0));
        Assertions.assertEquals(pointMask2, goal.getPointMasks().getBookshelfMasks().get(1));
        Assertions.assertEquals(pointMask3, goal.getPointMasks().getBookshelfMasks().get(2));
        Assertions.assertEquals(pointMask4, goal.getPointMasks().getBookshelfMasks().get(3));
    }

    @Test
    @DisplayName("Calculating points when there are more than four rows: goal satisfied")
    void calculatePoints_moreThanFourRows_goalSatisfied() {
        Bookshelf bookshelf = new MockBookshelf(new int[][] {
            { 0, 0, 0, 0, 0 },
            { 1, 1, 1, 1, 1 },
            { 2, 2, 2, 2, 2 },
            { 3, 3, 3, 3, 3 },
            { 4, 4, 4, 4, 4 },
            { 5, 5, 5, 5, 5 },
        });

        BookshelfMask pointMask1 = new MockBookshelfMask(bookshelf, new int[][]{
                { 0, 0, 0, 0, 0 },
                { 1, 1, 1, 1, 1 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
        });
        BookshelfMask pointMask2 = new MockBookshelfMask(bookshelf, new int[][]{
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 1, 1, 1, 1, 1 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
        });
        BookshelfMask pointMask3 = new MockBookshelfMask(bookshelf, new int[][]{
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 1, 1, 1, 1, 1 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
        });
        BookshelfMask pointMask4 = new MockBookshelfMask(bookshelf, new int[][]{
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 1, 1, 1, 1, 1 },
                { 0, 0, 0, 0, 0 },
        });

        Assertions.assertEquals(8, goal.calculatePoints(bookshelf));
        Assertions.assertEquals(4, goal.getPointMasks().getSize());
        Assertions.assertEquals(pointMask1, goal.getPointMasks().getBookshelfMasks().get(0));
        Assertions.assertEquals(pointMask2, goal.getPointMasks().getBookshelfMasks().get(1));
        Assertions.assertEquals(pointMask3, goal.getPointMasks().getBookshelfMasks().get(2));
        Assertions.assertEquals(pointMask4, goal.getPointMasks().getBookshelfMasks().get(3));


        Assertions.assertEquals(6, goal.calculatePoints(bookshelf));
        Assertions.assertEquals(4, goal.getPointMasks().getSize());
        Assertions.assertEquals(pointMask1, goal.getPointMasks().getBookshelfMasks().get(0));
        Assertions.assertEquals(pointMask2, goal.getPointMasks().getBookshelfMasks().get(1));
        Assertions.assertEquals(pointMask3, goal.getPointMasks().getBookshelfMasks().get(2));
        Assertions.assertEquals(pointMask4, goal.getPointMasks().getBookshelfMasks().get(3));

        Assertions.assertEquals(4, goal.calculatePoints(bookshelf));
        Assertions.assertEquals(4, goal.getPointMasks().getSize());
        Assertions.assertEquals(pointMask1, goal.getPointMasks().getBookshelfMasks().get(0));
        Assertions.assertEquals(pointMask2, goal.getPointMasks().getBookshelfMasks().get(1));
        Assertions.assertEquals(pointMask3, goal.getPointMasks().getBookshelfMasks().get(2));
        Assertions.assertEquals(pointMask4, goal.getPointMasks().getBookshelfMasks().get(3));

        Assertions.assertEquals(2, goal.calculatePoints(bookshelf));
        Assertions.assertEquals(4, goal.getPointMasks().getSize());
        Assertions.assertEquals(pointMask1, goal.getPointMasks().getBookshelfMasks().get(0));
        Assertions.assertEquals(pointMask2, goal.getPointMasks().getBookshelfMasks().get(1));
        Assertions.assertEquals(pointMask3, goal.getPointMasks().getBookshelfMasks().get(2));
        Assertions.assertEquals(pointMask4, goal.getPointMasks().getBookshelfMasks().get(3));
    }

    @Test
    @DisplayName("Calculating points with a full rows bookshelf: goal satisfied")
    void calculatePoints_fullBookshelf_goalNotSatisfied() {
        Bookshelf bookshelf = new MockBookshelf(new int[][] {
            { 1, 1, 1, 1, 1 },
            { 2, 2, 2, 2, 2 },
            { 3, 3, 3, 3, 3 },
            { 4, 4, 4, 4, 4 },
            { 5, 5, 5, 5, 5 },
            { 6, 6, 6, 6, 6 },
        });

        BookshelfMask pointMask1 = new MockBookshelfMask(bookshelf, new int[][]{
                { 1, 1, 1, 1, 1 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
        });
        BookshelfMask pointMask2 = new MockBookshelfMask(bookshelf, new int[][]{
                { 0, 0, 0, 0, 0 },
                { 1, 1, 1, 1, 1 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
        });
        BookshelfMask pointMask3 = new MockBookshelfMask(bookshelf, new int[][]{
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 1, 1, 1, 1, 1 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
        });
        BookshelfMask pointMask4 = new MockBookshelfMask(bookshelf, new int[][]{
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 1, 1, 1, 1, 1 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
        });

        Assertions.assertEquals(8, goal.calculatePoints(bookshelf));
        Assertions.assertEquals(4, goal.getPointMasks().getSize());
        Assertions.assertEquals(pointMask1, goal.getPointMasks().getBookshelfMasks().get(0));
        Assertions.assertEquals(pointMask2, goal.getPointMasks().getBookshelfMasks().get(1));
        Assertions.assertEquals(pointMask3, goal.getPointMasks().getBookshelfMasks().get(2));
        Assertions.assertEquals(pointMask4, goal.getPointMasks().getBookshelfMasks().get(3));


        Assertions.assertEquals(6, goal.calculatePoints(bookshelf));
        Assertions.assertEquals(4, goal.getPointMasks().getSize());
        Assertions.assertEquals(pointMask1, goal.getPointMasks().getBookshelfMasks().get(0));
        Assertions.assertEquals(pointMask2, goal.getPointMasks().getBookshelfMasks().get(1));
        Assertions.assertEquals(pointMask3, goal.getPointMasks().getBookshelfMasks().get(2));
        Assertions.assertEquals(pointMask4, goal.getPointMasks().getBookshelfMasks().get(3));

        Assertions.assertEquals(4, goal.calculatePoints(bookshelf));
        Assertions.assertEquals(4, goal.getPointMasks().getSize());
        Assertions.assertEquals(pointMask1, goal.getPointMasks().getBookshelfMasks().get(0));
        Assertions.assertEquals(pointMask2, goal.getPointMasks().getBookshelfMasks().get(1));
        Assertions.assertEquals(pointMask3, goal.getPointMasks().getBookshelfMasks().get(2));
        Assertions.assertEquals(pointMask4, goal.getPointMasks().getBookshelfMasks().get(3));

        Assertions.assertEquals(2, goal.calculatePoints(bookshelf));
        Assertions.assertEquals(4, goal.getPointMasks().getSize());
        Assertions.assertEquals(pointMask1, goal.getPointMasks().getBookshelfMasks().get(0));
        Assertions.assertEquals(pointMask2, goal.getPointMasks().getBookshelfMasks().get(1));
        Assertions.assertEquals(pointMask3, goal.getPointMasks().getBookshelfMasks().get(2));
        Assertions.assertEquals(pointMask4, goal.getPointMasks().getBookshelfMasks().get(3));
    }

    @Test
    @DisplayName("goal satisfied and then not satisfied: points should return to 0")
    void calculatePoints_goalSatisfiedThenNotSatisfied_pointsReturnToZero() {
        Bookshelf bookshelf = new MockBookshelf(new int[][] {
                { 1, 1, 1, 1, 1 },
                { 2, 2, 2, 2, 2 },
                { 3, 3, 3, 3, 3 },
                { 4, 4, 4, 4, 4 },
                { 5, 5, 5, 5, 5 },
                { 6, 6, 6, 6, 6 },
        });

        BookshelfMask pointMask1 = new MockBookshelfMask(bookshelf, new int[][]{
                { 1, 1, 1, 1, 1 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
        });
        BookshelfMask pointMask2 = new MockBookshelfMask(bookshelf, new int[][]{
                { 0, 0, 0, 0, 0 },
                { 1, 1, 1, 1, 1 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
        });
        BookshelfMask pointMask3 = new MockBookshelfMask(bookshelf, new int[][]{
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 1, 1, 1, 1, 1 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
        });
        BookshelfMask pointMask4 = new MockBookshelfMask(bookshelf, new int[][]{
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 1, 1, 1, 1, 1 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
        });

        Assertions.assertEquals(8, goal.calculatePoints(bookshelf));
        Assertions.assertEquals(4, goal.getPointMasks().getSize());
        Assertions.assertEquals(pointMask1, goal.getPointMasks().getBookshelfMasks().get(0));
        Assertions.assertEquals(pointMask2, goal.getPointMasks().getBookshelfMasks().get(1));
        Assertions.assertEquals(pointMask3, goal.getPointMasks().getBookshelfMasks().get(2));
        Assertions.assertEquals(pointMask4, goal.getPointMasks().getBookshelfMasks().get(3));


        Assertions.assertEquals(6, goal.calculatePoints(bookshelf));
        Assertions.assertEquals(4, goal.getPointMasks().getSize());
        Assertions.assertEquals(pointMask1, goal.getPointMasks().getBookshelfMasks().get(0));
        Assertions.assertEquals(pointMask2, goal.getPointMasks().getBookshelfMasks().get(1));
        Assertions.assertEquals(pointMask3, goal.getPointMasks().getBookshelfMasks().get(2));
        Assertions.assertEquals(pointMask4, goal.getPointMasks().getBookshelfMasks().get(3));

        bookshelf = new MockBookshelf(new int[][]{
                { 0, 0, 3, 2, 1 },
                { 1, 1, 1, 4, 1 },
                { 2, 2, 4, 2, 2 },
                { 4, 4, 5, 2, 4 },
                { 1, 4, 5, 6, 1 },
                { 3, 5, 5, 1, 6 },
        });

        Assertions.assertEquals(0, goal.calculatePoints(bookshelf));
        Assertions.assertEquals(0, goal.getPointMasks().getSize());

        bookshelf = new MockBookshelf(new int[][] {
                { 0, 0, 0, 0, 0 },
                { 1, 3, 1, 2, 1 },
                { 2, 2, 2, 2, 2 },
                { 3, 3, 6, 3, 3 },
                { 4, 4, 4, 4, 4 },
                { 5, 4, 3, 2, 1 },
        });

        pointMask1 = new MockBookshelfMask(bookshelf, new int[][]{
                { 0, 0, 0, 0, 0 },
                { 1, 1, 1, 1, 1 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
        });
        pointMask2 = new MockBookshelfMask(bookshelf, new int[][]{
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 1, 1, 1, 1, 1 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
        });
        pointMask3 = new MockBookshelfMask(bookshelf, new int[][]{
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 1, 1, 1, 1, 1 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
        });
        pointMask4 = new MockBookshelfMask(bookshelf, new int[][]{
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 1, 1, 1, 1, 1 },
                { 0, 0, 0, 0, 0 },
        });

        Assertions.assertEquals(4, goal.calculatePoints(bookshelf));
        Assertions.assertEquals(4, goal.getPointMasks().getSize());
        Assertions.assertEquals(pointMask1, goal.getPointMasks().getBookshelfMasks().get(0));
        Assertions.assertEquals(pointMask2, goal.getPointMasks().getBookshelfMasks().get(1));
        Assertions.assertEquals(pointMask3, goal.getPointMasks().getBookshelfMasks().get(2));
        Assertions.assertEquals(pointMask4, goal.getPointMasks().getBookshelfMasks().get(3));

        Assertions.assertEquals(2, goal.calculatePoints(bookshelf));
        Assertions.assertEquals(4, goal.getPointMasks().getSize());
        Assertions.assertEquals(pointMask1, goal.getPointMasks().getBookshelfMasks().get(0));
        Assertions.assertEquals(pointMask2, goal.getPointMasks().getBookshelfMasks().get(1));
        Assertions.assertEquals(pointMask3, goal.getPointMasks().getBookshelfMasks().get(2));
        Assertions.assertEquals(pointMask4, goal.getPointMasks().getBookshelfMasks().get(3));
    }
}
