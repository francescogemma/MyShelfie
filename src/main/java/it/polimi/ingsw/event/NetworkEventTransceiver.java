package it.polimi.ingsw.event;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import it.polimi.ingsw.controller.db.CommonGoalTypeAdapter;
import it.polimi.ingsw.controller.db.PersonalGoalTypeAdapter;
import it.polimi.ingsw.event.data.EventData;
import it.polimi.ingsw.event.data.internal.PlayerDisconnectedInternalEventData;
import it.polimi.ingsw.event.receiver.EventListener;
import it.polimi.ingsw.model.goal.CommonGoal;
import it.polimi.ingsw.model.goal.PersonalGoal;
import it.polimi.ingsw.networking.Connection;
import it.polimi.ingsw.networking.DisconnectedException;
import it.polimi.ingsw.utils.Logger;
import jdk.jfr.Event;

import java.util.ArrayList;
import java.util.List;

public class NetworkEventTransceiver implements EventTransceiver {
    private final Object lock;

    private final List<EventListener<EventData>> listeners = new ArrayList<>();
    private final Connection connection;
    private final Gson gson;

    public NetworkEventTransceiver(Connection connection, Object lock) {
        this.lock = lock;

        this.connection = connection;
        this.gson = new GsonBuilder()
            .registerTypeAdapterFactory(new EventDataTypeAdapterFactory())
            .registerTypeAdapter(CommonGoal.class, new CommonGoalTypeAdapter())
            .registerTypeAdapter(PersonalGoal.class, new PersonalGoalTypeAdapter())
            .create();

        new Thread(() -> {
            while (true) {
                String eventJSON;
                try {
                    eventJSON = connection.receive();
                } catch (DisconnectedException e) {
                    this.notifyDisconnection();
                    return;
                }

                EventData eventData;
                try {
                    eventData = gson.fromJson(eventJSON, EventData.class);
                } catch (JsonParseException e) {
                    // We skip non-valid event JSONs
                    Logger.writeCritical("Got exception while deserializing");
                    Logger.writeCritical(e.getMessage());
                    continue;
                }

                synchronized (this.lock) {
                    List<EventListener<EventData>> listenersCopy = new ArrayList<>(listeners);
                    for (EventListener<EventData> listener : listenersCopy) {
                        listener.handle(eventData);
                    }
                }
            }
        }).start();
    }

    private void notifyDisconnection () {
        synchronized (lock) {
            List<EventListener<EventData>> listenersCopy = new ArrayList<>(listeners);

            listenersCopy
                    .forEach(l ->
                            l.handle(new PlayerDisconnectedInternalEventData())
                    );
        }
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
            this.listeners.remove(listener);
        }
    }

    @Override
    public void unregisterAllListeners() {
        synchronized (lock) {
            this.listeners.clear();
        }
    }

    @Override
    public void broadcast(EventData data) {
        try {
            connection.send(gson.toJson(data, EventData.class));
        } catch (DisconnectedException ignored) {

        }
    }

    public void disconnect() {
        connection.disconnect();
    }
}
