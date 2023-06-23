package it.polimi.ingsw.event.data.game;

import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.Responder;
import it.polimi.ingsw.event.data.EventData;
import it.polimi.ingsw.event.receiver.CastEventReceiver;
import it.polimi.ingsw.event.receiver.EventReceiver;
import it.polimi.ingsw.event.transmitter.EventTransmitter;
import it.polimi.ingsw.utils.Logger;
import it.polimi.ingsw.utils.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

/**
 * Event sent when the game has started.
 * @author Giacomo Groppi
 */
public class GameHasStartedEventData implements EventData {
    public static final String ID = "GAME_HAS_STARTED";

    /**
     * Default constructor.
     */
    public GameHasStartedEventData() {
    }

    public static CastEventReceiver<GameHasStartedEventData> castEventReceiver(EventReceiver<EventData> receiver) {
        return new CastEventReceiver<>(ID, receiver);
    }

    public static <T extends EventData> Requester<GameHasStartedEventData, T> requester(EventTransmitter transmitter,
                                                                                              EventReceiver<EventData> receiver,
                                                                                              Object responsesLock) {
        return new Requester<>(ID, transmitter, receiver, responsesLock);
    }

    public static <T extends EventData> Responder<GameHasStartedEventData, T> responder(EventTransmitter transmitter,
                                                                                              EventReceiver<EventData> receiver,
                                                                                              Function<GameHasStartedEventData, T> response) {
        return new Responder<>(ID, transmitter, receiver, response);
    }

    @Override
    public String getId() {
        return ID;
    }
}
