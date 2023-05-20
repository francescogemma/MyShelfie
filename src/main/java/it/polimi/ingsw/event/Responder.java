package it.polimi.ingsw.event;

import it.polimi.ingsw.event.data.EventData;
import it.polimi.ingsw.event.data.wrapper.SyncEventDataWrapper;
import it.polimi.ingsw.event.receiver.CastEventReceiver;
import it.polimi.ingsw.event.receiver.EventReceiver;
import it.polimi.ingsw.event.transmitter.EventTransmitter;
import it.polimi.ingsw.utils.Logger;

import java.util.function.Function;

/**
 * Answers to synchronous request performed with a {@link Requester}. Specifically it listens for
 * requests on the provided receiver: when it receives a {@link SyncEventDataWrapper} which wraps an event
 * which matches the specified request type, it processes the response through the given lambda function, passing to it
 * the wrapped request event data, then it sends back on the provided transmitter a {@link SyncEventDataWrapper} which
 * wraps the response data and has attached to it the same integer that was in the request one.
 *
 * @param <R> is the type of the request we are answering for.
 * @param <S> is the type of the responses we are sending back.
 *
 * @author Cristiano Migali
 */
public class Responder<R extends EventData, S extends EventData> {

    /**
     * Constructor of the class. It registers the listener which handles requests answering.
     *
     * @param requestEventId is the event identifier of the requests we answer for.
     * @param transmitter is the transmitter on which we send back response.
     * @param receiver is the receiver on which we receive requests.
     * @param response is lambda function which process the response to the given request.
     */
    public Responder(String requestEventId, EventTransmitter transmitter, EventReceiver<EventData> receiver,
                     Function<R, S> response) {
        new CastEventReceiver<SyncEventDataWrapper<R>>(
            SyncEventDataWrapper.WRAPPER_ID + "_" + requestEventId,
            receiver).registerListener(data -> {
                transmitter.broadcast(new SyncEventDataWrapper<S>(data.getCount(),
                            response.apply(data.getWrappedData())
                        )
                );}
        );
    }
}
