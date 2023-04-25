package it.polimi.ingsw.event;

import it.polimi.ingsw.event.data.EventData;
import it.polimi.ingsw.event.receiver.EventListener;

import java.util.*;

public class LocalEventTransceiver implements EventTransceiver {
    private final List<EventListener<EventData>> listeners = new ArrayList<>();

    private final Object lock = new Object();

    @Override
    public void registerListener(EventListener<EventData> listener) {
        synchronized (lock) {
            listeners.add(listener);
        }
    }

    @Override
    public void broadcast(EventData data) {
        synchronized (lock) {
            for (EventListener<EventData> listener : listeners) {
                listener.handle(data);
            }
        }
    }
}
