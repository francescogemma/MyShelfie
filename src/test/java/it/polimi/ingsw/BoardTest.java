package it.polimi.ingsw;

import it.polimi.ingsw.model.Bag;
import it.polimi.ingsw.model.Board;

import it.polimi.ingsw.model.IllegalExtractionException;
import it.polimi.ingsw.model.Tile;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

import java.util.List;

public class BoardTest {
    private Board board;
    @Before
    public void setUp() {
        board = new Board();
    }

    @Test
    public void testFill() {
        final boolean printBoard = false;
        Bag bag = new Bag();

        for (int i = 0; i < 33; i++) {
            Assert.assertFalse(board.isFull(2));
            board.fillRandomly(bag.getRandomTile(), 2);
            if (printBoard) System.out.print(board);

        }

        Assert.assertTrue(board.isFull(2));
        Assert.assertFalse(board.isFull(3));
        Assert.assertFalse(board.isFull(4));

        for (int i = 0; i < 7; i++) {
            Assert.assertFalse(board.isFull(3));
            board.fillRandomly(bag.getRandomTile(), 3);
            if (printBoard) System.out.print(board);
        }

        Assert.assertTrue(board.isFull(3));
        Assert.assertFalse(board.isFull(4));

        for (int i = 0; i < 5; i++) {
            Assert.assertFalse(board.isFull(4));
            board.fillRandomly(bag.getRandomTile(), 4);
            if (printBoard) System.out.print(board);

        }

        Assert.assertTrue(board.isFull(4));
    }

    static void fillBoard(Board board) {
        assert board != null;
        Bag bag = new Bag();
        for (int i = 0; i < 45; i++) {
            board.fillRandomly(bag.getRandomTile(), 4);
        }

        Assert.assertTrue(board.isFull(4));
    }

    @Test
    public void testSelectAvailableTiles() throws IllegalExtractionException {
        fillBoard(board);

        Assert.assertEquals(20, board.getSelectableTiles().size());

        board.selectTile(4, 0);

        Assert.assertEquals( 2, board.getSelectableTiles().size());

        board.selectTile(4, 1);

        Assert.assertEquals(1, board.getSelectableTiles().size());

        board.selectTile(4, 2);
    }

    @Test (expected = IllegalExtractionException.class)
    public void testWrongeSelectAfterOne() throws IllegalExtractionException {
        fillBoard(this.board);
        board.selectTile(5, 5);
    }

    @Test (expected = IllegalExtractionException.class)
    public void testWrongeSelectAfterTwo() throws IllegalExtractionException {
        fillBoard(this.board);
        board.selectTile(5, 1);
        board.selectTile(4, 2);
    }

    @Test (expected = IllegalExtractionException.class)
    public void testWrongeSelectAfterThree() throws IllegalExtractionException {
        fillBoard(this.board);
        board.selectTile(7, 3);
        board.selectTile(6, 3);
        board.selectTile(6, 4);
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
        Assert.assertEquals(1, board.draw().size());
    }

    @Test
    public void testSelectDrawTwo() throws IllegalExtractionException {
        fillBoard(this.board);
        board.selectTile(7, 3);
        board.selectTile(6, 3);
        Assert.assertEquals(2, board.draw().size());
    }

    @Test
    public void testSelectDrawThree() throws IllegalExtractionException {
        fillBoard(this.board);
        board.selectTile(7, 3);
        board.selectTile(6, 3);
        board.selectTile(5, 3);
        Assert.assertEquals(3, board.draw().size());
    }
}
