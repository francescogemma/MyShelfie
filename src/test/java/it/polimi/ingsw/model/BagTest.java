package it.polimi.ingsw.model;

import jdk.jfr.Description;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class BagTest {
    private Bag bag;

    @BeforeEach
    public void setUp() {
        bag = new Bag();
    }

    @Test
    @Description("The function checks the initial size of the Bag")
    void checkSize() {
        Assertions.assertEquals(22 * 6, bag.getRemaining());
    }

    @Test
    @Description("The function tests that the getRandomTile function removes the object from the board.")
    void testGetRandomTile () {
        for (int i = 0; i < 22 * 6; i++) {
            final int oSize = bag.getRemaining();
            bag.getRandomTile();
            Assertions.assertEquals(oSize - 1, bag.getRemaining());
        }
    }

    @Test
    @Description("Tests that an exception is thrown when attempting to remove a Tile but the bag is empty.")
    void testExtractionEmpty() {
        for (int i = 0; i < 22 * 6; i++) {
            bag.getRandomTile();
        }

        Assertions.assertEquals(0, bag.getRemaining());
        Assertions.assertThrows(IllegalStateException.class, () -> {
            bag.getRandomTile();
        });
    }

    private void removeRandom() {
        final int r = new Random().nextInt(bag.getRemaining());
        for (int i = 0; i < r; i++)
            bag.getRandomTile();
    }

    @Test
    @Description("Test restore")
    void testRestore () {
        removeRandom();
        final int s = bag.getRemaining();
        bag.forgetLastExtraction();
        assertEquals(s + 1, bag.getRemaining());

        assertThrows(RuntimeException.class, () -> {
            bag.forgetLastExtraction();
        });
    }

    @Test
    @Description("Testing clone")
    void testingClone() {
        removeRandom();
        Bag newBag = new Bag(this.bag);
        assertEquals(newBag, this.bag);
    }

    @Test
    @Description("Testing clone with restore")
    void testingCloneAndRestore() {
        removeRandom();
        Bag newBag = new Bag(this.bag);
        assertEquals(newBag, this.bag);
        newBag.getRandomTile();
        newBag.forgetLastExtraction();
        assertEquals(newBag, this.bag);
    }

    @Test
    @Description("Testing clone and equals")
    void testingCloneAndEquals() {
        removeRandom();
        Bag newBag = new Bag(this.bag);
        assertEquals(newBag, this.bag);
        newBag.getRandomTile();
        assertNotEquals(newBag, this.bag);
    }

    @AfterEach
    public void tearDown() {

    }
}