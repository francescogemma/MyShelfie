package it.polimi.ingsw.model.goal;

import it.polimi.ingsw.model.bookshelf.Bookshelf;
import it.polimi.ingsw.model.bookshelf.MockBookshelf;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RowFiveAllDifferentColorTest {

    @BeforeEach
    void setUp () {
        goal = new RowFiveAllDifferentColor(4);
    }
    private RowFiveAllDifferentColor goal;

    @Test
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
    }

    @Test
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

        Assertions.assertEquals(8, goal.calculatePoints(bookshelf));
        Assertions.assertEquals(6, goal.calculatePoints(bookshelf));
        Assertions.assertEquals(0, goal.calculatePoints(secondBookshelf));
        Assertions.assertEquals(4, goal.calculatePoints(bookshelf));
        Assertions.assertEquals(2, goal.calculatePoints(bookshelf));
    }

    @Test
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
    }
}
