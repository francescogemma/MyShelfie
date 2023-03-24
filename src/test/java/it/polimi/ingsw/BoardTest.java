package it.polimi.ingsw;

import it.polimi.ingsw.model.Bag;
import it.polimi.ingsw.model.Board;

import it.polimi.ingsw.model.IllegalExtractionException;
import it.polimi.ingsw.model.Tile;
import org.junit.jupiter.api.*;

import java.util.List;

public class BoardTest {
    private Board board;
    @BeforeEach
    public void setUp() {
        board = new Board();
    }

    @Test
    public void testFill() {
        final boolean printBoard = false;
        Bag bag = new Bag();

        for (int i = 0; i < 33; i++) {
            Assertions.assertFalse(board.isFull(2));
            board.fillRandomly(bag.getRandomTile(), 2);
            if (printBoard) System.out.print(board);

        }

        Assertions.assertTrue(board.isFull(2));
        Assertions.assertFalse(board.isFull(3));
        Assertions.assertFalse(board.isFull(4));

        for (int i = 0; i < 7; i++) {
            Assertions.assertFalse(board.isFull(3));
            board.fillRandomly(bag.getRandomTile(), 3);
            if (printBoard) System.out.print(board);
        }

        Assertions.assertTrue(board.isFull(3));
        Assertions.assertFalse(board.isFull(4));

        for (int i = 0; i < 5; i++) {
            Assertions.assertFalse(board.isFull(4));
            board.fillRandomly(bag.getRandomTile(), 4);
            if (printBoard) System.out.print(board);

        }

        Assertions.assertTrue(board.isFull(4));
    }

    static void fillBoard(Board board) {
        assert board != null;
        Bag bag = new Bag();
        for (int i = 0; i < 45; i++) {
            board.fillRandomly(bag.getRandomTile(), 4);
        }

        Assertions.assertTrue(board.isFull(4));
    }

    @Test
    public void testSelectAvailableTiles() throws IllegalExtractionException {
        fillBoard(board);

        Assertions.assertEquals(20, board.getSelectableTiles().size());

        board.selectTile(4, 0);

        Assertions.assertEquals( 2, board.getSelectableTiles().size());

        board.selectTile(4, 1);

        Assertions.assertEquals(1, board.getSelectableTiles().size());

        board.selectTile(4, 2);
    }

    @Test
    public void testWrongeSelectAfterOne() {
        fillBoard(this.board);

        Assertions.assertThrows(IllegalExtractionException.class, () -> {
            board.selectTile(5, 5);
        });
    }

    @Test
    public void testWrongeSelectAfterTwo() throws IllegalExtractionException {
        fillBoard(this.board);
        board.selectTile(5, 1);
        Assertions.assertThrows(IllegalExtractionException.class, () -> {
            board.selectTile(4, 2);
        });
    }

    @Test
    public void testWrongeSelectAfterThree() throws IllegalExtractionException {
        fillBoard(this.board);
        board.selectTile(7, 3);
        board.selectTile(6, 3);
        Assertions.assertThrows(IllegalExtractionException.class, () -> {
            board.selectTile(6, 4);
        });
    }

    @Test
    public void testSelectVertical() throws IllegalExtractionException {
        fillBoard(this.board);
        board.selectTile(7, 3);
        board.selectTile(6, 3);
        board.selectTile(5, 3);
    }

    @Test
    public void testSelectHorizontal() throws IllegalExtractionException {
        fillBoard(this.board);
        board.selectTile(7, 3);
        board.selectTile(6, 3);
        board.selectTile(5, 3);
    }

    @Test
    public void testSelectDrawOne() throws IllegalExtractionException {
        fillBoard(this.board);
        board.selectTile(7, 3);
        Assertions.assertEquals(1, board.draw().size());
    }

    @Test
    public void testSelectDrawTwo() throws IllegalExtractionException {
        fillBoard(this.board);
        board.selectTile(7, 3);
        board.selectTile(6, 3);
        Assertions.assertEquals(2, board.draw().size());
    }

    @Test
    public void testSelectDrawThree() throws IllegalExtractionException {
        fillBoard(this.board);
        board.selectTile(7, 3);
        board.selectTile(6, 3);
        board.selectTile(5, 3);
        Assertions.assertEquals(3, board.draw().size());
    }

    @Test
    public void testNeedRefill() throws IllegalExtractionException {
        fillBoard(this.board);
        Assertions.assertFalse(board.needsRefill());

        board.selectTile(5, 1);
        board.selectTile(5, 2);
        board.selectTile(5, 3);

        Assertions.assertEquals(3, board.draw().size());
        Assertions.assertFalse(board.needsRefill());

        board.selectTile(4, 0);
        board.selectTile(4, 1);

        Assertions.assertEquals(2, board.draw().size());

        Assertions.assertTrue(board.needsRefill());
    }
}
