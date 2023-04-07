package it.polimi.ingsw.model.board;

import it.polimi.ingsw.model.Tile;
import it.polimi.ingsw.model.bag.Bag;

import it.polimi.ingsw.utils.Coordinate;
import jdk.jfr.Description;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {
    private Board board;
    @BeforeEach
    public void setUp() {
        board = new Board();
    }

    @Test
    void testFill() {
        final boolean printBoard = false;
        Bag bag = new Bag();

        for (int i = 0; i < 33; i++) {
            assertFalse(board.isFull(2));
            board.fillRandomly(bag.getRandomTile(), 2);
            if (printBoard) System.out.print(board);

        }

        assertTrue(board.isFull(2));
        assertFalse(board.isFull(3));
        assertFalse(board.isFull(4));

        for (int i = 0; i < 7; i++) {
            assertFalse(board.isFull(3));
            board.fillRandomly(bag.getRandomTile(), 3);
            if (printBoard) System.out.print(board);
        }

        assertTrue(board.isFull(3));
        assertFalse(board.isFull(4));

        for (int i = 0; i < 5; i++) {
            assertFalse(board.isFull(4));
            board.fillRandomly(bag.getRandomTile(), 4);
            if (printBoard) System.out.print(board);

        }

        assertTrue(board.isFull(4));
    }

    @Test
    void fillRandomly_boardIsFull_ShouldThrowException() {
        fillBoard(board);
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            board.fillRandomly(Tile.BLUE, 4);
        });
    }

    static void fillBoard(Board board) {
        assert board != null;
        Bag bag = new Bag();
        for (int i = 0; i < 45; i++) {
            board.fillRandomly(bag.getRandomTile(), 4);
        }

        assertTrue(board.isFull(4));
    }

    @Test
    void testSelectAvailableTiles() throws IllegalExtractionException, SelectionFullException {
        fillBoard(board);

        Assertions.assertEquals(20, board.getSelectableTiles().size());

        board.selectTile(4, 0);

        Assertions.assertEquals( 2, board.getSelectableTiles().size());

        board.selectTile(4, 1);

        Assertions.assertEquals(1, board.getSelectableTiles().size());

        board.selectTile(4, 2);
    }

    @Test
    void testWrongeSelectAfterOne() {
        fillBoard(this.board);

        Assertions.assertThrows(IllegalExtractionException.class, () -> {
            board.selectTile(5, 5);
        });
    }

    @Test
    void testWrongeSelectAfterTwo() throws IllegalExtractionException, SelectionFullException {
        fillBoard(this.board);
        board.selectTile(5, 1);
        Assertions.assertThrows(IllegalExtractionException.class, () -> {
            board.selectTile(4, 2);
        });
    }

    @Test
    void testWrongeSelectAfterThree() throws IllegalExtractionException, SelectionFullException {
        fillBoard(this.board);
        board.selectTile(7, 3);
        board.selectTile(6, 3);
        Assertions.assertThrows(IllegalExtractionException.class, () -> {
            board.selectTile(6, 4);
        });
    }

    @Test
    void testSelectVertical() throws IllegalExtractionException, SelectionFullException {
        fillBoard(this.board);
        board.selectTile(7, 3);
        board.selectTile(6, 3);
        board.selectTile(5, 3);
    }

    @Test
    void testSelectHorizontal() throws IllegalExtractionException, SelectionFullException {
        fillBoard(this.board);
        board.selectTile(7, 3);
        board.selectTile(6, 3);
        board.selectTile(5, 3);
    }

    @Test
    void testSelectDrawOne() throws IllegalExtractionException, SelectionFullException {
        fillBoard(this.board);
        board.selectTile(7, 3);
        Assertions.assertEquals(1, board.draw().size());
    }

    @Test
    void testSelectDrawTwo() throws IllegalExtractionException, SelectionFullException {
        fillBoard(this.board);
        board.selectTile(7, 3);
        board.selectTile(6, 3);
        Assertions.assertEquals(2, board.draw().size());
    }

    @Test
    void testSelectDrawThree() throws IllegalExtractionException, SelectionFullException {
        fillBoard(this.board);
        board.selectTile(7, 3);
        board.selectTile(6, 3);
        board.selectTile(5, 3);
        Assertions.assertEquals(3, board.draw().size());
    }

    @Test
    void testNeedRefill() throws IllegalExtractionException, SelectionFullException {
        fillBoard(this.board);
        assertFalse(board.needsRefill());

        board.selectTile(5, 1);
        board.selectTile(5, 2);
        board.selectTile(5, 3);

        Assertions.assertEquals(3, board.draw().size());
        assertFalse(board.needsRefill());

        board.selectTile(4, 0);
        board.selectTile(4, 1);

        Assertions.assertEquals(2, board.draw().size());

        assertTrue(board.needsRefill());
    }

    @Test
    void testRemoveSameTile() throws IllegalExtractionException, SelectionFullException {
        fillBoard(this.board);
        board.selectTile(4, 0);
        board.selectTile(4, 1);
        Assertions.assertThrows(IllegalExtractionException.class, () ->  {
            board.selectTile(4, 0);
        });
        board.selectTile(4, 2);
    }

    @Test
    void selectTile_four_ShouldThrowException() throws IllegalExtractionException, SelectionFullException {
        fillBoard(this.board);
        this.board.selectTile(4, 0);
        this.board.selectTile(4, 1);
        this.board.selectTile(4, 2);
        Assertions.assertThrows(SelectionFullException.class, () -> {
            this.board.selectTile(6, 6);
        });
    }

    @Test
    @Description("Make sure that getSelectedTiles does not return null.")
    void getSelectedTiles_testCombination_correctOutput() throws SelectionFullException, IllegalExtractionException {
        fillBoard(board);
        board.selectTile(4, 0);
        board.selectTile(4, 1);
        board.selectTile(4, 2);

        assertFalse(board.getSelectedTiles().contains(null));
        Assertions.assertEquals(3, board.getSelectedTiles().size());
    }

    @Test
    @Description("Make sure that needsRefill return false if we didn't touch any Tile on Board")
    void needsRefill_testZeroExtraction_correctOutput () throws SelectionFullException, IllegalExtractionException {
        fillBoard(board);
        for (int i = 0; i < Board.TWO_PLAYER_POSITION.size() + Board.THREE_PLAYER_POSITION.size() + Board.FOUR_PLAYER_POSITION.size(); i++) {
            board.selectTile(
                    board.getSelectableCoordinate().get(0)
            );
            board.draw();
        }

        assertEquals(0, board.getSelectableCoordinate().size());

        assertTrue(board.needsRefill());
    }

    @Test
    void needsRefill_testMoreThanFourPlayer_ShouldThrowException() {
        fillBoard(board);
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            board.isFull(5);
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            board.isFull(1);
        });
    }

    @Test
    void selectTile_coordinateOutside_ShouldThrowException() {
        fillBoard(board);
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            board.selectTile(-1, 5);
        });

    }
}
