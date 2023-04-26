package it.polimi.ingsw.event.data.game;

import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.Responder;
import it.polimi.ingsw.event.data.EventData;
import it.polimi.ingsw.event.receiver.CastEventReceiver;
import it.polimi.ingsw.event.receiver.EventReceiver;
import it.polimi.ingsw.event.transmitter.EventTransmitter;
import it.polimi.ingsw.model.bookshelf.Bookshelf;
import it.polimi.ingsw.model.game.PlayerView;

import java.util.function.Function;

public class BookshelfHasChanged implements EventData {
    private final Bookshelf bookshelf;
    private final String username;

    public BookshelfHasChanged(String username, Bookshelf bookshelf) {
        this.username = username;
        this.bookshelf = new Bookshelf(bookshelf);
    }

    public static final String ID = "CURRENT_PLAYER_CHANGED";

    public String getUsername() {
        return this.username;
    }
    public Bookshelf getBookshelf () {
        return new Bookshelf(bookshelf);
    }

    public static CastEventReceiver<BookshelfHasChanged> castEventReceiver(EventReceiver<EventData> receiver) {
        return new CastEventReceiver<>(ID, receiver);
    }

    public static <T extends EventData> Requester<BookshelfHasChanged, T> requester(EventTransmitter transmitter,
                                                                                              EventReceiver<EventData> receiver,
                                                                                              Object responsesLock) {
        return new Requester<>(ID, transmitter, receiver, responsesLock);
    }

    public static <T extends EventData> Responder<BookshelfHasChanged, T> responder(EventTransmitter transmitter,
                                                                                              EventReceiver<EventData> receiver,
                                                                                              Function<BookshelfHasChanged, T> response) {
        return new Responder<>(ID, transmitter, receiver, response);
    }

    @Override
    public String getId() {
        return ID;
    }

}
