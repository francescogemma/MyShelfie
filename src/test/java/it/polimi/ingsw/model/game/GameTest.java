package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.model.game.Player;
import it.polimi.ingsw.utils.Pair;
import org.junit.jupiter.api.*;

/**
 * Tester for Game
 * @author Giacomo Groppi
 * */
class GameTest {
    private Game game;

    private void addPlayer (int number) throws PlayerNotInGameException, IllegalFlowException {
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
        Assertions.assertEquals("TestingGame", game.getName());
    }

    @Test
    void testCurrentPlayer() throws PlayerNotInGameException, IllegalFlowException {
        Assertions.assertThrows(IllegalFlowException.class, () -> {
            game.getCurrentPlayer();
        });
        addPlayer(3);

        Assertions.assertThrows(IllegalFlowException.class, () -> {
             game.getCurrentPlayer();
        });

        game.startGame();
        Assertions.assertNotNull(game.getCurrentPlayer());
    }

    @Test
    void isOver_empty_correctOutput() throws PlayerNotInGameException, IllegalFlowException {
        addPlayer(2);
    }

    @Test
    void getStartingPlayer_empty_correctOutput() throws PlayerNotInGameException, IllegalFlowException {
        addPlayer(2);
        Assertions.assertNotNull(this.game.getStartingPlayer());
    }

    @Test
    void addPlayer_nullPlayer_ShouldThrowException() {
        Assertions.assertThrows(NullPointerException.class, () -> {
            this.game.addPlayer(null);
        });
    }

    @Test
    void addPlayer_moreThanFourPlayers_ShouldThrowException() throws PlayerNotInGameException, IllegalFlowException {
        addPlayer(4);
        Assertions.assertThrows(IllegalFlowException.class, () -> {
            this.game.addPlayer(new Player("test"));
        });
    }

    @Test
    void isConnected_playerNotExistsConnected_ShouldThrowException() throws PlayerNotInGameException, IllegalFlowException {
        addPlayer(3);
        Assertions.assertThrows(PlayerNotInGameException.class, () -> {
            game.isConnected(new Player("Test"));
        });
    }

    @Test
    void isEquals__correctOutput() {
        Game g1 = new Game("test1");
        Game g2 = new Game("test1");
        Game g3 = new Game("test2");

        Assertions.assertEquals(g1, g2);
        Assertions.assertNotEquals(g3, g2);
    }
}
