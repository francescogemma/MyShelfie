package it.polimi.ingsw.event;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.polimi.ingsw.event.data.EventData;
import it.polimi.ingsw.event.receiver.EventListener;

import java.util.ArrayList;
import java.util.List;

public class MockNetworkEventTransceiver implements EventTransceiver {
    private final Object lock = new Object();
    private final List<String> serializedEvents = new ArrayList<>();
    private final List<EventListener<EventData>> listeners = new ArrayList<>();

    private final Gson gson;


    private boolean disconnect = false;

    public MockNetworkEventTransceiver() {
        gson = new GsonBuilder().registerTypeAdapterFactory(new EventDataTypeAdapterFactory()).create();

        new Thread(() -> {
            synchronized (lock) {
                while (true) {
                    while (serializedEvents.isEmpty()) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) { }

                        if (disconnect) {
                            return;
                        }
                    }

                    List<String> serializedEventsCopy = new ArrayList<>(serializedEvents);
                    serializedEvents.clear();

                    for (String eventJSON : serializedEventsCopy) {
                        EventData data = gson.fromJson(eventJSON, EventData.class);

                        for (EventListener<EventData> listener : listeners) {
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
    public void broadcast(EventData data) {
        synchronized (lock) {
            serializedEvents.add(gson.toJson(data, EventData.class));

            lock.notifyAll();
        }
    }

    public void disconnect() {
        synchronized (lock) {
            disconnect = true;
            lock.notifyAll();
        }
    }
}
