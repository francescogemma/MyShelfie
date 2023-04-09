package it.polimi.ingsw.event;

import it.polimi.ingsw.event.data.EventData;
import it.polimi.ingsw.event.data.wrapper.SyncEventDataWrapper;
import it.polimi.ingsw.event.receiver.CastEventReceiver;
import it.polimi.ingsw.event.receiver.EventReceiver;
import it.polimi.ingsw.event.transmitter.EventTransmitter;

import java.util.function.Function;

public class Responder<R extends EventData, S extends EventData> {
    public Responder(String requestEventId, EventTransmitter transmitter, EventReceiver<EventData> receiver,
                     Function<R, S> response) {
        new CastEventReceiver<SyncEventDataWrapper<R>>(
            SyncEventDataWrapper.WRAPPER_ID + "_" + requestEventId,
            receiver).registerListener(data ->
                transmitter.broadcast(new SyncEventDataWrapper<S>(data.getCount(),
                    response.apply(data.getWrappedData()))));
    }
}
