package it.polimi.ingsw.event.receiver;

import it.polimi.ingsw.event.data.EventData;

/**
 * Represents an object which can receive a subclass of {@link EventData}.
 * That is, when an {@link EventData} with the specified type is broadcast by the transmitter coupled
 * with this receiver, the receiver will notify all the registered listeners through
 * {@link EventListener#handle(EventData)}.
 *
 * @param <T> is the subclass of {@link EventData} that this receiver receives.
 *
 * @author Cristiano Migali
 * */
public interface EventReceiver<T extends EventData> {
    /**
     * Registers a listener on this receiver.
     *
     * @param listener is the listener which will be registered.
     */
    void registerListener (EventListener<T> listener);

    /**
     * Unregisters a previously registered listener from this receiver.
     *
     * @param listener is the listener which will be unregistered. Listeners are compared by address,
     * hence you must keep a reference to the listener that you have registered to be able to unregister it next.
     */
    void unregisterListener(EventListener<T> listener);

    /**
     * Removes all the listeners registered on this receiver.
     * @author Giacomo Groppi
     * */
    void unregisterAllListeners();
}
