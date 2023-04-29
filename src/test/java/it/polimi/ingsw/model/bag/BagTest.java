package it.polimi.ingsw.model.bag;

import it.polimi.ingsw.model.bag.Bag;
import it.polimi.ingsw.utils.Logger;
import jdk.jfr.Description;
import org.junit.jupiter.api.*;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class BagTest {
    // TODO: Use test methods name conventions

    private static final int numberOfRun = 10000;
    private Bag bag;

    @BeforeEach
    public void setUp() {
        bag = new Bag();
        Logger.setShouldPrint(false);
    }

    @RepeatedTest(numberOfRun)
    @Description("The function checks the initial size of the Bag")
    void checkSize() {
        Assertions.assertEquals(22 * 6, bag.getRemaining());
    }

    @RepeatedTest(numberOfRun)
    @Description("The function tests that the getRandomTile function removes the object from the board.")
    void testGetRandomTile () {
        for (int i = 0; i < 22 * 6; i++) {
            final int oSize = bag.getRemaining();
            bag.getRandomTile();
            Assertions.assertEquals(oSize - 1, bag.getRemaining());
        }
    }

    @RepeatedTest(numberOfRun)
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
        final int r = new Random().nextInt(bag.getRemaining() - 1) + 1;

        for (int i = 0; i < r; i++)
            bag.getRandomTile();
    }

    @RepeatedTest(numberOfRun)
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

    @RepeatedTest(numberOfRun)
    @Description("Testing clone")
    void testingClone() {
        removeRandom();
        Bag newBag = new Bag(this.bag);
        assertEquals(newBag, this.bag);
    }

    @RepeatedTest(numberOfRun)
    @Description("Testing clone with restore")
    void testingCloneAndRestore() {
        removeRandom();
        Bag newBag = new Bag(this.bag);
        assertEquals(newBag, this.bag);
        newBag.getRandomTile();
        newBag.forgetLastExtraction();
        assertEquals(newBag, this.bag);
    }

    @RepeatedTest(numberOfRun)
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