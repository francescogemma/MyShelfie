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

import java.util.ArrayList;
import java.util.List;

public class NetworkEventTransceiver implements EventTransceiver {
    private final Object lock = new Object();

    private final List<EventListener<EventData>> listeners = new ArrayList<>();
    private final Connection connection;
    private final Gson gson;

    public NetworkEventTransceiver(Connection connection) {
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
                    return;
                }

                EventData eventData;
                try {
                    eventData = gson.fromJson(eventJSON, EventData.class);
                } catch (JsonParseException e) {
                    // We skip non-valid event JSONs
                    continue;
                }

                synchronized (lock) {
                    for (EventListener<EventData> listener : listeners) {
                        listener.handle(eventData);
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
        try {
            connection.send(gson.toJson(data, EventData.class));
        } catch (DisconnectedException e) {
            synchronized (lock) {
                listeners
                        .forEach(l ->
                            l.handle(new PlayerDisconnectedInternalEventData())
                        );
            }
        }
    }

    public void disconnect() {
        connection.disconnect();
    }
}
