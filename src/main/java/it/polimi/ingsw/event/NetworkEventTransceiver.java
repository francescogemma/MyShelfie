package it.polimi.ingsw.event;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import it.polimi.ingsw.controller.db.CommonGoalTypeAdapter;
import it.polimi.ingsw.controller.db.PersonalGoalTypeAdapter;
import it.polimi.ingsw.controller.db.TileTypeAdapter;
import it.polimi.ingsw.event.data.EventData;
import it.polimi.ingsw.event.data.internal.PlayerDisconnectedInternalEventData;
import it.polimi.ingsw.event.receiver.EventListener;
import it.polimi.ingsw.model.goal.CommonGoal;
import it.polimi.ingsw.model.goal.PersonalGoal;
import it.polimi.ingsw.model.tile.Tile;
import it.polimi.ingsw.networking.Connection;
import it.polimi.ingsw.networking.DisconnectedException;
import it.polimi.ingsw.utils.Logger;

import java.util.*;

/**
 * Is an {@link EventTransceiver} which allows broadcast and reception of events through a network. It relies on a
 * {@link Connection} (whose concrete implementations are {@link it.polimi.ingsw.networking.TCP.TCPConnection} and
 * {@link it.polimi.ingsw.networking.RMI.RMIConnection}, respectively the former allows communication over TCP protocol,
 * the latter over RMI protocol). Every NetworkEventTransceiver on an host is coupled with another
 * NetworkEventTransceiver (usually on a different host), these two have both a {@link Connection} object, every
 * {@link Connection} represents one of the two ends of the communication channel between the hosts.
 * When we broadcast events from one NetworkEventTransceiver, the other will receive them and notify all its
 * registered listeners.
 *
 * @author Cristiano Migali
 */
public class NetworkEventTransceiver implements EventTransceiver {
    /**
     * Lock object used to synchronize listeners registration, removal and event handling.
     */
    private final Object lock;

    /**
     * Is the list of listeners registered on the transceiver.
     */
    private final List<EventListener<EventData>> listeners = new ArrayList<>();

    /**
     * Is the underlying {@link Connection} object which allows to communicate with the paired NetworkEventTransceiver
     * on a different host.
     */
    private final Connection connection;

    /**
     * {@link Gson} object which allows for serialization of {@link EventData}s before sending them through the
     * {@link Connection}.
     */
    private final Gson gson;

    /**
     * Constructor of the class. Initializes the inner lock, the connection object, the {@link Gson} object
     * used for serialization and starts the "receiver Thread" which receives events from the coupled
     * NetworkEventTransceiver and notifies all the listeners.
     *
     * @param connection is the connection on which broadcast events will be sent and from which events
     *                   dispatched by the coupled NetworkEventTransceiver will be received.
     * @param lock is the inner lock used for listeners registration, removal and events handling.
     */
    public NetworkEventTransceiver(Connection connection, Object lock) {
        this.lock = lock;

        this.connection = connection;
        this.gson = new GsonBuilder()
            .registerTypeAdapterFactory(new EventDataTypeAdapterFactory())
            .registerTypeAdapter(CommonGoal.class, new CommonGoalTypeAdapter())
            .registerTypeAdapter(PersonalGoal.class, new PersonalGoalTypeAdapter())
            .registerTypeAdapter(Tile.class, new TileTypeAdapter())
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
                    Logger.writeCritical("Got exception while deserializing: " + e.getMessage());
                    continue;
                }

                synchronized (this.lock) {
                    List<EventListener<EventData>> listenersCopy = new ArrayList<>(listeners);
                    /* Event handling is done inside the lock because of an optimization:
                     * every TUI's component listener needs to synchronize on the drawing lock,
                     * we take the lock one time and then handle the events.
                     */
                    for (EventListener<EventData> listener : listenersCopy) {
                        listener.handle(eventData);
                    }
                }
            }
        }).start();

        new Thread(() -> {
            EventData toSend;

            while (true) {
                synchronized (sendQueue) {
                    while (sendQueue.isEmpty()) {
                        if (!hasToSend) {
                            return;
                        }

                        try {
                            sendQueue.wait();
                        } catch (InterruptedException e) {

                        }
                    }

                    toSend = sendQueue.poll();
                }

                try {
                    connection.send(gson.toJson(toSend, EventData.class));
                } catch (DisconnectedException e) {
                    return;
                }
            }
        }).start();
    }

    /**
     * Broadcasts an internal disconnection {@link EventData} to every registered listener.
     *
     * @author Giacomo Groppi
     */
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

    private final Queue<EventData> sendQueue = new ArrayDeque<>();
    private boolean hasToSend = true;

    @Override
    public void broadcast(EventData data) {
        synchronized (sendQueue) {
            sendQueue.add(data);
            sendQueue.notifyAll();
        }
    }

    /**
     * Disconnects the underlying connection.
     * This causes the "receiver Thread" to stop.
     * After having performed a disconnect the NetworkEventTransceiver is
     * useless: it can't broadcast nor receive events.
     */
    public void disconnect() {
        connection.disconnect();

        synchronized (sendQueue) {
            hasToSend = false;
            sendQueue.notifyAll();
        }
    }
}
