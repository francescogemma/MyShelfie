package it.polimi.ingsw.event.receiver;

import it.polimi.ingsw.event.data.EventData;

public interface EventListener<T extends EventData> {
    void handle(T data);
}
