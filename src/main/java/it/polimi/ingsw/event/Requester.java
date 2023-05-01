package it.polimi.ingsw.event;

import it.polimi.ingsw.event.data.EventData;
import it.polimi.ingsw.event.data.wrapper.SyncEventDataWrapper;
import it.polimi.ingsw.event.receiver.CastEventReceiver;
import it.polimi.ingsw.event.receiver.EventReceiver;
import it.polimi.ingsw.event.transmitter.EventTransmitter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Requester<R extends EventData, S extends EventData> {
    private final EventTransmitter transmitter;

    private static final Object nextRequestCountLock = new Object();
    private static int nextRequestCount = 0;

    private final Object responsesLock;
    private final Set<Integer> waitingFor = new HashSet<>();
    private final Map<Integer, R> responses = new HashMap<>();

    public Requester(String responseEventId, EventTransmitter transmitter, EventReceiver<EventData> receiver, Object responsesLock) {
        this.transmitter = transmitter;
        this.responsesLock = responsesLock;

        new CastEventReceiver<SyncEventDataWrapper<R>>(SyncEventDataWrapper.WRAPPER_ID + "_" + responseEventId,
            receiver).registerListener(data -> {
                synchronized (this.responsesLock) {
                    if (waitingFor.contains(data.getCount())) {
                        waitingFor.remove(data.getCount());
                        responses.put(data.getCount(), data.getWrappedData());

                        this.responsesLock.notifyAll();
                    }
                }
        });
    }

    public R request(S data) {
        int count;

        synchronized (nextRequestCountLock) {
            count = nextRequestCount;
            nextRequestCount++;
        }

        synchronized (responsesLock) {
            waitingFor.add(count);
        }

        transmitter.broadcast(new SyncEventDataWrapper<S>(count, data));

        // TODO: Handle disconnections

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
