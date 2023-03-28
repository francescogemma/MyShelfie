package it.polimi.ingsw.model.evaluator;

import it.polimi.ingsw.model.bookshelf.Bookshelf;
import it.polimi.ingsw.model.bookshelf.BookshelfMask;
import it.polimi.ingsw.model.bookshelf.MockBookshelf;
import it.polimi.ingsw.model.bookshelf.Shelf;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;

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

    private static ArrayList<Arguments> bookshelfMaskAndSizeProvider() {
        ArrayList<Arguments> arguments = new ArrayList<>();
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
    }

    @Test
    @DisplayName("Add an empty mask and check that points are 0.")
    void add_emptyMask_correctOutput() {
        adjacencyEvaluator.add(new BookshelfMask(new Bookshelf()));
        Assertions.assertEquals(0, adjacencyEvaluator.getPoints());
    }

    @ParameterizedTest
    @DisplayName("Add masks of various sizes and get results.")
    @MethodSource("bookshelfMaskAndSizeProvider")
    void add_variousMasks_correctOutput(BookshelfMask bookshelfMask, int size) {
        adjacencyEvaluator.add(bookshelfMask);

        if (size >= 6) Assertions.assertEquals(8, adjacencyEvaluator.getPoints());
        if (size == 5) Assertions.assertEquals(5, adjacencyEvaluator.getPoints());
        if (size == 4) Assertions.assertEquals(3, adjacencyEvaluator.getPoints());
        if (size == 3) Assertions.assertEquals(2, adjacencyEvaluator.getPoints());
    }

    @Test
    @DisplayName("Add two masks and check if point sum is correct")
    void add_twoMasks_correctOutput() {
        adjacencyEvaluator.add(bookshelfMaskSizer(3));
        adjacencyEvaluator.add(bookshelfMaskSizer(5));

        Assertions.assertEquals(5 + 2, adjacencyEvaluator.getPoints());
    }

    @Test
    @DisplayName("Add many masks together and check if sum is correct")
    void add_manyMasks_correctOutput() {
        adjacencyEvaluator.add(bookshelfMaskSizer(3));
        adjacencyEvaluator.add(bookshelfMaskSizer(5));
        adjacencyEvaluator.add(bookshelfMaskSizer(8));
        adjacencyEvaluator.add(bookshelfMaskSizer(2));
        adjacencyEvaluator.add(bookshelfMaskSizer(7));
        adjacencyEvaluator.add(bookshelfMaskSizer(4));

        Assertions.assertEquals(2 + 5 + 8 + 8 + 3, adjacencyEvaluator.getPoints());
    }
}
