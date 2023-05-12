package it.polimi.ingsw.event;

import it.polimi.ingsw.event.data.EventData;
import it.polimi.ingsw.event.receiver.EventListener;

import java.util.*;

/**
 * Is an {@link EventTransceiver} which allows broadcast and reception of events between components living in the same
 * machine.
 */
public class LocalEventTransceiver implements EventTransceiver {
    /**
     * Is the list of listeners registered on the transceiver.
     */
    private final List<EventListener<EventData>> listeners = new ArrayList<>();

    /**
     * Lock object used to synchronize listeners registrations, removal and broadcast.
     */
    private final Object lock = new Object();

    @Override
    public void registerListener(EventListener<EventData> listener) {
        synchronized (lock) {
            listeners.add(listener);
        }
    }

    @Override
    public void unregisterListener(EventListener<EventData> listener) {
        synchronized (lock) {
            listeners.remove(listener);
        }
    }

    @Override
    public void unregisterAllListeners() {
        synchronized (lock) {
            listeners.clear();
        }
    }

    @Override
    public void broadcast(EventData data) {
        synchronized (lock) {
            List<EventListener<EventData>> listenersCopy = new ArrayList<>(listeners);

            for (EventListener<EventData> listener : listenersCopy) {
                listener.handle(data);
            }
        }
    }
}
