package it.polimi.ingsw;

import it.polimi.ingsw.model.Bag;
import it.polimi.ingsw.model.Board;
import it.polimi.ingsw.model.Tile;

import org.junit.Test;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

public class BoardTest {
    @Test
    public void testFillTwoPlayers() {
        Board board = new Board();
        Bag bag = new Bag();

        for (int i = 0; i < 33; i++) {
            Assert.assertTrue(!board.isFull(2));
            board.fillRandomly(bag.getRandomTile(), 2);
            //System.out.print(board);

        }

        Assert.assertTrue(board.isFull(2));
        Assert.assertTrue(!board.isFull(3));
        Assert.assertTrue(!board.isFull(4));

        for (int i = 0; i < 7; i++) {
            Assert.assertTrue(!board.isFull(3));
            board.fillRandomly(bag.getRandomTile(), 3);
            //System.out.print(board);
        }

        Assert.assertTrue(board.isFull(3));
        Assert.assertTrue(!board.isFull(4));

        for (int i = 0; i < 5; i++) {
            Assert.assertTrue(!board.isFull(4));
            board.fillRandomly(bag.getRandomTile(), 4);
            //System.out.print(board);

        }
    }
}
