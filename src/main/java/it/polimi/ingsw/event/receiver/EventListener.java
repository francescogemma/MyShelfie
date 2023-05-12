package it.polimi.ingsw.event.receiver;

import it.polimi.ingsw.event.data.EventData;

/**
 * Represents the callback that gets invoked by an {@link EventReceiver} when it receives an event of
 * the specified type.
 *
 * @param <T> is the type of the events that the listener handles.
 *
 * @author Cristiano Migali
 */
public interface EventListener<T extends EventData> {
    /**
     * Callback invoked by an {@link EventReceiver} when it receives an event of the specified type.
     *
     * @param data is the EventData subclass associated with the received event.
     */
    void handle(T data);
}
