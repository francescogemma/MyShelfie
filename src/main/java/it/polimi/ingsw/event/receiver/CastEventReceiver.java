package it.polimi.ingsw.event.receiver;

import it.polimi.ingsw.event.data.EventData;

import java.util.HashMap;
import java.util.Map;

public class CastEventReceiver<T extends EventData> implements EventReceiver<T> {
    private final EventReceiver<EventData> receiver;

    private final String eventId;

    private final Object lock = new Object();

    private final Map<EventListener<T>, EventListener<EventData>> listeners = new HashMap<>();

    public CastEventReceiver(String eventId, EventReceiver<EventData> receiver) {
        this.eventId = eventId;
        this.receiver = receiver;
    }

    @Override
    public void registerListener(EventListener<T> listener) {
        synchronized (lock) {
            listeners.put(listener, data -> {
                if (data.getId().equals(eventId)) {
                    try {
                        listener.handle((T) data);
                    } catch (ClassCastException e) {
                        throw new IllegalStateException("Event ID should always match event data concrete type");
                    }
                }
            });

            receiver.registerListener(listeners.get(listener));
        }
    }

    @Override
    public void unregisterListener(EventListener<T> listener) {
        synchronized (lock) {
            receiver.unregisterListener(listeners.remove(listener));
        }
    }
}
