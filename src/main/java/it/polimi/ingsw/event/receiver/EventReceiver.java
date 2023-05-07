package it.polimi.ingsw.event.receiver;

import it.polimi.ingsw.event.data.EventData;

/**
 * @author Cristiano Migali
 * */
public interface EventReceiver<T extends EventData> {
    void registerListener (EventListener<T> listener);
    void unregisterListener(EventListener<T> listener);

    /**
     * Removes all the listeners registered to this receiver.
     * @author Giacomo Groppi
     * */
    void unregisterAllListeners();
}
