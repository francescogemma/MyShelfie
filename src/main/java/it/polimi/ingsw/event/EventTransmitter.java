package it.polimi.ingsw.event;

public interface EventTransmitter<T> {
    void broadcast(Event<T> event);
}
