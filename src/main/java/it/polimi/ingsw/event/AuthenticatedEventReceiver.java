package it.polimi.ingsw.event;

import java.util.function.BiPredicate;

public class AuthenticatedEventReceiver<T> implements EventReceiver<AuthenticatedEventData<T>> {
    private final EventReceiver<Object> receiver;

    public AuthenticatedEventReceiver(EventReceiver<Object> receiver) {
        this.receiver = receiver;
    }

    @Override
    public void registerListener(String eventId, EventListener<AuthenticatedEventData<T>> listener) {
        receiver.registerListener(eventId, eventData -> {
            try {
                AuthenticatedEventData<?> castedEventData = (AuthenticatedEventData<?>) eventData;

                listener.handle(new AuthenticatedEventData<>(castedEventData.getUsername(),
                    castedEventData.getPassword(), (T) castedEventData.getWrappedData()));
            } catch (ClassCastException e) { }
        });
    }
}
