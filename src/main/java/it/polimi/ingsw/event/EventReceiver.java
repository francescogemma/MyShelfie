package it.polimi.ingsw.event;

public interface EventReceiver<T> {
    void registerListener(String eventId, EventListener<T> listener);
}
