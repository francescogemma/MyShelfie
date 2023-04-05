package it.polimi.ingsw.event;

import it.polimi.ingsw.event.data.EventData;
import it.polimi.ingsw.event.data.wrapper.SyncEventDataWrapper;
import it.polimi.ingsw.event.receiver.CastEventReceiver;
import it.polimi.ingsw.event.receiver.EventReceiver;
import it.polimi.ingsw.event.transmitter.EventTransmitter;

import java.util.function.Function;

public class Responder<ReceivedDataType extends EventData, SentDataType extends EventData> {
    public Responder(String requestEventId, EventTransmitter transmitter, EventReceiver<EventData> receiver,
                     Function<ReceivedDataType, SentDataType> response) {
        new CastEventReceiver<SyncEventDataWrapper<ReceivedDataType>>(
            SyncEventDataWrapper.WRAPPER_ID + "_" + requestEventId,
            receiver).registerListener(data ->
                transmitter.broadcast(new SyncEventDataWrapper<SentDataType>(data.getCount(),
                    response.apply(data.getWrappedData()))));
    }
}
