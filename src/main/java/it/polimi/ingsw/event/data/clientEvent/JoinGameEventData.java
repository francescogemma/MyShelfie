package it.polimi.ingsw.event.data.clientEvent;

import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.Responder;
import it.polimi.ingsw.event.data.EventData;
import it.polimi.ingsw.event.data.gameEvent.BoardChangedEventData;
import it.polimi.ingsw.event.receiver.CastEventReceiver;
import it.polimi.ingsw.event.receiver.EventReceiver;
import it.polimi.ingsw.event.transmitter.EventTransmitter;
import it.polimi.ingsw.model.board.BoardView;

import java.util.function.Function;

public class JoinGameEventData implements EventData {
    public static final String ID = "JOIN_GAME";

    private final String gameName;

    public JoinGameEventData(String gameName) {
        this.gameName = gameName;
    }

    public String getGameName() {
        return this.gameName;
    }

    public static <T extends JoinGameEventData> CastEventReceiver<T> castEventReceiver(EventReceiver<EventData> receiver) {
        return new CastEventReceiver<>(ID, receiver);
    }

    public static <T extends EventData> Requester<JoinGameEventData, T> requester(EventTransmitter transmitter,
                                                                                      EventReceiver<EventData> receiver) {
        return new Requester<>(ID, transmitter, receiver);
    }

    public static <T extends EventData> Responder<JoinGameEventData, T> responder(EventTransmitter transmitter,
                                                                                      EventReceiver<EventData> receiver,
                                                                                      Function<JoinGameEventData, T> response) {
        return new Responder<>(ID, transmitter, receiver, response);
    }

    @Override
    public String getId() {
        return ID;
    }
}
