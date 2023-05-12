package it.polimi.ingsw.event.transmitter;

import it.polimi.ingsw.event.data.EventData;

/**
 * Represent an object which can broadcast {@link EventData}. A {@link EventTransmitter} is coupled with an
 * {@link it.polimi.ingsw.event.receiver.EventReceiver} which receives the broadcast event and notifies all
 * its listeners. Event broadcast can be local (a component notifies another on the same machine) or through
 * network (allows communication between objects "living" on different machines).
 *
 * @author Cristiano Migali
 */
public interface EventTransmitter {
    /**
     * Broadcasts the given {@link EventData} to the coupled {@link it.polimi.ingsw.event.receiver.EventReceiver}.
     *
     * @param data is the {@link EventData} which will be broadcast.
     */
    void broadcast(EventData data);
}
