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

public class MockNetworkEventTransceiver implements EventTransceiver {
    private final Object lock = new Object();
    private final List<String> serializedEvents = new ArrayList<>();
    private final List<EventListener<EventData>> listeners = new ArrayList<>();

    private final Gson gson;

    private boolean disconnect = false;

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

    public void disconnect() {
        synchronized (serializedEvents) {
            disconnect = true;
            serializedEvents.notifyAll();
        }
    }
}
