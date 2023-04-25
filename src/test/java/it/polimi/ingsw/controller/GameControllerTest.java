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

    /*
    @BeforeEach
    void setUp () {
        this.gameController = new GameController(new Game("Testing"));
    }

    private VirtualView getNewVirtualView () {

        EventTransceiver transceiver = new LocalEventTransceiver();
        return new VirtualView(transceiver);
    }

    @Test
    void join_nullPointer_throwNullPointerException () {
        Assertions.assertThrows(NullPointerException.class, () -> {
            this.gameController.join(null);
        });
    }

    @Test
    void join_singalsEmitted_correctOutput () {
        VirtualView virtualViewGiacomo = this.getNewVirtualView();
        VirtualView virtualViewMichele = this.getNewVirtualView();

        virtualViewGiacomo.setUsername("Giacomo");
        virtualViewMichele.setUsername("Michele");

        AtomicReference<String> usernameGiacomo = new AtomicReference<>(null);

        AtomicBoolean callGiacomo = new AtomicBoolean(false);
        AtomicBoolean callMichele = new AtomicBoolean(false);

        virtualViewMichele.setGameController(gameController);

        // we synchronize on player "Giacomo"
        virtualViewGiacomo.getNetworkReceiver().registerListener(event -> {
            Assertions.assertEquals(PlayerHasJoinEventData.ID, event.getId());
            Assertions.assertEquals("Michele", ((PlayerHasJoinEventData) event).getUsername());
            callGiacomo.set(true);
        });

        // we synchronize on player "Michele"
        virtualViewMichele.getNetworkReceiver().registerListener(event -> {
            callMichele.set(true);
        });

        callGiacomo.set(false);
        callMichele.set(false);
        usernameGiacomo.set(null);

        this.gameController.join(virtualViewGiacomo);

        Assertions.assertTrue(callGiacomo.get() == false);
        Assertions.assertTrue(callMichele.get() == false);

        this.gameController.join(virtualViewMichele);

        Assertions.assertTrue(callGiacomo.get() == true);
        Assertions.assertTrue(callMichele.get() == false);
    }

    @Test
    void join_fivePlayers_correctOutput () {
        VirtualView virtualViewGiacomo = this.getNewVirtualView();
        VirtualView virtualViewMichele = this.getNewVirtualView();
        VirtualView virtualViewCristiano = this.getNewVirtualView();
        VirtualView virtualViewFrancesco = this.getNewVirtualView();
        VirtualView virtualViewPluto = this.getNewVirtualView();

        virtualViewGiacomo.setUsername("Giacomo");
        virtualViewMichele.setUsername("Michele");
        virtualViewCristiano.setUsername("Cristiano");
        virtualViewFrancesco.setUsername("Francesco");
        virtualViewPluto.setUsername("Pluto");

        AtomicBoolean callGiacomo = new AtomicBoolean(false);
        AtomicBoolean callMichele = new AtomicBoolean(false);
        AtomicBoolean callCrisitano = new AtomicBoolean(false);
        AtomicBoolean callFrancesco = new AtomicBoolean(false);

        virtualViewMichele.setGameController(gameController);

        // we synchronize on player "Giacomo"
        virtualViewGiacomo.getNetworkReceiver().registerListener(event -> {
            Assertions.assertEquals(PlayerHasJoinEventData.ID, event.getId());
            callGiacomo.set(true);
        });

        // we synchronize on player "Michele"
        virtualViewMichele.getNetworkReceiver().registerListener(event -> {
            callMichele.set(true);
        });

        // we synchronize on player "Cristiano"
        virtualViewCristiano.getNetworkReceiver().registerListener(event -> {
            callCrisitano.set(true);
        });

        // we synchronize on player "Francesco"
        virtualViewFrancesco.getNetworkReceiver().registerListener(event -> {
            callFrancesco.set(true);
        });

        this.gameController.join(virtualViewGiacomo);
        this.gameController.join(virtualViewMichele);
        this.gameController.join(virtualViewCristiano);
        this.gameController.join(virtualViewFrancesco);

        callGiacomo.set(false);
        callMichele.set(false);
        callCrisitano.set(false);
        callFrancesco.set(false);
        this.gameController.join(virtualViewPluto);

        Assertions.assertFalse(callGiacomo.get());
        Assertions.assertFalse(callMichele.get());
        Assertions.assertFalse(callCrisitano.get());
        Assertions.assertFalse(callFrancesco.get());

    }
    */
}
