package it.polimi.ingsw.event.data.game;

import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.Responder;
import it.polimi.ingsw.event.data.EventData;
import it.polimi.ingsw.event.receiver.CastEventReceiver;
import it.polimi.ingsw.event.receiver.EventReceiver;
import it.polimi.ingsw.event.transmitter.EventTransmitter;
import it.polimi.ingsw.model.bookshelf.Bookshelf;
import it.polimi.ingsw.utils.Logger;

import java.util.function.Function;

public class BookshelfHasChangedEventData implements EventData {
    // TODO: Use a BookshelfView instead
    private final Bookshelf bookshelf;
    private final String username;

    public BookshelfHasChangedEventData(String username, Bookshelf bookshelf) {
        this.username = username;
        this.bookshelf = new Bookshelf(bookshelf);
    }

    public static final String ID = "BOOKSHELF_HAS_CHANGED";

    public String getUsername() {
        return this.username;
    }
    public Bookshelf getBookshelf () {
        return new Bookshelf(bookshelf);
    }

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
