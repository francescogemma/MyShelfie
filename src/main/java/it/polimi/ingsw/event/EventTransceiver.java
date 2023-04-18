package it.polimi.ingsw.event;

import it.polimi.ingsw.event.data.EventData;
import it.polimi.ingsw.event.receiver.EventReceiver;
import it.polimi.ingsw.event.transmitter.EventTransmitter;

public interface EventTransceiver extends EventTransmitter, EventReceiver<EventData> {
}
