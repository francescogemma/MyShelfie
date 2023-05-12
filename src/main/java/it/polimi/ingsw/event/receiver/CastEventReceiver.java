package it.polimi.ingsw.event.receiver;

import it.polimi.ingsw.event.data.EventData;

import java.util.HashMap;
import java.util.Map;

/**
 * Is a special kind of {@link EventReceiver} which listens for all the events on a provided receiver and filters
 * only those of a certain type. Then it notifies these events to all its listeners with the advantage that the
 * EventData objects are already cast.
 * In order to perform the filtering, the CastEventReceiver requires to know the identifier of the EventData
 * it will filter for; this is due to type erasure which denies access to type parameters at runtime.
 * It is crucial that the type parameter of the CastEventReceiver matches the given id.
 *
 * @param <T> is the type of the EventData we are filtering for.
 */
public class CastEventReceiver<T extends EventData> implements EventReceiver<T> {
    /**
     * The given "source" receiver from which we will filter events of the specified type.
     */
    private final EventReceiver<EventData> receiver;

    /**
     * The EventData identifier we are filtering for.
     */
    private final String eventId;

    /**
     * Lock object used for synchronization in listeners registration and removal.
     */
    private final Object lock = new Object();

    /**
     * Maps a listener registered on this receiver with the listener actually registered on the "source"
     * receiver. It is used to retrieve the reference to the listener registered on the "source" receiver when
     * performing listener removal in {@link CastEventReceiver#unregisterListener(EventListener)}.
     *
     * @see CastEventReceiver#registerListener(EventListener)
     */
    private final Map<EventListener<T>, EventListener<EventData>> listeners = new HashMap<>();

    /**
     * Constructor of the class. It sets the event identifier we are filtering for and the "source" receiver.
     *
     * @param eventId is the event identifier we are filtering for.
     * @param receiver is the "source" receiver from which we will filter events of the specified type.
     */
    public CastEventReceiver(String eventId, EventReceiver<EventData> receiver) {
        this.eventId = eventId;
        this.receiver = receiver;
    }

    @Override
    public void registerListener(EventListener<T> listener) {
        synchronized (lock) {
            listeners.put(listener, data -> {
                if (data.getId().equals(eventId)) {
                    try {
                        listener.handle((T) data);
                    } catch (ClassCastException e) {
                        throw new IllegalStateException("Event ID should always match event data concrete type");
                    }
                }
            });

            receiver.registerListener(listeners.get(listener));
        }
    }

    @Override
    public void unregisterListener(EventListener<T> listener) {
        synchronized (lock) {
            receiver.unregisterListener(listeners.remove(listener));
        }
    }

    @Override
    public void unregisterAllListeners() {
        synchronized (lock) {
            receiver.unregisterAllListeners();
        }
    }
}
