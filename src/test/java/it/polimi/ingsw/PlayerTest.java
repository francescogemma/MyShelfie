package it.polimi.ingsw;

import it.polimi.ingsw.model.Player;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class PlayerTest {
    private Player player;

    @BeforeEach
    public void setUp() {
        player = new Player("TestingPlayer");
    }

    @Test
    @DisplayName("Add points to the player")
    public void addPoints_pointsToAdd_updatedPoints() {
        player.addPoints(10);
        Assertions.assertEquals(10, player.getPoints());
        player.addPoints(5);
        Assertions.assertEquals(15, player.getPoints());
    }

    @Test
    @DisplayName("Add negative points to the player")
    public void addPoints_negativePoints_exceptionThrown() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> player.addPoints(-10));
    }

    @Test
    @DisplayName("Add zero points to the player")
    public void addPoints_zeroPoints_exceptionThrown() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> player.addPoints(0));
    }

    @Test
    @DisplayName("Get 0 points from the player at the beginning")
    public void getPoints_zeroPointsAtTheBeginning_zeroPoints() {
        Assertions.assertEquals(0, player.getPoints());
    }

    @Test
    @DisplayName("Get the name of the player")
    public void getName_nameOfThePlayer_correctName() {
        Assertions.assertEquals("TestingPlayer", player.getName());
    }
}
