package it.polimi.ingsw.event.data.gameEvent;

import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.Responder;
import it.polimi.ingsw.event.data.EventData;
import it.polimi.ingsw.event.receiver.CastEventReceiver;
import it.polimi.ingsw.event.receiver.EventReceiver;
import it.polimi.ingsw.event.transmitter.EventTransmitter;

import java.util.function.Function;

public class GameIsNoLongerAvailableEventData implements EventData {
    public static final String ID = "GAME_IS_NO_LONGER_AVAILABLE";

    private final String gameName;

    public GameIsNoLongerAvailableEventData(String gameName) {
        this.gameName = gameName;
    }

    public static <T extends GameIsNoLongerAvailableEventData> CastEventReceiver<T> castEventReceiver(EventReceiver<EventData> receiver) {
        return new CastEventReceiver<>(ID, receiver);
    }

    public static <T extends EventData> Requester<GameIsNoLongerAvailableEventData, T> requester(EventTransmitter transmitter,
                                                                                        EventReceiver<EventData> receiver) {
        return new Requester<>(ID, transmitter, receiver);
    }

    public static <T extends EventData> Responder<GameIsNoLongerAvailableEventData, T> responder(EventTransmitter transmitter,
                                                                                        EventReceiver<EventData> receiver,
                                                                                        Function<GameIsNoLongerAvailableEventData, T> response) {
        return new Responder<>(ID, transmitter, receiver, response);
    }

    public String getGameName () {
        return this.gameName;
    }

    @Override
    public String getId() {
        return ID;
    }
}
