package it.polimi.ingsw.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PairTest {
    @Test
    public void testEquals () {
        Pair<String, Coordinate> p1 = new Pair<>("Test", new Coordinate(4, 5));
        Pair<String, Coordinate> p2 = new Pair<>("Test", new Coordinate(4, 5));
        assertEquals(p1, p2);
    }

    @Test
    public void testEqualsFalse () {
        Pair<String, Coordinate> p1 = new Pair<>("Test", new Coordinate(4, 5));
        Pair<String, Coordinate> p2 = new Pair<>(null, new Coordinate(4, 5));
        assertNotEquals(p1, p2);
    }
}
