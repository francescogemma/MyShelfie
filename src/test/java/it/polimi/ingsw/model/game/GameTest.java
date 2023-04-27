package it.polimi.ingsw.model.game;

import it.polimi.ingsw.event.LocalEventTransceiver;
import it.polimi.ingsw.event.data.EventData;
import it.polimi.ingsw.event.data.game.*;
import it.polimi.ingsw.model.bag.Bag;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.board.BoardView;
import it.polimi.ingsw.model.board.FullSelectionException;
import it.polimi.ingsw.model.board.IllegalExtractionException;
import it.polimi.ingsw.model.tile.Tile;
import it.polimi.ingsw.utils.Coordinate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Tester for Game
 * @author Giacomo Groppi
 * */
class GameTest {
    private Game game;
    private LocalEventTransceiver transceiver;
    private final String creator = "Giacomo";

    private static final int numberOfRun = 1000;

    private Board getBoardFull (int numberOfPlayers) {
        Bag bag = new Bag();
        Board board = new Board();

        while (!bag.isEmpty()) {
            Tile t = bag.getRandomTile();
            try {
                board.fillRandomly(t, numberOfPlayers);
            } catch (Exception e) {
                break;
            }
        }
        return board;
    }

    private boolean isPresentEvent(List<EventData> events, String id) {
        for (EventData event: events) {
            if (event.getId().equals(id))
                return true;
        }
        return false;
    }

    @BeforeEach
    void setUp () {
        game = new Game("testing", creator);
        transceiver = new LocalEventTransceiver();
        game.setTransceiver(transceiver);
    }

    @Test
    void getName__correctOutput() {
        Game g2 = new Game("testing2", creator);
        Assertions.assertEquals("testing2", g2.getName());
    }

    @Test
    void constructor__throwNullPointerException() {
        Assertions.assertThrows(NullPointerException.class, () -> {
            new Game(null, creator);
        });

        Assertions.assertThrows(NullPointerException.class, () -> {
            new Game("prova", null);
        });

        Assertions.assertThrows(NullPointerException.class, () -> {
            new Game(null, null);
        });
    }

