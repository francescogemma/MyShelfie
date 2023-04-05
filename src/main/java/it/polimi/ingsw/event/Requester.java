package it.polimi.ingsw.event;

import it.polimi.ingsw.event.data.EventData;
import it.polimi.ingsw.event.data.wrapper.SyncEventDataWrapper;
import it.polimi.ingsw.event.receiver.CastEventReceiver;
import it.polimi.ingsw.event.receiver.EventReceiver;
import it.polimi.ingsw.event.transmitter.EventTransmitter;

import java.util.HashMap;
import java.util.Map;

public class Requester<ReceivedDataType extends EventData, SentDataType extends EventData> {
    private final EventTransmitter transmitter;

    private static final Object nextRequestCountLock = new Object();
    private static int nextRequestCount = 0;

    private final Object responsesLock = new Object();
    private final Map<Integer, ReceivedDataType> responses = new HashMap<>();

    public Requester(String responseEventId, EventTransmitter transmitter, EventReceiver<EventData> receiver) {
        this.transmitter = transmitter;

        new CastEventReceiver<SyncEventDataWrapper<ReceivedDataType>>(SyncEventDataWrapper.WRAPPER_ID + "_" + responseEventId,
            receiver).registerListener(data -> {
                synchronized (responsesLock) {
                    responses.put(data.getCount(), data.getWrappedData());

                    responsesLock.notifyAll();
                }
        });
    }

    public ReceivedDataType request(SentDataType data) {
        int count;

        synchronized (nextRequestCountLock) {
            count = nextRequestCount;
            nextRequestCount++;
        }

        transmitter.broadcast(new SyncEventDataWrapper<SentDataType>(count, data));

        synchronized (responsesLock) {
            while (responses.get(count) == null) {
                try {
                    responsesLock.wait();
                } catch (InterruptedException e) { }
            }

            return responses.remove(count);
        }
    }
}
