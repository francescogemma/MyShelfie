package it.polimi.ingsw.event.data.game;

import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.Responder;
import it.polimi.ingsw.event.data.EventData;
import it.polimi.ingsw.event.receiver.CastEventReceiver;
import it.polimi.ingsw.event.receiver.EventReceiver;
import it.polimi.ingsw.event.transmitter.EventTransmitter;

import java.util.function.Function;

public class GameHasBeenStoppedEventData implements EventData {
    public static final String ID = "GAME_HAS_BEEN_STOPPED";

    public GameHasBeenStoppedEventData() {

    }

    public static CastEventReceiver<GameHasBeenStoppedEventData> castEventReceiver(EventReceiver<EventData> receiver) {
        return new CastEventReceiver<>(ID, receiver);
    }

    public static <T extends EventData> Requester<GameHasBeenStoppedEventData, T> requester(EventTransmitter transmitter,
                                                                                            EventReceiver<EventData> receiver,
                                                                                            Object responsesLock) {
        return new Requester<>(ID, transmitter, receiver, responsesLock);
    }

    public static <T extends EventData> Responder<GameHasBeenStoppedEventData, T> responder(EventTransmitter transmitter,
                                                                                            EventReceiver<EventData> receiver,
                                                                                            Function<GameHasBeenStoppedEventData, T> response) {
        return new Responder<>(ID, transmitter, receiver, response);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this)
            return true;
        if (other == null || other.getClass() != this.getClass())
            return false;

        return true;
    }

    @Override
    public String getId() {
        return ID;
    }
}