    @Test
    void contructor__throwIllegalArgumentException () {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new Game("", "");
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new Game("foo", "");
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new Game("", "bar");
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

        game.startGame("Giacomo");

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
            game.startGame("Giacomo");
        });
    }

    @Test
    void startGame_0Players_throwIllegalFlowException () {
        Assertions.assertThrows(IllegalFlowException.class, () -> {
            game.startGame("");
        });
    }

    @Test
    void getCurrentPlayer__correctOutput () throws IllegalFlowException, PlayerAlreadyInGameException {
        game.addPlayer("Giacomo");
        game.addPlayer("Michele");

        game.startGame("Giacomo");
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
        this.game = new Game("testing", creator);
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

        game.startGame("Giacomo");

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

        game.startGame("Giacomo");

        Assertions.assertEquals("Cristiano", game.getLastPlayer().getUsername());
    }

    @Test
    void disconnectPlayer__correctOutput() throws IllegalFlowException, PlayerAlreadyInGameException {
        game.addPlayer("Giacomo");
        game.addPlayer("Francesco");
        game.addPlayer("Cristiano");
        game.addPlayer("Michele");


        game.startGame("Giacomo");

        Assertions.assertEquals("Giacomo", game.getCurrentPlayer().getUsername());


        game.disconnectPlayer("Giacomo");

        Assertions.assertEquals("Francesco", game.getCurrentPlayer().getUsername());
    }

    @Test
    void disconnectPlayer_twoPlayerDisconnected_correctOutput() throws IllegalFlowException, PlayerAlreadyInGameException {
        game.addPlayer("Giacomo");
        game.addPlayer("Francesco");
        game.addPlayer("Cristiano");
        game.addPlayer("Michele");


        game.startGame("Giacomo");

        Assertions.assertEquals("Giacomo", game.getCurrentPlayer().getUsername());


        game.disconnectPlayer("Giacomo");
        game.disconnectPlayer("Francesco");

        Assertions.assertEquals("Cristiano", game.getCurrentPlayer().getUsername());
    }

    @Test
    void disconnectPlayer_threePlayerDisconnected_correctOutput() throws IllegalFlowException, PlayerAlreadyInGameException {
        game.addPlayer("Giacomo");
        game.addPlayer("Francesco");
        game.addPlayer("Cristiano");
        game.addPlayer("Michele");


        game.startGame("Giacomo");

        Assertions.assertEquals("Giacomo", game.getCurrentPlayer().getUsername());


        game.disconnectPlayer("Giacomo");
        game.disconnectPlayer("Francesco");
        game.disconnectPlayer("Cristiano");

        Assertions.assertEquals("Michele", game.getCurrentPlayer().getUsername());
    }

    @Test
    void disconnectPlayer_fourPlayerDisconnected_correctOutput() throws IllegalFlowException, PlayerAlreadyInGameException {
        game.addPlayer("Giacomo");
        game.addPlayer("Francesco");
        game.addPlayer("Cristiano");
        game.addPlayer("Michele");


        game.startGame("Giacomo");

        Assertions.assertEquals("Giacomo", game.getCurrentPlayer().getUsername());

        game.disconnectPlayer("Giacomo");
        game.disconnectPlayer("Francesco");
        game.disconnectPlayer("Cristiano");
        game.disconnectPlayer("Michele");

        Assertions.assertEquals("Giacomo", game.getCurrentPlayer().getUsername());
    }

    @Test
    void startGame_signals_correctOutput () throws IllegalFlowException, PlayerAlreadyInGameException {
        AtomicBoolean call = new AtomicBoolean(false);
        game.addPlayer("Giacomo");
        game.addPlayer("Michele");

        GameHasStartedEventData.castEventReceiver(transceiver).registerListener(event -> {
            call.set(true);
        });

        game.startGame("Giacomo");

        Assertions.assertTrue(call.get());
    }

    @Test
    void selectTile_signalsEmitted_correctOutput() throws IllegalFlowException, PlayerAlreadyInGameException, IllegalExtractionException, FullSelectionException {
        AtomicBoolean call = new AtomicBoolean(false);
        AtomicReference<BoardView> boardViewCall = new AtomicReference<>(null);

        game.addPlayer("Giacomo");
        game.addPlayer("Michele");
        game.addPlayer("Cristiano");

        BoardChangedEventData.castEventReceiver(transceiver).registerListener(event -> {
            call.set(true);
            boardViewCall.set(event.board());
        });

        try {
            game.selectTile("Giacomo", new Coordinate(4, 0));
        } catch (IllegalFlowException e) {

        }

        Assertions.assertFalse(call.get());

        game.startGame("Giacomo");

        call.set(false);
        try {
            game.selectTile("Michele", new Coordinate(4, 0));
        } catch (IllegalFlowException e) {

        }

        Assertions.assertFalse(call.get());

        game.selectTile("Giacomo", new Coordinate(4, 0));

        Assertions.assertTrue(call.get());
        Assertions.assertEquals(boardViewCall.get(), this.game.getBoard());

        call.set(false);
        boardViewCall.set(null);

        try {
            game.selectTile("Giacomo", new Coordinate(4, 3));
            assert false: "This functions should throw IllegalExtractionException";
        } catch (IllegalExtractionException e) {

        }

        Assertions.assertFalse(call.get());
    }

    @Test
    void insertTile_signalsEmitted_correctOutput () throws IllegalFlowException, PlayerAlreadyInGameException, IllegalExtractionException, FullSelectionException {
        AtomicBoolean call = new AtomicBoolean(false);
        AtomicReference<BoardView> boardViewCall = new AtomicReference<>(null);

        game.addPlayer("Giacomo");
        game.addPlayer("Michele");
        game.addPlayer("Cristiano");

        BoardChangedEventData.castEventReceiver(transceiver).registerListener(event -> {
            call.set(true);
            boardViewCall.set(event.board());
        });

        game.startGame("Giacomo");

        Assertions.assertThrows(IllegalExtractionException.class, () -> {
            game.insertTile("Giacomo", 0);
        });

        game.selectTile("Giacomo", new Coordinate(4, 0));
        game.selectTile("Giacomo", new Coordinate(5, 0));

        Assertions.assertThrows(IllegalFlowException.class, () -> {
            game.insertTile("Michele", 0);
        });

        game.insertTile("Giacomo", 4);

        Assertions.assertTrue(call.get());
        Assertions.assertEquals(game.getBoard(), boardViewCall.get());
    }

    @Test
    void addPlayer_signalsEmitted_correctOutput() throws IllegalFlowException, PlayerAlreadyInGameException {
        transceiver.registerListener(event -> {
            Assertions.assertEquals(PlayerHasJoinEventData.ID, event.getId());
            Assertions.assertEquals("Giacomo", ((PlayerHasJoinEventData) event).getUsername());
        });

        game.addPlayer("Giacomo");
    }

    @Test
    void addPlayer_playerAlreadyInGameButDisconnected_correctOutput () throws IllegalFlowException, PlayerAlreadyInGameException {
        AtomicBoolean set = new AtomicBoolean(false);
        game.addPlayer("Giacomo");
        game.addPlayer("Michele");
        game.addPlayer("Cristiano");

        transceiver.registerListener(event -> {
            if (set.get()) {
                Assertions.assertEquals(PlayerHasJoinEventData.ID, event.getId());
                Assertions.assertEquals("Giacomo", ((PlayerHasJoinEventData) event).getUsername());
            }
        });

        game.startGame("Giacomo");

        game.disconnectPlayer("Giacomo");

        set.set(true);
        game.addPlayer("Giacomo");
    }

    @Test
    void disconnectPlayer_singleSignalsEmitted_correctOutput() throws IllegalFlowException, PlayerAlreadyInGameException {
        AtomicBoolean setPlayer = new AtomicBoolean(false);
        AtomicBoolean setCurrent = new AtomicBoolean(false);

        AtomicBoolean callPlayerHasJoinEventData = new AtomicBoolean(false);
        AtomicBoolean callCurrentPlayerChangedEventData = new AtomicBoolean(false);

        game.addPlayer("Giacomo");
        game.addPlayer("Michele");
        game.addPlayer("Cristiano");

        PlayerHasDisconnectedEventData.castEventReceiver(transceiver).registerListener(event -> {
            if (setPlayer.get()) {
                Assertions.assertEquals("Giacomo", event.getUsername());
                callPlayerHasJoinEventData.set(true);
            }
        });

        CurrentPlayerChangedEventData.castEventReceiver(transceiver).registerListener(event -> {
            if (setCurrent.get()) {
                Assertions.assertEquals("Michele", event.getUsername());
                callCurrentPlayerChangedEventData.set(true);
            }
        });

        game.startGame("Giacomo");

        setCurrent.set(true);
        game.disconnectPlayer("Giacomo");

        Assertions.assertTrue(callCurrentPlayerChangedEventData.get());

        game.disconnectPlayer("Giacomo");

        Assertions.assertFalse(callPlayerHasJoinEventData.get());
    }

    @Test
    void hasPlayerDisconnected_onePlayerDisconnected_correctOutput () throws IllegalFlowException, PlayerAlreadyInGameException {
        game.addPlayer("Giacomo");
        game.addPlayer("Michele");

        game.disconnectPlayer("Giacomo");

        Assertions.assertTrue(game.hasPlayerDisconnected());
        game.disconnectPlayer("Michele");
        Assertions.assertTrue(game.hasPlayerDisconnected());

        game.addPlayer("Giacomo");
        game.addPlayer("Michele");

        Assertions.assertFalse(game.hasPlayerDisconnected());
    }

    @RepeatedTest(numberOfRun)
    void isOver_signalsEmittedLastPlayerCompleteBookshelf_correctOutput () throws IllegalFlowException, PlayerAlreadyInGameException, IllegalExtractionException, FullSelectionException {
        final String usernameUser1 = "Giacomo";
        final String usernameUser2 = "Michele";

        game.addPlayer(usernameUser1);
        game.addPlayer(usernameUser2);

        game.startGame("Giacomo");

        game.selectTile(usernameUser1, new Coordinate(4, 0));
        game.insertTile(usernameUser1, 0);

        game.selectTile(usernameUser2, new Coordinate(3, 1));
        game.selectTile(usernameUser2, new Coordinate(4, 1));
        game.selectTile(usernameUser2, new Coordinate(5, 1));

        game.insertTile(usernameUser2, 0);

        game.selectTile(usernameUser1, new Coordinate(2, 2));
        game.insertTile(usernameUser1, 0);

        game.selectTile(usernameUser2, new Coordinate(3, 2));
        game.selectTile(usernameUser2, new Coordinate(4, 2));
        game.selectTile(usernameUser2, new Coordinate(5, 2));
        game.insertTile(usernameUser2, 0);

        game.selectTile(usernameUser1, new Coordinate(7, 3));
        game.insertTile(usernameUser1, 0);

        game.selectTile(usernameUser2, new Coordinate(6, 3));
        game.selectTile(usernameUser2, new Coordinate(5, 3));
        game.selectTile(usernameUser2, new Coordinate(4, 3));
        game.insertTile(usernameUser2, 1);

        game.selectTile(usernameUser1, new Coordinate(1, 4));
        game.insertTile(usernameUser1, 0);

        game.selectTile(usernameUser2, new Coordinate(1, 3));
        game.selectTile(usernameUser2, new Coordinate(2, 3));
        game.selectTile(usernameUser2, new Coordinate(3, 3));
        game.insertTile(usernameUser2, 1);

        game.selectTile(usernameUser1, new Coordinate(3, 7));
        game.insertTile(usernameUser1, 0);

        game.selectTile(usernameUser2, new Coordinate(2, 4));
        game.selectTile(usernameUser2, new Coordinate(3, 4));
        game.selectTile(usernameUser2, new Coordinate(4, 4));
        game.insertTile(usernameUser2, 2);

        game.selectTile(usernameUser1, new Coordinate(4, 7));
        game.insertTile(usernameUser1, 0);

        game.selectTile(usernameUser2, new Coordinate(2, 5));
        game.selectTile(usernameUser2, new Coordinate(3, 5));
        game.selectTile(usernameUser2, new Coordinate(4, 5));
        game.insertTile(usernameUser2, 2);

        game.selectTile(usernameUser1, new Coordinate(3, 6));
        game.selectTile(usernameUser1, new Coordinate(4, 6));
        game.insertTile(usernameUser1, 1);

        game.selectTile(usernameUser2, new Coordinate(5, 6));
        game.selectTile(usernameUser2, new Coordinate(5, 5));
        game.selectTile(usernameUser2, new Coordinate(5, 4));
        game.insertTile(usernameUser2, 3);

        game.selectTile(usernameUser1, new Coordinate(6, 4));
        game.selectTile(usernameUser1, new Coordinate(6, 5));
        game.insertTile(usernameUser1, 2);

        game.selectTile(usernameUser2, new Coordinate(7, 4));
        game.selectTile(usernameUser2, new Coordinate(7, 5));
        game.insertTile(usernameUser2, 3);

        game.selectTile(usernameUser1, new Coordinate(4, 0));
        game.insertTile(usernameUser1, 1);

        game.selectTile(usernameUser2, new Coordinate(3, 1));
        game.selectTile(usernameUser2, new Coordinate(4, 1));
        game.selectTile(usernameUser2, new Coordinate(5, 1));

        game.insertTile(usernameUser2, 4);

        game.selectTile(usernameUser1, new Coordinate(2, 2));
        game.insertTile(usernameUser1, 3);

        game.selectTile(usernameUser2, new Coordinate(3, 2));
        game.selectTile(usernameUser2, new Coordinate(4, 2));
        game.selectTile(usernameUser2, new Coordinate(5, 2));
        game.insertTile(usernameUser2, 4);

        game.selectTile(usernameUser1, new Coordinate(7, 3));
        game.insertTile(usernameUser1, 3);

        AtomicBoolean call = new AtomicBoolean(false);
        GameOverEventData.castEventReceiver(transceiver).registerListener(event -> {
            call.set(true);
            // we can't test the winner name because it depends on the tile in the board.
        });

        game.selectTile(usernameUser2, new Coordinate(6, 3));
        try {
            game.insertTile(usernameUser2, 3);
        } catch (IllegalStateException e) {
            Assertions.fail();
        }

        Assertions.assertTrue(call.get());
        Assertions.assertTrue(game.isOver());
    }

    @RepeatedTest(numberOfRun)
    void isOver_signalsEmittedFirstPlayerCompleteBookshelf_correctOutput () throws IllegalFlowException, PlayerAlreadyInGameException, IllegalExtractionException, FullSelectionException {
        final String usernameUser1 = "Giacomo";
        final String usernameUser2 = "Michele";

        this.game = new Game("Testing", usernameUser2);
        this.transceiver = new LocalEventTransceiver();
        this.game.setTransceiver(transceiver);

        game.addPlayer(usernameUser2);
        game.addPlayer(usernameUser1);

        game.startGame("Michele");

        game.disconnectPlayer(usernameUser2);

        game.selectTile(usernameUser1, new Coordinate(4, 0));
        game.insertTile(usernameUser1, 0);

        game.addPlayer(usernameUser2);

        game.selectTile(usernameUser2, new Coordinate(3, 1));
        game.selectTile(usernameUser2, new Coordinate(4, 1));
        game.selectTile(usernameUser2, new Coordinate(5, 1));
        game.insertTile(usernameUser2, 0);

        game.selectTile(usernameUser1, new Coordinate(2, 2));
        game.insertTile(usernameUser1, 0);

        game.selectTile(usernameUser2, new Coordinate(3, 2));
        game.selectTile(usernameUser2, new Coordinate(4, 2));
        game.selectTile(usernameUser2, new Coordinate(5, 2));
        game.insertTile(usernameUser2, 0);

        game.selectTile(usernameUser1, new Coordinate(7, 3));
        game.insertTile(usernameUser1, 0);

        game.selectTile(usernameUser2, new Coordinate(6, 3));
        game.selectTile(usernameUser2, new Coordinate(5, 3));
        game.selectTile(usernameUser2, new Coordinate(4, 3));
        game.insertTile(usernameUser2, 1);

        game.selectTile(usernameUser1, new Coordinate(1, 4));
        game.insertTile(usernameUser1, 0);

        game.selectTile(usernameUser2, new Coordinate(1, 3));
        game.selectTile(usernameUser2, new Coordinate(2, 3));
        game.selectTile(usernameUser2, new Coordinate(3, 3));
        game.insertTile(usernameUser2, 1);

        game.selectTile(usernameUser1, new Coordinate(3, 7));
        game.insertTile(usernameUser1, 0);

        game.selectTile(usernameUser2, new Coordinate(2, 4));
        game.selectTile(usernameUser2, new Coordinate(3, 4));
        game.selectTile(usernameUser2, new Coordinate(4, 4));
        game.insertTile(usernameUser2, 2);

        game.selectTile(usernameUser1, new Coordinate(4, 7));
        game.insertTile(usernameUser1, 0);

        game.selectTile(usernameUser2, new Coordinate(2, 5));
        game.selectTile(usernameUser2, new Coordinate(3, 5));
        game.selectTile(usernameUser2, new Coordinate(4, 5));
        game.insertTile(usernameUser2, 2);

        game.selectTile(usernameUser1, new Coordinate(3, 6));
        game.selectTile(usernameUser1, new Coordinate(4, 6));
        game.insertTile(usernameUser1, 1);

        game.selectTile(usernameUser2, new Coordinate(5, 6));
        game.selectTile(usernameUser2, new Coordinate(5, 5));
        game.selectTile(usernameUser2, new Coordinate(5, 4));
        game.insertTile(usernameUser2, 3);

        game.selectTile(usernameUser1, new Coordinate(6, 4));
        game.selectTile(usernameUser1, new Coordinate(6, 5));
        game.insertTile(usernameUser1, 2);

        game.selectTile(usernameUser2, new Coordinate(7, 4));
        game.selectTile(usernameUser2, new Coordinate(7, 5));
        game.insertTile(usernameUser2, 3);

        game.selectTile(usernameUser1, new Coordinate(4, 0));
        game.insertTile(usernameUser1, 1);

        game.selectTile(usernameUser2, new Coordinate(3, 1));
        game.selectTile(usernameUser2, new Coordinate(4, 1));
        game.selectTile(usernameUser2, new Coordinate(5, 1));

        game.insertTile(usernameUser2, 4);

        game.selectTile(usernameUser1, new Coordinate(2, 2));
        game.insertTile(usernameUser1, 3);

        game.selectTile(usernameUser2, new Coordinate(3, 2));
        game.selectTile(usernameUser2, new Coordinate(4, 2));
        game.selectTile(usernameUser2, new Coordinate(5, 2));
        game.insertTile(usernameUser2, 4);

        game.selectTile(usernameUser1, new Coordinate(7, 3));
        game.insertTile(usernameUser1, 3);

        AtomicBoolean call = new AtomicBoolean(false);
        GameOverEventData.castEventReceiver(transceiver).registerListener(event -> {
            call.set(true);
            // we can't test the winner name because it depends on the tile in the board.

        });

        game.selectTile(usernameUser2, new Coordinate(6, 3));
        game.insertTile(usernameUser2, 3);

        Assertions.assertFalse(game.isOver());

        game.selectTile(usernameUser1, new Coordinate(4, 7));
        game.insertTile(usernameUser1, 3);

        Assertions.assertTrue(call.get());
        Assertions.assertTrue(game.isOver());

        Assertions.assertThrows(IllegalFlowException.class, () -> {
            game.selectTile(usernameUser1, new Coordinate(0, 0));
        });

        Assertions.assertThrows(IllegalFlowException.class, () -> {
            game.forgetLastSelection(usernameUser1, new Coordinate(4, 0));
        });
    }

    @Test
    void forgetLastSelection_correctDeselect_correctOutput() throws IllegalFlowException, PlayerAlreadyInGameException, IllegalExtractionException, FullSelectionException {
        List<EventData> events = new ArrayList<>();

        BoardChangedEventData.castEventReceiver(transceiver).registerListener(events::add);
        PlayerHasDeselectTile.castEventReceiver(transceiver).registerListener(events::add);

        final String username1 = "Giacomo";
        final String username2 = "Michele";

        this.game.addPlayer(username1);
        this.game.addPlayer(username2);

        Assertions.assertThrows(IllegalFlowException.class, () -> {
            game.forgetLastSelection(username1, new Coordinate(4, 0));
        });

        this.game.startGame("Giacomo");

        this.game.selectTile(username1, new Coordinate(4, 0));

        // not current player
        Assertions.assertThrows(IllegalFlowException.class, () -> {
            game.forgetLastSelection(username2, new Coordinate(4, 0));
        });

        this.game.forgetLastSelection(username1, new Coordinate(4, 0));

        Assertions.assertTrue(this.isPresentEvent(events, BoardChangedEventData.ID));
        Assertions.assertTrue(this.isPresentEvent(events, PlayerHasDeselectTile.ID));
    }

    @Test
    void startGame_incorrectOwnership_correctOutput() throws IllegalFlowException, PlayerAlreadyInGameException {
        game = new Game("testing", "Giacomo");

        game.setTransceiver(new LocalEventTransceiver());

        game.addPlayer("Giacomo");
        game.addPlayer("Michele");

        Assertions.assertThrows(IllegalFlowException.class, () -> {
            game.startGame("Michele");
        });

        game.startGame("Giacomo");
    }
}
