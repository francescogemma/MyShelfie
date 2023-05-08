package it.polimi.ingsw.event.data.game;

import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.Responder;
import it.polimi.ingsw.event.data.EventData;
import it.polimi.ingsw.event.receiver.CastEventReceiver;
import it.polimi.ingsw.event.receiver.EventReceiver;
import it.polimi.ingsw.event.transmitter.EventTransmitter;
import it.polimi.ingsw.model.bookshelf.BookshelfView;

import java.util.Objects;
import java.util.function.Function;

/**
 * This event indicates that the bookshelf has changed.
 * It is sent to all users in the game whenever there is a change in the bookshelf.
 *
 * @see BookshelfView
 * @see EventData
 * @author Giacomo Groppi
 */
public record BookshelfHasChangedEventData(String username, BookshelfView bookshelf) implements EventData {

    public BookshelfHasChangedEventData {
        Objects.requireNonNull(username);
        Objects.requireNonNull(bookshelf);
    }

    public static final String ID = "BOOKSHELF_HAS_CHANGED";

    public static CastEventReceiver<BookshelfHasChangedEventData> castEventReceiver(EventReceiver<EventData> receiver) {
        return new CastEventReceiver<>(ID, receiver);
    }

    public static <T extends EventData> Requester<BookshelfHasChangedEventData, T> requester(EventTransmitter transmitter,
                                                                                             EventReceiver<EventData> receiver,
                                                                                             Object responsesLock) {
        return new Requester<>(ID, transmitter, receiver, responsesLock);
    }

    public static <T extends EventData> Responder<BookshelfHasChangedEventData, T> responder(EventTransmitter transmitter,
                                                                                             EventReceiver<EventData> receiver,
                                                                                             Function<BookshelfHasChangedEventData, T> response) {
        return new Responder<>(ID, transmitter, receiver, response);
    }

    @Override
    public String getId() {
        return ID;
    }

}
