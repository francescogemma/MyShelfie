package it.polimi.ingsw.controller;

import it.polimi.ingsw.event.EventTransceiver;
import it.polimi.ingsw.event.data.EventData;
import it.polimi.ingsw.event.receiver.EventReceiver;
import it.polimi.ingsw.event.transmitter.EventTransmitter;

public class VirtualView {
    private GameController gameController;
    private String username;
    private final EventTransceiver transceiver;

    public VirtualView(EventTransceiver transceiver) {
        if (transceiver == null)
            throw new NullPointerException();
        this.transceiver = transceiver;
        gameController = null;
    }

    public void setUsername (String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public EventTransmitter getNetworkTransmitter () {
        return this.transceiver;
    }

    public EventReceiver<EventData> getNetworkReceiver () {
        return this.transceiver;
    }

    public boolean isInGame() {
        return this.gameController != null;
    }

    public void setGameController(GameController gameController) {
        this.gameController = gameController;
    }
}
