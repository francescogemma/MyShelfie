package it.polimi.ingsw.event.data.game;

import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.Responder;
import it.polimi.ingsw.event.data.EventData;
import it.polimi.ingsw.event.receiver.CastEventReceiver;
import it.polimi.ingsw.event.receiver.EventReceiver;
import it.polimi.ingsw.event.transmitter.EventTransmitter;
import it.polimi.ingsw.model.board.BoardView;

import java.util.Objects;
import java.util.function.Function;

/**
 * The {@code BoardChangedEventData} event indicates that ghe game board has changed.
 * This event is send to all participants in the game.
 *
 * @see BoardView
 * @see EventData
 * @author Giacomo Groppi
 * */
public record BoardChangedEventData(BoardView board) implements EventData {
    public static final String ID = "BOARD_CHANGE";

    /**
     * Constructs a new {@code BoardChangedEventData} event with the give {@link BoardView boardView} arguments.
     * @throws NullPointerException iff board is null
     *
     * @see BoardView
     * */
    public BoardChangedEventData {
        Objects.requireNonNull(board);
    }

    public static CastEventReceiver<BoardChangedEventData> castEventReceiver(EventReceiver<EventData> receiver) {
        return new CastEventReceiver<>(ID, receiver);
    }

    public static <T extends EventData> Requester<BoardChangedEventData, T> requester(EventTransmitter transmitter,
                                                                                      EventReceiver<EventData> receiver,
                                                                                      Object responsesLock) {
        return new Requester<>(ID, transmitter, receiver, responsesLock);
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
