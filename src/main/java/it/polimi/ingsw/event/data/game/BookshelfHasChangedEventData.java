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

    /**
     * Constructor of the class.
     *
     * @param username is the username of the player whose {@link it.polimi.ingsw.model.bookshelf.Bookshelf} has changed.
     * @param bookshelf is the new {@link it.polimi.ingsw.model.bookshelf.Bookshelf} after the change.
     */
    public BookshelfHasChangedEventData {
        Objects.requireNonNull(username);
        Objects.requireNonNull(bookshelf);
    }

    /**
     * Unique identifier for the EventData, added for serialization purposes accordingly to {@link EventData}
     * interface contract.
     */
    public static final String ID = "BOOKSHELF_HAS_CHANGED";

    /**
     * {@link CastEventReceiver} factory method for the EventData, added accordingly to {@link EventData}
     * interface contract.
     *
     * @param receiver is the {@link EventReceiver} which will receive instances of this EventData.
     * @return a {@link CastEventReceiver} which filters the events received by receiver and provides those
     * of this EventData type to an {@link it.polimi.ingsw.event.receiver.EventListener} after a cast.
     */
    public static CastEventReceiver<BookshelfHasChangedEventData> castEventReceiver(EventReceiver<EventData> receiver) {
        return new CastEventReceiver<>(ID, receiver);
    }

    /**
     * {@link Requester} factory method that performs requests which receive responses of this EventData, added
     * accordingly to {@link EventData} interface contract.
     *
     * @param transmitter is the {@link EventTransmitter} on which the request is sent.
     * @param receiver is the {@link EventReceiver} which receives the response to the request.
     * @param responsesLock is the lock Object on which the {@link Requester} synchronizes to wait for the
     *                      response.
     * @return a {@link Requester} which is capable of performing requests which receive responses of this
     * EventData.
     * @param <T> is the type of the request EventData.
     */
    public static <T extends EventData> Requester<BookshelfHasChangedEventData, T> requester(EventTransmitter transmitter,
                                                                                             EventReceiver<EventData> receiver,
                                                                                             Object responsesLock) {
        return new Requester<>(ID, transmitter, receiver, responsesLock);
    }

    /**
     * {@link Responder} factory method which allows to respond to requests of this EventData, added accordingly
     * to {@link EventData} interface contract.
     *
     * @param transmitter is the {@link EventTransmitter} on which the response is sent.
     * @param receiver is the {@link EventReceiver} which receives the request.
     * @param response is the function which allows to compute the appropriate response to the received request.
     * @return a {@link Responder} which is capable of answering requests of this EventData.
     * @param <T> is the type of the response EventData.
     */
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
