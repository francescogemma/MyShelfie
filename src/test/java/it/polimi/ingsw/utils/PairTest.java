package it.polimi.ingsw.utils;

import it.polimi.ingsw.utils.Coordinate;
import it.polimi.ingsw.utils.Pair;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PairTest {
    @Test
    void testEquals () {
        Pair<String, Coordinate> p1 = new Pair<>("Test", new Coordinate(4, 5));
        Pair<String, Coordinate> p2 = new Pair<>("Test", new Coordinate(4, 5));
        assertEquals(p1, p2);
    }

    @Test
    void testEqualsFalse () {
        Pair<String, Coordinate> p1 = new Pair<>("Test", new Coordinate(4, 5));
        Pair<String, Coordinate> p2 = new Pair<>(null, new Coordinate(4, 5));
        assertNotEquals(p1, p2);
    }
}
