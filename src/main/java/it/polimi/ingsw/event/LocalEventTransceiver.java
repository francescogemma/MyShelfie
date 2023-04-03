package it.polimi.ingsw.event;

import java.util.*;

public class LocalEventTransceiver implements EventTransmitter<Object>, EventReceiver<Object> {
    private final Map<String, List<EventListener<Object>>> listeners = new HashMap<>();

    @Override
    public void registerListener(String eventId, EventListener<Object> listener) {
        listeners.computeIfAbsent(eventId, id -> new ArrayList<>());

        listeners.get(eventId).add(listener);
    }

    @Override
    public void broadcast(Event<Object> event) {
        for (EventListener<Object> listener : listeners.get(event.getId())) {
            listener.handle(event.getData());
        }
    }
}
