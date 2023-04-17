package it.polimi.ingsw.model.game;

import it.polimi.ingsw.event.LocalEventTransceiver;
import it.polimi.ingsw.event.data.gameEvent.PlayerHasDisconnectedEventData;
import it.polimi.ingsw.event.data.gameEvent.PlayerHasJoinEventData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Tester for Game
 * @author Giacomo Groppi
 * */
class GameTest {
    private Game game;
    private LocalEventTransceiver transceiver;

    @BeforeEach
    void setUp () {
        game = new Game("testing");
        transceiver = new LocalEventTransceiver();
        game.setTransceiver(transceiver);
    }

    @Test
    void getName__correctOutput() {
        Game g2 = new Game("testing2");
        Assertions.assertEquals("testing2", g2.getName());
    }

    @Test
    void constructor__throwNullPointerException() {
        Assertions.assertThrows(NullPointerException.class, () -> {
            new Game(null);
        });
    }

    @Test
    void contructor__throwIllegalArgumentException () {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new Game("");
        });
    }

    @Test
    void getStartingPlayer__correctOutput () throws IllegalFlowException, PlayerAlreadyInGameException {
        this.game.addPlayer("Giacomo");
        Assertions.assertEquals("Giacomo", game.getStartingPlayer().getUsername());
    }

    @Test
    void getStartingPlayer__throw () {
        Assertions.assertThrows(IllegalFlowException.class, () -> {
            game.getStartingPlayer();
        });
    }

    @Test
    void getPlayers__correctOutput () throws IllegalFlowException, PlayerAlreadyInGameException {
        game.addPlayer("Giacomo");
        Assertions.assertEquals(1, game.getPlayers().size());
        Assertions.assertEquals("Giacomo", game.getPlayers().get(0).getUsername());
    }

    @Test
    void isOver__correctOutput () {
        Assertions.assertFalse(game.isOver());
    }

    @Test
    void addPlayer__throwPlayerAlreadyInGameException() throws IllegalFlowException, PlayerAlreadyInGameException {
        game.addPlayer("Giacomo");
        Assertions.assertThrows(PlayerAlreadyInGameException.class, () -> {
            game.addPlayer("Giacomo");
        });
    }

    @Test
    void addPlayer_nullPointer_throwNullPointerException () {
        Assertions.assertThrows(NullPointerException.class, () -> {
            game.addPlayer(null);
        });
    }

    @Test
    void addPlayer_stringEmpty_throwNullPointerException() {
        Assertions.assertThrows(NullPointerException.class, () -> {
            game.addPlayer("");
        });
    }

    @Test
    void addPlayer_gameAlreadyStarted_throwIllegalFlowException() throws IllegalFlowException, PlayerAlreadyInGameException {
        game.addPlayer("Giacomo");
        game.addPlayer("Cristiano");
        game.addPlayer("Michele");

        game.startGame();

        Assertions.assertThrows(IllegalFlowException.class, () -> {
            game.addPlayer("Francesco");
        });
    }

    @Test
    void addPlayer_already4_throwIllegalFlowException() throws IllegalFlowException, PlayerAlreadyInGameException {
        game.addPlayer("Giacomo");
        game.addPlayer("Cristiano");
        game.addPlayer("Michele");
        game.addPlayer("Francesco");

        Assertions.assertThrows(IllegalFlowException.class, () -> {
            game.addPlayer("Paperino");
        });
    }

    @Test
    void startGame_onePlayer_throwIllegalFlowException () throws IllegalFlowException, PlayerAlreadyInGameException {
        game.addPlayer("Giacomo");
        Assertions.assertThrows(IllegalFlowException.class, () -> {
            game.startGame();
        });
    }

    @Test
    void startGame_0Players_throwIllegalFlowException () {
        Assertions.assertThrows(IllegalFlowException.class, () -> {
            game.startGame();
        });
    }

    @Test
    void getCurrentPlayer__correctOutput () throws IllegalFlowException, PlayerAlreadyInGameException {
        game.addPlayer("Giacomo");
        game.addPlayer("Michele");

        game.startGame();
        Assertions.assertEquals("Giacomo", game.getCurrentPlayer().getUsername());
    }

    @Test
    void getCurrentPlayer_gameNotStarted_throwsIllegalFlowException () {
        Assertions.assertThrows(IllegalFlowException.class, () -> {
            game.getCurrentPlayer();
        });
    }

    @Test
    void setTransceiver_nullPointer_throws () {
        this.game = new Game("testing");
        Assertions.assertThrows(NullPointerException.class, () -> {
            game.setTransceiver(null);
        });
    }

    @Test
    void addPlayer_signals_correctOutput () throws IllegalFlowException, PlayerAlreadyInGameException {
        AtomicBoolean called = new AtomicBoolean(false);
        AtomicReference<String> username = new AtomicReference<>("");

        PlayerHasJoinEventData.castEventReceiver(transceiver).registerListener(
                event -> {
                    assert !called.get();
                    called.set(true);
                    username.set(event.getUsername());
                }
        );

        this.game.addPlayer("Giacomo");

        Assertions.assertTrue(called.get());
        Assertions.assertEquals("Giacomo", username.get());

        called.set(false);
        this.game.addPlayer("Michele");
        Assertions.assertEquals("Michele", username.get());
    }

    @Test
    void addPlayer_forReconnection_correctOutput() throws IllegalFlowException, PlayerAlreadyInGameException {
        game.addPlayer("Giacomo");
        game.addPlayer("Michele");

        AtomicReference<String> username = new AtomicReference<>("");

        PlayerHasDisconnectedEventData.castEventReceiver(transceiver).registerListener(
                event -> {
                    username.set(event.getUsername());
                }
        );

        game.startGame();

        game.disconnectPlayer("Giacomo");

        Assertions.assertEquals("Giacomo", username.get());
        Assertions.assertEquals("Michele", game.getCurrentPlayer().getUsername());
    }

    @Test
    void getLastPlayer__correctOutput() throws IllegalFlowException, PlayerAlreadyInGameException {
        game.addPlayer("Giacomo");
        game.addPlayer("Michele");
        game.addPlayer("Cristiano");

        Assertions.assertThrows(IllegalFlowException.class, () -> {
            game.getLastPlayer();
        });

        game.startGame();

        Assertions.assertEquals("Cristiano", game.getLastPlayer().getUsername());
    }
}

