package it.polimi.ingsw.event.data.gameEvent;

import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.Responder;
import it.polimi.ingsw.event.data.EventData;
import it.polimi.ingsw.event.receiver.CastEventReceiver;
import it.polimi.ingsw.event.receiver.EventReceiver;
import it.polimi.ingsw.event.transmitter.EventTransmitter;
import it.polimi.ingsw.model.board.Board;

import java.util.function.Function;

public class BoardChangedEventData implements EventData {
    private final Board board;
    public static final String ID = "BOARD_CHANGE";

    public BoardChangedEventData(Board board) {
        if (board == null)
            throw new NullPointerException();
        this.board = board;
    }

    public Board getBoard () {
        return this.board;
    }

    public static <T extends BoardChangedEventData> CastEventReceiver<T> castEventReceiver(EventReceiver<EventData> receiver) {
        return new CastEventReceiver<>(ID, receiver);
    }

    public static <T extends EventData> Requester<BoardChangedEventData, T> requester(EventTransmitter transmitter,
                                                                                      EventReceiver<EventData> receiver) {
        return new Requester<>(ID, transmitter, receiver);
    }

    public static <T extends EventData> Responder<BoardChangedEventData, T> responder(EventTransmitter transmitter,
                                                                                      EventReceiver<EventData> receiver,
                                                                                      Function<BoardChangedEventData, T> response) {
        return new Responder<>(ID, transmitter, receiver, response);
    }

    @Override
    public String getId() {
        return ID;
    }
}
