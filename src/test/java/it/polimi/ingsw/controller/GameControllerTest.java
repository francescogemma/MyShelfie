package it.polimi.ingsw.controller;

import it.polimi.ingsw.event.EventTransceiver;
import it.polimi.ingsw.event.LocalEventTransceiver;
import it.polimi.ingsw.event.data.game.PlayerHasJoinEventData;
import it.polimi.ingsw.model.game.Game;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Giacomo Groppi
 * */
class GameControllerTest {
    private GameController gameController;


    @BeforeEach
    void setUp () {
        this.gameController = new GameController(new Game("Testing", "Giacomo"));
    }

    private VirtualView getNewVirtualView () {
        EventTransceiver transceiver = new LocalEventTransceiver();
        return new VirtualView(transceiver);
    }

    @Test
    void join_nullPointer_throwNullPointerException () {
        Assertions.assertThrows(NullPointerException.class, () -> {
            this.gameController.join(null, "Ciao");
        });

        Assertions.assertThrows(NullPointerException.class, () -> {
            this.gameController.join(new LocalEventTransceiver(), null);
        });

        Assertions.assertThrows(NullPointerException.class, () -> {
            this.gameController.join(null, null);
        });
    }

    @Test
    void exitGame_playerNotInGame_shouldThrow () {
        gameController.join(new LocalEventTransceiver(), "Giacomo");

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            gameController.exitGame("Michele");
        });
    }
}
