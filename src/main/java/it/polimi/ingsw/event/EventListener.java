package it.polimi.ingsw.event;

public interface EventListener<T> {
    void handle(T data);
}
