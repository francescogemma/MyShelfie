package it.polimi.ingsw.event.transmitter;

import it.polimi.ingsw.event.data.EventData;

public interface EventTransmitter {
    void broadcast(EventData data);
}
