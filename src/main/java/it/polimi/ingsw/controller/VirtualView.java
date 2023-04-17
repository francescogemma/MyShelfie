package it.polimi.ingsw.controller;

import it.polimi.ingsw.event.MockNetworkEventTransceiver;
import it.polimi.ingsw.event.data.EventData;
import it.polimi.ingsw.event.receiver.EventReceiver;
import it.polimi.ingsw.event.transmitter.EventTransmitter;

public class VirtualView {
    private GameController gameController;
    private String username;
    private MockNetworkEventTransceiver transceiver;

    public String getUsername() {
        return username;
    }

    public EventTransmitter getNetworkTransmitter () {
        return this.transceiver;
    }

    public EventReceiver<EventData> getNetworkReceiver () {
        return this.transceiver;
    }

    public void setGameController(GameController gameController, MockNetworkEventTransceiver transceiver) {
        this.gameController = gameController;

        this.transceiver = transceiver;

    }
}
