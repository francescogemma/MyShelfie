package it.polimi.ingsw.event.receiver;

import it.polimi.ingsw.event.data.EventData;

public interface EventReceiver<T extends EventData> {
    void registerListener (EventListener<T> listener);
    void unregisterListener(EventListener<T> listener);
}
