package it.polimi.ingsw.event;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.polimi.ingsw.controller.db.CommonGoalTypeAdapter;
import it.polimi.ingsw.controller.db.PersonalGoalTypeAdapter;
import it.polimi.ingsw.event.data.EventData;
import it.polimi.ingsw.event.receiver.EventListener;
import it.polimi.ingsw.model.goal.CommonGoal;
import it.polimi.ingsw.model.goal.PersonalGoal;

import java.util.ArrayList;
import java.util.List;

/**
 * Is an {@link EventTransceiver} which simulates the dispatch of {@link EventData} through the network. In reality
 * events are broadcast on the same machine between different threads. It is used for testing purposes.
 *
 * @author Cristiano Migali
 */
public class MockNetworkEventTransceiver implements EventTransceiver {
    /**
     * Lock object which allows synchronization of listeners registration and removal.
     */
    private final Object lock = new Object();

    /**
     * List of {@link EventData} JSON which are being dispatched. They will be received by another thread which
     * will consume the list and notify the listeners.
     */
    private final List<String> serializedEvents = new ArrayList<>();

    /**
     * List of listeners registered on the transceiver.
     */
    private final List<EventListener<EventData>> listeners = new ArrayList<>();

    /**
     * {@link Gson} object used for serialization and deserialization of {@link EventData}.
     */
    private final Gson gson;

    /**
     * disconnect is true iff the transceiver won't broadcast any event, anymore.
     */
    private boolean disconnect = false;

    /**
     * Constructor of the class. It initializes the {@link Gson} object used in serialization and starts the
     * "receiver" thread which will notify the listeners of the broadcast events.
     */
    public MockNetworkEventTransceiver() {
        gson = new GsonBuilder()
                .registerTypeAdapterFactory(new EventDataTypeAdapterFactory())
                .registerTypeAdapter(CommonGoal.class, new CommonGoalTypeAdapter())
                .registerTypeAdapter(PersonalGoal.class, new PersonalGoalTypeAdapter())
                .create();

        new Thread(() -> {
            while (true) {
                List<String> serializedEventsCopy;
                synchronized (serializedEvents) {
                    while (serializedEvents.isEmpty()) {
                        try {
                            serializedEvents.wait();
                        } catch (InterruptedException e) {
                        }

                        if (disconnect) {
                            return;
                        }
                    }

                    serializedEventsCopy = new ArrayList<>(serializedEvents);

                    serializedEvents.clear();
                }

                synchronized (lock) {
                    for (String eventJSON : serializedEventsCopy) {
                        EventData data = gson.fromJson(eventJSON, EventData.class);

                        List<EventListener<EventData>> listenersCopy = new ArrayList<>(listeners);
                        for (EventListener<EventData> listener : listenersCopy) {
                            listener.handle(data);
                        }
                    }
                }
            }
        }).start();
    }

    @Override
    public void registerListener(EventListener<EventData> listener) {
        synchronized (lock) {
            listeners.add(listener);
        }
    }

    @Override
    public void unregisterListener(EventListener<EventData> listener) {
        synchronized (lock) {
            listeners.remove(listener);
        }
    }

    @Override
    public void unregisterAllListeners() {
        synchronized (lock) {
            listeners.clear();
        }
    }

    @Override
    public void broadcast(EventData data) {
        synchronized (serializedEvents) {
            serializedEvents.add(gson.toJson(data, EventData.class));

            serializedEvents.notifyAll();
        }
    }

    /**
     * Disconnects the transceiver. That is, it stops the receiver thread associated with the transceiver.
     * The transceiver won't be able to broadcast events anymore.
     */
    public void disconnect() {
        synchronized (serializedEvents) {
            disconnect = true;
            serializedEvents.notifyAll();
        }
    }
}
