package it.polimi.ingsw.event;

import it.polimi.ingsw.event.data.EventData;
import it.polimi.ingsw.event.receiver.EventReceiver;
import it.polimi.ingsw.event.transmitter.EventTransmitter;

/**
 * Represents an object which can both broadcast and receive {@link EventData}. That is an object which is
 * both an {@link EventTransmitter} and an {@link EventReceiver}.
 * Usually different instances of the same concrete EventTransceiver are coupled: the events broadcast by one are
 * received by the other and vice-versa.
 *
 * @author Giacomo Groppi
 */
public interface EventTransceiver extends EventTransmitter, EventReceiver<EventData> {
}
