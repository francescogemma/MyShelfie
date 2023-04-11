package it.polimi.ingsw.model.evaluator;

import it.polimi.ingsw.model.bookshelf.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;

class AdjacencyEvaluatorTest {
    private AdjacencyEvaluator adjacencyEvaluator;

    private static BookshelfMask bookshelfMaskSizer(int size) {
        Bookshelf bookshelf = new MockBookshelf(new int[][]{
                { 0, 0, 0, 0, 0 },
                { 1, 1, 1, 1, 1 },
                { 2, 2, 2, 2, 2 },
                { 3, 3, 3, 3, 3 },
                { 4, 4, 4, 4, 4 },
                { 5, 5, 5, 5, 5 },
        });

        BookshelfMask bookshelfMask = new BookshelfMask(bookshelf);
        for (int i = 0; i < size; i++) {
            bookshelfMask.add(Shelf.getInstance(i % 6, i / 5));
        }

        return bookshelfMask;
    }

    private static List<Arguments> bookshelfMaskAndSizeProvider() {
        List<Arguments> arguments = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            arguments.add(Arguments.arguments(bookshelfMaskSizer(i), i));
        }

        return arguments;
    }

    @BeforeEach
    public void setUp() {
        adjacencyEvaluator = new AdjacencyEvaluator();
    }

    @Test
    @DisplayName("Insert nothing into adjacencyEvaluator and check if points are 0.")
    void add_nothing_correctOutput() {
        Assertions.assertEquals(0, adjacencyEvaluator.getPoints());
        Assertions.assertEquals(0, adjacencyEvaluator.getPointMasks().getSize());
    }

    @Test
    @DisplayName("Add an empty mask and check that points are 0.")
    void add_emptyMask_correctOutput() {
        adjacencyEvaluator.add(new BookshelfMask(new Bookshelf()));
        Assertions.assertEquals(0, adjacencyEvaluator.getPoints());
        Assertions.assertEquals(0, adjacencyEvaluator.getPointMasks().getSize());
    }

    @ParameterizedTest
    @DisplayName("Add masks of various sizes and get results.")
    @MethodSource("bookshelfMaskAndSizeProvider")
    void add_variousMasks_correctOutput(BookshelfMask bookshelfMask, int size) {
        adjacencyEvaluator.add(bookshelfMask);

        if (size >= 6) {
            Assertions.assertEquals(8, adjacencyEvaluator.getPoints());
            Assertions.assertEquals(1, adjacencyEvaluator.getPointMasks().getSize());
            Assertions.assertEquals(bookshelfMask, adjacencyEvaluator.getPointMasks().getBookshelfMasks().get(0));
        }
        if (size == 5) {
            Assertions.assertEquals(5, adjacencyEvaluator.getPoints());
            Assertions.assertEquals(1, adjacencyEvaluator.getPointMasks().getSize());
            Assertions.assertEquals(bookshelfMask, adjacencyEvaluator.getPointMasks().getBookshelfMasks().get(0));
        }
        if (size == 4) {
            Assertions.assertEquals(3, adjacencyEvaluator.getPoints());
            Assertions.assertEquals(1, adjacencyEvaluator.getPointMasks().getSize());
            Assertions.assertEquals(bookshelfMask, adjacencyEvaluator.getPointMasks().getBookshelfMasks().get(0));
        }
        if (size == 3) {
            Assertions.assertEquals(2, adjacencyEvaluator.getPoints());
            Assertions.assertEquals(1, adjacencyEvaluator.getPointMasks().getSize());
            Assertions.assertEquals(bookshelfMask, adjacencyEvaluator.getPointMasks().getBookshelfMasks().get(0));
        }
    }

    @Test
    @DisplayName("Add two masks and check if point sum is correct")
    void add_twoMasks_correctOutput() {
        BookshelfMask mask1 = bookshelfMaskSizer(3);
        BookshelfMask mask2 = bookshelfMaskSizer(5);

        adjacencyEvaluator.add(mask1);
        adjacencyEvaluator.add(mask2);

        BookshelfMaskSet bookshelfMaskSet = new BookshelfMaskSet();
        bookshelfMaskSet.add(mask1);
        bookshelfMaskSet.add(mask2);

        Assertions.assertEquals(5 + 2, adjacencyEvaluator.getPoints());
        Assertions.assertTrue(compareMaskSets(bookshelfMaskSet, adjacencyEvaluator.getPointMasks()));
    }

    @Test
    @DisplayName("Add many masks together and check if sum is correct")
    void add_manyMasks_correctOutput() {
        BookshelfMask mask1 = bookshelfMaskSizer(3);
        BookshelfMask mask2 = bookshelfMaskSizer(5);
        BookshelfMask mask3 = bookshelfMaskSizer(8);
        BookshelfMask mask4 = bookshelfMaskSizer(2);
        BookshelfMask mask5 = bookshelfMaskSizer(7);
        BookshelfMask mask6 = bookshelfMaskSizer(4);

        adjacencyEvaluator.add(mask1);
        adjacencyEvaluator.add(mask2);
        adjacencyEvaluator.add(mask3);
        adjacencyEvaluator.add(mask4);
        adjacencyEvaluator.add(mask5);
        adjacencyEvaluator.add(mask6);

        BookshelfMaskSet bookshelfMaskSet = new BookshelfMaskSet();
        bookshelfMaskSet.add(mask1);
        bookshelfMaskSet.add(mask2);
        bookshelfMaskSet.add(mask3);
        bookshelfMaskSet.add(mask5);
        bookshelfMaskSet.add(mask6);

        Assertions.assertEquals(2 + 5 + 8 + 8 + 3, adjacencyEvaluator.getPoints());
        Assertions.assertTrue(compareMaskSets(bookshelfMaskSet, adjacencyEvaluator.getPointMasks()));
    }

    @Test
    @DisplayName("Add two masks, clear, and check if point sum is 0")
    void clear_correctOutput() {
        adjacencyEvaluator.add(bookshelfMaskSizer(3));
        adjacencyEvaluator.add(bookshelfMaskSizer(5));

        adjacencyEvaluator.clear();
        Assertions.assertEquals(0, adjacencyEvaluator.getPoints());
        Assertions.assertEquals(0, adjacencyEvaluator.getPointMasks().getSize());
    }

    private boolean compareMaskSets(BookshelfMaskSet a, BookshelfMaskSet b) {
        if(a.getSize() != b.getSize()) {
            return false;
        }

        for(int i = 0; i < a.getSize(); i++) {
            if(!a.getBookshelfMasks().get(i).equals(b.getBookshelfMasks().get(i))) {
                return false;
            }
        }

        return true;
    }
}
