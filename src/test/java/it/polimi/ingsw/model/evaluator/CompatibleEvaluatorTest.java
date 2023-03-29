package it.polimi.ingsw.model.evaluator;

import it.polimi.ingsw.model.bookshelf.Bookshelf;
import it.polimi.ingsw.model.bookshelf.BookshelfMask;
import it.polimi.ingsw.model.bookshelf.Shelf;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CompatibleEvaluatorTest {
    CompatibleEvaluator compatibleEvaluator;

    @Test
    @DisplayName("Create evaluator, do nothing, get 0 points")
    void getPoints_CorrectOutput() {
        compatibleEvaluator = new CompatibleEvaluator(2, 4, (a, b) -> true);
        Assertions.assertEquals(0, compatibleEvaluator.getPoints());
    }

    @Test
    @DisplayName("Add 3 incompatible shapes, requesting a size of 2. Therefore, get 0 points.")
    void getPoints_ThreeIncompatibles_CorrectOutput() {
        compatibleEvaluator = new CompatibleEvaluator(2, 2, (a, b) -> false);

        for (int i = 0; i < 3; i++) {
            compatibleEvaluator.add(new BookshelfMask(new Bookshelf()));
        }

        Assertions.assertEquals(0, compatibleEvaluator.getPoints());
    }

    @Test
    @DisplayName("Add a mask, an incompatible mask, and a compatible mask. Require size = 2, get points.")
    void getPoints_BadThenGood_CorrectOutput() {
        // Condition is shelves amount is equal.
        compatibleEvaluator = new CompatibleEvaluator(2, 2,
                (a, b) -> a.getShelves().size() == b.getShelves().size()
        );

        BookshelfMask bookshelfMask = new BookshelfMask(new Bookshelf());
        bookshelfMask.add(Shelf.getInstance(2, 2));

        // not compatible
        compatibleEvaluator.add(bookshelfMask);

        // compatible
        compatibleEvaluator.add(new BookshelfMask(new Bookshelf()));
        Assertions.assertEquals(0, compatibleEvaluator.getPoints());

        // compatible
        compatibleEvaluator.add(new BookshelfMask(new Bookshelf()));
        Assertions.assertNotEquals(0, compatibleEvaluator.getPoints());
    }

    @Test
    @DisplayName("Get a winning condition, then clear, therefore get no points.")
    void getPoints_afterClear_CorrectOutput() {
        compatibleEvaluator = new CompatibleEvaluator(2, 2, (a, b) -> true);

        for (int i = 0; i < 3; i++) {
            compatibleEvaluator.add(new BookshelfMask(new Bookshelf()));
        }

        Assertions.assertNotEquals(0, compatibleEvaluator.getPoints());

        compatibleEvaluator.clear();
        Assertions.assertEquals(0, compatibleEvaluator.getPoints());

    }

    @Test
    @DisplayName("Get a winning condition, keep requesting points.")
    void getPoints_repeat_CorrectOutput() {
        compatibleEvaluator = new CompatibleEvaluator(3, 2, (a, b) -> true);

        for (int i = 0; i < 3; i++) {
            compatibleEvaluator.add(new BookshelfMask(new Bookshelf()));
        }

        for (int i = 0; i < 16; i++) {
            compatibleEvaluator.add(new BookshelfMask(new Bookshelf()));
        }

        Assertions.assertEquals(8, compatibleEvaluator.getPoints());
        Assertions.assertEquals(6, compatibleEvaluator.getPoints());
        Assertions.assertEquals(4, compatibleEvaluator.getPoints());

        Assertions.assertThrows(IllegalStateException.class, () -> compatibleEvaluator.getPoints());
    }

    @Test
    @DisplayName("Try to construct an evaluator with nonsense targetGroupSize.")
    void CompatibleEvaluator_zeroTargetGroupSize_CorrectOutput() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
            compatibleEvaluator = new CompatibleEvaluator(3, 0, (a, b) -> true)
        );
    }

    @Test
    @DisplayName("Try to construct an evaluator with nonsense amount of players.")
    void CompatibleEvaluator_tooManyPlayers_CorrectOutput() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                compatibleEvaluator = new CompatibleEvaluator(32, 2, (a, b) -> true)
        );
    }


}
