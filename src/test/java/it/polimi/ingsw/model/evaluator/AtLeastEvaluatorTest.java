package it.polimi.ingsw.model.evaluator;

import it.polimi.ingsw.model.bookshelf.Bookshelf;
import it.polimi.ingsw.model.bookshelf.BookshelfMask;
import it.polimi.ingsw.model.bookshelf.Shelf;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.function.Predicate;

class AtLeastEvaluatorTest {
    AtLeastEvaluator atLeastEvaluator;

    @Test
    @DisplayName("Insert enough masks and check correct progression of given points.")
    void getPoints_enoughMasks_correctOutput() {
        atLeastEvaluator = new AtLeastEvaluator(4, 12);

        for (int i = 0; i < 16; i++) {
            atLeastEvaluator.add(new BookshelfMask(new Bookshelf()));
        }

        Assertions.assertEquals(8, atLeastEvaluator.getPoints());
        Assertions.assertEquals(6, atLeastEvaluator.getPoints());
        Assertions.assertEquals(4, atLeastEvaluator.getPoints());
        Assertions.assertEquals(2, atLeastEvaluator.getPoints());

        Assertions.assertThrows(IllegalStateException.class, () -> atLeastEvaluator.getPoints());
    }

    @Test
    @DisplayName("Insert not enough masks and check that no points are given.")
    void getPoints_tooFewMasks_correctOutput() {
        atLeastEvaluator = new AtLeastEvaluator(4, 12);

        for (int i = 0; i < 6; i++) {
            atLeastEvaluator.add(new BookshelfMask(new Bookshelf()));
        }

        Assertions.assertEquals(0, atLeastEvaluator.getPoints());
    }

    @Test
    @DisplayName("Insert not enough masks and check that no points are given.")
    void AtLeastEvaluator_toCount_correctOutput() {
        // Create a predicate that says only to count empty masks
        Predicate<BookshelfMask> toCount = (mask) -> mask.getShelves().isEmpty();
        atLeastEvaluator = new AtLeastEvaluator(4, 7, toCount);

        for (int i = 0; i < 6; i++) {
            atLeastEvaluator.add(new BookshelfMask(new Bookshelf()));
        }

        BookshelfMask bookshelfMask = new BookshelfMask(new Bookshelf());
        bookshelfMask.add(Shelf.getInstance(2, 2));
        atLeastEvaluator.add(bookshelfMask);

        Assertions.assertEquals(0, atLeastEvaluator.getPoints());
    }
}
