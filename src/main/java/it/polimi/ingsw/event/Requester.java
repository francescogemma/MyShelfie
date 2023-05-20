package it.polimi.ingsw.event;

import it.polimi.ingsw.event.data.EventData;
import it.polimi.ingsw.event.data.internal.PlayerDisconnectedInternalEventData;
import it.polimi.ingsw.event.data.wrapper.SyncEventDataWrapper;
import it.polimi.ingsw.event.receiver.CastEventReceiver;
import it.polimi.ingsw.event.receiver.EventListener;
import it.polimi.ingsw.event.receiver.EventReceiver;
import it.polimi.ingsw.event.transmitter.EventTransmitter;
import it.polimi.ingsw.networking.DisconnectedException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Allows to perform synchronous requests through an {@link EventTransmitter} (used to send the requests)
 * and an {@link EventReceiver} (used to receive the response).
 * The events dispatching framework provided by {@link EventTransmitter} and {@link EventReceiver} is intrinsically
 * asynchronous. A Requester attaches an integer (unique for every request) to the request {@link EventData} before
 * sending it (using a {@link SyncEventDataWrapper}); then it waits for an {@link EventData} of the type of the
 * response it is waiting for to which the same integer of the request has been attached.
 * The attachment in the response is performed by a {@link Responder} which works in symbiosis with the Requester.
 * After having created a Requester, you MUST initialize it through {@link Requester#registerAllListeners()}.
 *
 * @param <R> is the type of the response we want to receive.
 * @param <S> is the type of the request we want to send.
 *
 * @author Cristiano Migali
 */
public class Requester<R extends EventData, S extends EventData> {
    /**
     * Is the transmitter used to send requests.
     */
    private final EventTransmitter transmitter;

    /**
     * Is the receiver on which responses are received.
     */
    private final EventReceiver<SyncEventDataWrapper<R>> responsesReceiver;

    /**
     * Is the listener registered on {@link Requester#responsesReceiver} which listens for every event dispatched
     * on the receiver and notifies the thread running the {@link Requester#request(EventData)} method when it
     * receives the desired response.
     */
    private final EventListener<SyncEventDataWrapper<R>> responsesListener;

    /**
     * Is the receiver which receives an internal disconnection event if the connection in the underlying receiver has
     * been disconnected. In fact Requester and Responder are meant to be used over the network, hence a disconnection
     * during a request can happen and must be taken into account.
     */
    private final EventReceiver<PlayerDisconnectedInternalEventData> disconnectedReceiver;

    /**
     * Is the listener which handles the disconnection of the (eventual) connection in the response receiver.
     */
    private final EventListener<PlayerDisconnectedInternalEventData> disconnectedListener;

    /**
     * Lock object used to generate the unique integers that will be attached to the requests.
     */
    private static final Object nextRequestCountLock = new Object();

    /**
     * Incremental count used to generate unique integers that will be attached to the requests, before performing
     * a request we get the value of this attribute and increment it (atomically thanks to {@link Requester#nextRequestCountLock}).
     */
    private static int nextRequestCount = 0;

    /**
     * Lock object used to synchronize the thread which waits for the response with the listener which receives it.
     * Furthermore this thread is used to synchronize listeners registration and removal through
     * {@link Requester#registerAllListeners()} and {@link Requester#unregisterAllListeners()}.
     */
    private final Object responsesLock;

    /**
     * Set used to store the integers attached to requests for which we are waiting a response.
     */
    private final Set<Integer> waitingFor = new HashSet<>();

    /**
     * Map used to store responses. The map key is the integer attached to the request.
     */
    private final Map<Integer, R> responses = new HashMap<>();

    /**
     * Boolean attribute true iff the response receiver has been disconnected or all the listeners have been remove from
     * it.
     */
    private boolean disconnected = true;

    /**
     * Constructor of the class. It initializes the transmitter and the receiver used respectively for sending requests
     * and receiving responses. It constructs (BUT DOES NOT REGISTER) the listener which receives responses and
     * notifies the waiting thread (which is running {@link Requester#request(EventData)}) and the one which receives
     * the disconnection event. You MUST register these listeners through {@link Requester#registerAllListeners()}.
     *
     * @param responseEventId is the identifier of the {@link EventData} type of responses we are waiting for.
     * @param transmitter is the transmitter on which requests are sent.
     * @param receiver is the receiver on which responses (and disconnection events) are received.
     * @param responsesLock is the lock used to synchronize the waiting thread running {@link Requester#request(EventData)}
     *                      with the listener which receives the response.
     */
    public Requester(String responseEventId, EventTransmitter transmitter, EventReceiver<EventData> receiver, Object responsesLock) {
        this.transmitter = transmitter;

        responsesReceiver = new CastEventReceiver<SyncEventDataWrapper<R>>(SyncEventDataWrapper.WRAPPER_ID + "_" +
            responseEventId, receiver);

        this.responsesLock = responsesLock;

        responsesListener = data -> {
            synchronized (this.responsesLock) {
                if (waitingFor.contains(data.getCount())) {
                    waitingFor.remove(data.getCount());
                    responses.put(data.getCount(), data.getWrappedData());

                    this.responsesLock.notifyAll();
                }
            }
        };

        disconnectedListener = data -> {
            synchronized (this.responsesLock) {
                disconnected = true;

                this.responsesLock.notifyAll();
            }
        };

        disconnectedReceiver = PlayerDisconnectedInternalEventData.castEventReceiver(receiver);
    }

    /**
     * Performs a synchronous request and waits until it receives the corresponding responses or some disconnection
     * happen.
     *
     * @param data is the event data of the request that we want to send.
     * @return the data of corresponding received response.
     *
     * @throws DisconnectedException if the underlying connection inside the receiver gets disconnected in between
     * the synchronous request.
     */
    public R request(S data) throws DisconnectedException {
        int count;

        synchronized (nextRequestCountLock) {
            count = nextRequestCount;
            nextRequestCount++;
        }

        synchronized (responsesLock) {
            waitingFor.add(count);
        }

        transmitter.broadcast(new SyncEventDataWrapper<S>(count, data));

        synchronized (responsesLock) {
            while (responses.get(count) == null) {
                if (disconnected) {
                    throw new DisconnectedException();
                }

                try {
                    responsesLock.wait();
                } catch (InterruptedException e) { }
            }

            return responses.remove(count);
        }
    }

    /**
     * Registers the listener which receives responses and the one which handles disconnection events on the receiver.
     * You MUST invoke this method after construction to start using the requester.
     */
    public void registerAllListeners() {
        synchronized (responsesLock) {
            responsesReceiver.registerListener(responsesListener);
            disconnectedReceiver.registerListener(disconnectedListener);

            disconnected = false;
        }
    }

    /**
     * Unregisters the listener which receives responses and the one which handles disconnection events from the
     * receiver.
     */
    public void unregisterAllListeners() {
        synchronized (responsesLock) {
            responsesReceiver.unregisterListener(responsesListener);
            disconnectedReceiver.unregisterListener(disconnectedListener);

            disconnected = true;

            responsesLock.notifyAll();
        }
    }
}
