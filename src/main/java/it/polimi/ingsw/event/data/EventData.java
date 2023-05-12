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
 * By convention the EventData subclass referred to "some event name" should be called SomeEventNameEventData and
 * its identifier should be SOME_EVENT_NAME.
 * EventData are serialized in the JSON format.
 * By convention every EventData subclass must provide three static methods: {@code castEventReceiver}, {@code requester},
 * {@code responder}. These methods respectively construct a {@link it.polimi.ingsw.event.receiver.CastEventReceiver}
 * which filters for events of the type of the EventData subclass,
 * a {@link it.polimi.ingsw.event.Requester} which receives responses of the type of the EventData subclass and
 * a {@link it.polimi.ingsw.event.Responder} which receives requests of the type of the EventData subclass.
 * This is due to the fact that a {@link it.polimi.ingsw.event.receiver.CastEventReceiver} needs to know the identifier
 * of the EventData subclass it is filtering for, indeed, because of Java Type erasure, you can't access to type
 * parameters at run time.
 * {@link it.polimi.ingsw.event.Requester} and {@link it.polimi.ingsw.event.Responder} need an internal
 * {@link it.polimi.ingsw.event.receiver.CastEventReceiver}, then they transitively need the static factory.
 * It is crucial that the given identifier matches with the type parameter of the cast receiver, otherwise it could
 * lead to bugs difficult to find. For this reason the only way of constructing these objects is through the
 * provided factories which ensure this match.
 *
 * @see it.polimi.ingsw.event.EventDataTypeAdapterFactory Events serialization
 * @see it.polimi.ingsw.event.receiver.CastEventReceiver
 *
 * @author Cristiano Migali
 */
public interface EventData {
    /**
     * @return the unique identifier to allow events deserialization.
     */
    String getId();
}
