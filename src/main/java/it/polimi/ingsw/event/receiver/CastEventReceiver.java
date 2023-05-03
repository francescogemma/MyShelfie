package it.polimi.ingsw.event.receiver;

import it.polimi.ingsw.event.data.EventData;

public class CastEventReceiver<T extends EventData> implements EventReceiver<T> {
    private final EventReceiver<EventData> receiver;

    private final String eventId;

    public CastEventReceiver(String eventId, EventReceiver<EventData> receiver) {
        this.eventId = eventId;
        this.receiver = receiver;
    }

    @Override
    public void registerListener(EventListener<T> listener) {
        receiver.registerListener(data -> {
            if (data.getId().equals(eventId)) {
                try {
                    listener.handle((T) data);
                } catch (ClassCastException e) {
                    throw new IllegalStateException("Event ID should always match event data concrete type");
                }
            }
        });
    }

    @Override
    public void unregisterListener(EventListener<T> listener) {
        receiver.unregisterListener((EventListener<EventData>) listener);
    }
}
