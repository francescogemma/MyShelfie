package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.tile.Tile;
import it.polimi.ingsw.model.tile.TileColor;
import it.polimi.ingsw.model.bookshelf.Bookshelf;
import it.polimi.ingsw.model.tile.TileVersion;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

class PlayerTest {
    private Player player;
    private Bookshelf bookshelf;

    @BeforeEach
    public void setUp() {
        player = new Player("TestingPlayer");
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
        Assertions.assertEquals("TestingPlayer", player.getName());
    }

    @Test
    @DisplayName("Set the bookshelf of the player")
    void setBookshelf_bookshelfOfThePlayer_correctBookshelf() {
        bookshelf = new Bookshelf();
        bookshelf.insertTiles(new ArrayList<>(Arrays.asList(
            Tile.getInstance(TileColor.CYAN, TileVersion.FIRST),
            Tile.getInstance(TileColor.MAGENTA, TileVersion.FIRST),
            Tile.getInstance(TileColor.BLUE, TileVersion.FIRST)
        )), 2);
        Assertions.assertNotEquals(bookshelf, player.getBookshelf());

        player.setBookshelf(bookshelf);
        Assertions.assertEquals(bookshelf, player.getBookshelf());
    }
}
