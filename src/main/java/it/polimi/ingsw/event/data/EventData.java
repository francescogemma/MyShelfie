package it.polimi.ingsw.event.data;

/**
 * Represents the data associated with any kind of event which can happen in the system.
 * Events can be dispatched to local components (components living on the same machine, as,
 * for example, different objects in the client program) or remote components (object living
 * on different machines connected through the network).
 * Dispatching events to remote components involves serialization.
 * Remote machines will receive every kind of serialized event on the same interface (the same network port).
 * We need a way which allows the deserializer to infer the concrete type from the received serialized EventData.
 * For this reason every EventData must provide a unique identifier which gets attached to the serialized data.
 * The identifier is given by {@link EventData#getId()}.
 * EventData are serialized in the JSON format.
 *
 * @see it.polimi.ingsw.event.EventDataTypeAdapterFactory Events serialization
 */
public interface EventData {
    /**
     * @return the unique identifier to allow events deserialization.
     */
    String getId();
}
