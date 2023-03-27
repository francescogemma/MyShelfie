package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.model.game.Player;
import org.junit.jupiter.api.*;

/**
 * Tester for Game
 * @author Giacomo Groppi
 * */
class GameTest {
    private Game game;

    private void addPlayer (int number) {
        assert number > 1 && number < 5;
        int i;

        for (i = 0; i < number; i++) {
            Player p = new Player("Name" + i);
            game.addPlayer(p);
            Assertions.assertTrue(game.isConnected(p));
        }
    }

    @BeforeEach
    public void setUp() {
        game = new Game("TestingGame");
        Assertions.assertEquals(game.getName(), "TestingGame");
    }

    @Test
    void testCurrentPlayer() {
        Assertions.assertNull(game.getCurrentPlayer());
        addPlayer(3);
    }

}
