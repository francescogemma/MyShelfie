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

class RowFiveAllDifferentColorTest {

    @BeforeEach
    void setUp () {
        goal = new RowFiveAllDifferentColor();
        goal.setPointStack(List.of(2, 4, 6, 8));
    }
    private RowFiveAllDifferentColor goal;

    @Test
    @DisplayName("Each row has two or more identical tiles. Winning condition not met.")
    void calculatePoints_FourDifferent_correctOutput () {
        Bookshelf bookshelf = new MockBookshelf(new int[][]{
                { 0, 0, 3, 2, 1 },
                { 1, 1, 1, 4, 1 },
                { 2, 2, 4, 2, 2 },
                { 4, 3, 5, 2, 4 },
                { 1, 4, 5, 6, 1 },
                { 3, 5, 5, 1, 6 },
        });

        Assertions.assertEquals(0, goal.calculatePoints(bookshelf));
        Assertions.assertEquals(0, goal.getPointMasks().getSize());
    }

    @Test
    @DisplayName("Get correct result twice, then wrong once, check if getting correct result twice again" +
            "yields the correct result.")
    void calculatePoints_FiveDifferentDouble_correctOutput () {
        Bookshelf bookshelf = new MockBookshelf(new int[][]{
                { 0, 0, 3, 2, 1 },
                { 1, 1, 1, 4, 1 },
                { 2, 2, 4, 2, 2 },
                { 4, 3, 5, 2, 1 },
                { 1, 4, 5, 6, 1 },
                { 3, 5, 4, 1, 6 },
        });

        Bookshelf secondBookshelf = new MockBookshelf(new int[][]{
                { 0, 0, 3, 2, 1 },
                { 1, 1, 1, 4, 1 },
                { 2, 2, 4, 2, 2 },
                { 4, 3, 3, 2, 1 },
                { 1, 4, 5, 6, 1 },
                { 3, 5, 4, 1, 6 },
        });

        BookshelfMask rowFive = new MockBookshelfMask(bookshelf, new int[][]{
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 1, 1, 1, 1, 1 },
        });

        BookshelfMask rowThree = new MockBookshelfMask(bookshelf, new int[][]{
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
                { 1, 1, 1, 1, 1 },
                { 0, 0, 0, 0, 0 },
                { 0, 0, 0, 0, 0 },
        });

        // check for correct points result
        Assertions.assertEquals(8, goal.calculatePoints(bookshelf));
        Assertions.assertEquals(6, goal.calculatePoints(bookshelf));

        // check for correct mask content
        Assertions.assertTrue(goal.getPointMasks().contains(rowThree));
        Assertions.assertTrue(goal.getPointMasks().contains(rowFive));
        Assertions.assertEquals(2, goal.getPointMasks().getSize());

        // check for 0 points and masks
        Assertions.assertEquals(0, goal.calculatePoints(secondBookshelf));
        Assertions.assertEquals(0, goal.getPointMasks().getSize());

        // check for correct points result
        Assertions.assertEquals(4, goal.calculatePoints(bookshelf));
        Assertions.assertEquals(2, goal.calculatePoints(bookshelf));

        // check for correct mask content
        Assertions.assertTrue(goal.getPointMasks().contains(rowThree));
        Assertions.assertTrue(goal.getPointMasks().contains(rowFive));
        Assertions.assertEquals(2, goal.getPointMasks().getSize());
    }

    @Test
    @DisplayName("Insert only one row that meets the criteria, and expect a score of zero.")
    void calculatePoints_FiveDifferentSingle_correctOutput () {
        Bookshelf bookshelf = new MockBookshelf(new int[][]{
                { 0, 0, 3, 2, 1 },
                { 1, 1, 1, 4, 1 },
                { 2, 2, 4, 2, 2 },
                { 4, 3, 5, 2, 1 },
                { 1, 4, 5, 6, 1 },
                { 3, 5, 5, 1, 6 },
        });

        Assertions.assertEquals(0, goal.calculatePoints(bookshelf));
        Assertions.assertEquals(0, goal.getPointMasks().getSize());
    }
}
