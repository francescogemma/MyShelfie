package it.polimi.ingsw.event;

public class CastEventReceiver<T> implements EventReceiver<T> {
    private final EventReceiver<Object> receiver;

    public CastEventReceiver(EventReceiver<Object> receiver) {
        this.receiver = receiver;
    }

    @Override
    public void registerListener(String eventId, EventListener<T> listener) {
        receiver.registerListener(eventId, eventData -> {
            try {
                listener.handle((T) eventData);
            } catch (ClassCastException e) { }
        });
    }
}
