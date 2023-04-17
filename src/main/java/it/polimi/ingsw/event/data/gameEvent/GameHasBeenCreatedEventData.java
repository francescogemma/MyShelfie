package it.polimi.ingsw.event.data.gameEvent;

import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.Responder;
import it.polimi.ingsw.event.data.EventData;
import it.polimi.ingsw.event.receiver.CastEventReceiver;
import it.polimi.ingsw.event.receiver.EventReceiver;
import it.polimi.ingsw.event.transmitter.EventTransmitter;
import it.polimi.ingsw.model.game.PlayerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class GameHasBeenCreatedEventData implements EventData {
    private final List<String> games;
    public static final String ID = "GAME_HAS_BEEN_CREATED";

    public GameHasBeenCreatedEventData(List<String> games) {
        this.games = new ArrayList<>(games);
    }

    public static <T extends GameHasBeenCreatedEventData> CastEventReceiver<T> castEventReceiver(EventReceiver<EventData> receiver) {
        return new CastEventReceiver<>(ID, receiver);
    }

    public static <T extends EventData> Requester<GameHasBeenCreatedEventData, T> requester(EventTransmitter transmitter,
                                                                                            EventReceiver<EventData> receiver) {
        return new Requester<>(ID, transmitter, receiver);
    }

    public static <T extends EventData> Responder<GameHasBeenCreatedEventData, T> responder(EventTransmitter transmitter,
                                                                                            EventReceiver<EventData> receiver,
                                                                                            Function<GameHasBeenCreatedEventData, T> response) {
        return new Responder<>(ID, transmitter, receiver, response);
    }

    public List<String> getWinners () {
        return new ArrayList<>(this.games);
    }

    @Override
    public String getId() {
        return ID;
    }
}
