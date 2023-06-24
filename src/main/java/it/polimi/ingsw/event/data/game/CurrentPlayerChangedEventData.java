package it.polimi.ingsw.event.data.game;

import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.Responder;
import it.polimi.ingsw.event.data.EventData;
import it.polimi.ingsw.event.receiver.CastEventReceiver;
import it.polimi.ingsw.event.receiver.EventReceiver;
import it.polimi.ingsw.event.transmitter.EventTransmitter;
import it.polimi.ingsw.model.game.Player;

import java.util.function.Function;

/**
 * This event indicates that the current player has changed.
 * @author Giacomo Groppi
 * */
public class CurrentPlayerChangedEventData implements EventData {
    /**
     * The username of the new current player.
     * */
    private final String username;

    /**
     * Unique identifier for the EventData, added for serialization purposes accordingly to {@link EventData}
     * interface contract.
     */
    public static final String ID = "CURRENT_PLAYER_CHANGED";

    /**
     * Constructor of the event.
     * @param player The new current {@link Player}.
     * */
    public CurrentPlayerChangedEventData(Player player) {
        this.username = player.getUsername();
    }

    /**
     * Getter for the username of the new current player.
     * @return The username of the new current player.
     * */
    public String getUsername() {
        return this.username;
    }

    /**
     * {@link CastEventReceiver} factory method for the EventData, added accordingly to {@link EventData}
     * interface contract.
     *
     * @param receiver is the {@link EventReceiver} which will receive instances of this EventData.
     * @return a {@link CastEventReceiver} which filters the events received by receiver and provides those
     * of this EventData type to an {@link it.polimi.ingsw.event.receiver.EventListener} after a cast.
     */
    public static CastEventReceiver<CurrentPlayerChangedEventData> castEventReceiver(EventReceiver<EventData> receiver) {
        return new CastEventReceiver<>(ID, receiver);
    }

    /**
     * {@link Requester} factory method that performs requests which receive responses of this EventData, added
     * accordingly to {@link EventData} interface contract.
     *
     * @param transmitter is the {@link EventTransmitter} on which the request is sent.
     * @param receiver is the {@link EventReceiver} which receives the response to the request.
     * @param responsesLock is the lock Object on which the {@link Requester} synchronizes to wait for the
     *                      response.
     * @return a {@link Requester} which is capable of performing requests which receive responses of this
     * EventData.
     * @param <T> is the type of the request EventData.
     */
    public static <T extends EventData> Requester<CurrentPlayerChangedEventData, T> requester(EventTransmitter transmitter,
                                                                                  EventReceiver<EventData> receiver,
                                                                                              Object responsesLock) {
        return new Requester<>(ID, transmitter, receiver, responsesLock);
    }

    /**
     * {@link Responder} factory method which allows to respond to requests of this EventData, added accordingly
     * to {@link EventData} interface contract.
     *
     * @param transmitter is the {@link EventTransmitter} on which the response is sent.
     * @param receiver is the {@link EventReceiver} which receives the request.
     * @param response is the function which allows to compute the appropriate response to the received request.
     * @return a {@link Responder} which is capable of answering requests of this EventData.
     * @param <T> is the type of the response EventData.
     */
    public static <T extends EventData> Responder<CurrentPlayerChangedEventData, T> responder(EventTransmitter transmitter,
                                                                                  EventReceiver<EventData> receiver,
                                                                                  Function<CurrentPlayerChangedEventData, T> response) {
        return new Responder<>(ID, transmitter, receiver, response);
    }

    @Override
    public String getId() {
        return ID;
    }
}
