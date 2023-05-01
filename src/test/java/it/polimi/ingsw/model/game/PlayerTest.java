package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.bookshelf.Bookshelf;
import it.polimi.ingsw.model.goal.PersonalGoal;
import it.polimi.ingsw.model.tile.Tile;
import it.polimi.ingsw.model.tile.TileColor;
import it.polimi.ingsw.model.tile.TileVersion;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

class PlayerTest {
    private Player player;

    @BeforeEach
    public void setUp() {
        player = new Player("TestingPlayer");
        player.getBookshelf().insertTiles(List.of(Tile.getInstance(TileColor.GREEN, TileVersion.FIRST)), 0);
    }

    @Test
    @DisplayName("Add points to the player")
    void addPoints_pointsToAdd_updatedPoints() {
        player.addPoints(10);
        Assertions.assertEquals(10, player.getPoints());
        player.addPoints(5);
        Assertions.assertEquals(15, player.getPoints());
    }

    @Test
    @DisplayName("Add negative points to the player")
    void addPoints_negativePoints_exceptionThrown() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> player.addPoints(-10));
    }

    @Test
    @DisplayName("Add zero points to the player")
    void addPoints_zeroPoints_exceptionThrown() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> player.addPoints(0));
    }

    @Test
    @DisplayName("Get 0 points from the player at the beginning")
    void getPoints_zeroPointsAtTheBeginning_zeroPoints() {
        Assertions.assertEquals(0, player.getPoints());
    }

    @Test
    @DisplayName("Get the name of the player")
    void getName_nameOfThePlayer_correctName() {
        Assertions.assertEquals("TestingPlayer", player.getUsername());
    }

    @Test
    @DisplayName("Get the bookshelf of the player")
    void getBookshelf_bookshelfOfThePlayer_correctBookshelf() {
        Bookshelf bookshelf = new Bookshelf();
        bookshelf.insertTiles(List.of(Tile.getInstance(TileColor.GREEN, TileVersion.FIRST)), 0);

        Assertions.assertEquals(bookshelf, player.getBookshelf());
    }

    @Test
    @DisplayName("Set the personal goal of the player")
    void setPersonalGoal_personalGoal_correctOutput() {
        PersonalGoal personalGoal = PersonalGoal.fromIndex(0);
        player.setPersonalGoal(personalGoal);
        Assertions.assertEquals(personalGoal, player.getPersonalGoal());
    }

    @Test
    @DisplayName("set connection state")
    void setConnectionState_connectionState_correctOutput() {
        Assertions.assertTrue(player.isConnected());
        player.setConnectionState(false);
        Assertions.assertFalse(player.isConnected());
        player.setConnectionState(true);
        Assertions.assertTrue(player.isConnected());
    }

    @Test
    @DisplayName("set achieved common goals")
    void setAchievedCommonGoals_achievedCommonGoals_correctOutput() {
        Assertions.assertFalse(player.hasAchievedCommonGoal(0));
        Assertions.assertFalse(player.hasAchievedCommonGoal(1));
        player.achievedCommonGoal(0);
        Assertions.assertTrue(player.hasAchievedCommonGoal(0));
        Assertions.assertFalse(player.hasAchievedCommonGoal(1));
        player.achievedCommonGoal(1);
        Assertions.assertTrue(player.hasAchievedCommonGoal(0));
        Assertions.assertTrue(player.hasAchievedCommonGoal(1));
    }

    @Test
    void is__correctOutput() {
        player = new Player("Giacomo");
        Assertions.assertTrue(player.is("Giacomo"));
    }
}
