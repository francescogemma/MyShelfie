package it.polimi.ingsw.model.board;

import it.polimi.ingsw.model.bag.Bag;
import it.polimi.ingsw.model.bag.IllegalExtractionException;
import it.polimi.ingsw.model.board.Board;

import org.junit.jupiter.api.*;

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
    void testSelectAvailableTiles() throws IllegalExtractionException {
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
    void testWrongeSelectAfterTwo() throws IllegalExtractionException {
        fillBoard(this.board);
        board.selectTile(5, 1);
        Assertions.assertThrows(IllegalExtractionException.class, () -> {
            board.selectTile(4, 2);
        });
    }

    @Test
    void testWrongeSelectAfterThree() throws IllegalExtractionException {
        fillBoard(this.board);
        board.selectTile(7, 3);
        board.selectTile(6, 3);
        Assertions.assertThrows(IllegalExtractionException.class, () -> {
            board.selectTile(6, 4);
        });
    }

    @Test
    void testSelectVertical() throws IllegalExtractionException {
        fillBoard(this.board);
        board.selectTile(7, 3);
        board.selectTile(6, 3);
        board.selectTile(5, 3);
    }

    @Test
    void testSelectHorizontal() throws IllegalExtractionException {
        fillBoard(this.board);
        board.selectTile(7, 3);
        board.selectTile(6, 3);
        board.selectTile(5, 3);
    }

    @Test
    void testSelectDrawOne() throws IllegalExtractionException {
        fillBoard(this.board);
        board.selectTile(7, 3);
        Assertions.assertEquals(1, board.draw().size());
    }

    @Test
    void testSelectDrawTwo() throws IllegalExtractionException {
        fillBoard(this.board);
        board.selectTile(7, 3);
        board.selectTile(6, 3);
        Assertions.assertEquals(2, board.draw().size());
    }

    @Test
    void testSelectDrawThree() throws IllegalExtractionException {
        fillBoard(this.board);
        board.selectTile(7, 3);
        board.selectTile(6, 3);
        board.selectTile(5, 3);
        Assertions.assertEquals(3, board.draw().size());
    }

    @Test
    void testNeedRefill() throws IllegalExtractionException {
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

    @Test
    void testRemoveSameTile() throws IllegalExtractionException {
        fillBoard(this.board);
        board.selectTile(4, 0);
        board.selectTile(4, 1);
        Assertions.assertThrows(IllegalExtractionException.class, () ->  {
            board.selectTile(4, 0);
        });
        board.selectTile(4, 2);
    }
}
