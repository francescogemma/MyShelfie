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

    public static CastEventReceiver<CurrentPlayerChangedEventData> castEventReceiver(EventReceiver<EventData> receiver) {
        return new CastEventReceiver<>(ID, receiver);
    }

    public static <T extends EventData> Requester<CurrentPlayerChangedEventData, T> requester(EventTransmitter transmitter,
                                                                                  EventReceiver<EventData> receiver,
                                                                                              Object responsesLock) {
        return new Requester<>(ID, transmitter, receiver, responsesLock);
    }

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
