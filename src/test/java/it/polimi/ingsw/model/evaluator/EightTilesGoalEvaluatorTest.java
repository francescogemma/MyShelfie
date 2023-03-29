package it.polimi.ingsw.model.evaluator;

import it.polimi.ingsw.model.bookshelf.Bookshelf;
import it.polimi.ingsw.model.bookshelf.BookshelfMask;
import it.polimi.ingsw.model.bookshelf.MockBookshelf;
import it.polimi.ingsw.model.bookshelf.Shelf;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EightTilesGoalEvaluatorTest {
    private EightTilesGoalEvaluator evaluator;
    private BookshelfMask mask;
    private Bookshelf bookshelf;

    @BeforeEach
    public void setUp() {
        evaluator = new EightTilesGoalEvaluator(4);
    }

    @Test
    @DisplayName("no mask added: 0 points")
    void calculatePoints_noMaskAdded_correctOutput() {
        Assertions.assertEquals(0, evaluator.getPoints());
    }

    @Test
    @DisplayName("empty mask: 0 points")
    void calculatePoints_emptyMask_correctOutput() {
        bookshelf = new MockBookshelf(new int[][]{
                { 3, 0, 0, 0, 0 },
                { 3, 1, 6, 0, 0 },
                { 3, 1, 6, 1, 0 },
                { 3, 3, 6, 6, 4 },
                { 3, 4, 1, 1, 5 },
                { 3, 3, 3, 3, 2 }
        });
        mask = new BookshelfMask(bookshelf);
        Assertions.assertTrue(evaluator.add(mask));
        Assertions.assertEquals(0, evaluator.getPoints());
    }

    @Test
    @DisplayName("tile 3 has more than 8 occurrences: 8 points")
    void calculatePoints_moreThanEight_correctOutput() {
        bookshelf = new MockBookshelf(new int[][]{
                { 3, 0, 0, 0, 0 },
                { 3, 1, 6, 0, 0 },
                { 3, 1, 6, 1, 0 },
                { 3, 3, 6, 6, 4 },
                { 3, 4, 1, 1, 5 },
                { 3, 3, 3, 3, 2 }
        });
        mask = new BookshelfMask(bookshelf);
        populateFullMask();
        Assertions.assertTrue(evaluator.add(mask));
        Assertions.assertEquals(8, evaluator.getPoints());
    }

    @Test
    @DisplayName("tile 3 has exactly 8 occurrences: 8 points")
    void calculatePoints_exactlyEight_correctOutput() {
        bookshelf = new MockBookshelf(new int[][]{
                { 3, 0, 0, 0, 0 },
                { 3, 1, 6, 0, 0 },
                { 3, 1, 6, 1, 0 },
                { 3, 4, 6, 6, 4 },
                { 3, 4, 1, 1, 5 },
                { 3, 3, 4, 3, 2 }
        });
        mask = new BookshelfMask(bookshelf);
        populateFullMask();
        Assertions.assertTrue(evaluator.add(mask));
        Assertions.assertEquals(8, evaluator.getPoints());
    }

    @Test
    @DisplayName("tile 3 has less than 8 occurrences: 0 points")
    void calculatePoints_lessThanEight_correctOutput() {
        bookshelf = new MockBookshelf(new int[][]{
                { 3, 0, 0, 0, 0 },
                { 3, 1, 6, 0, 0 },
                { 4, 1, 6, 1, 0 },
                { 3, 4, 6, 6, 4 },
                { 3, 4, 1, 1, 5 },
                { 3, 3, 4, 3, 2 }
        });
        mask = new BookshelfMask(bookshelf);
        populateFullMask();
        Assertions.assertTrue(evaluator.add(mask));
        Assertions.assertEquals(0, evaluator.getPoints());
    }

    private void populateFullMask() {
        for(int row = 0; row < Bookshelf.ROWS; row++) {
            for(int column = 0; column < Bookshelf.COLUMNS; column++) {
                mask.add(Shelf.getInstance(row, column));
            }
        }
    }
}
