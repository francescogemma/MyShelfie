package it.polimi.ingsw.event.data.game;

import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.Responder;
import it.polimi.ingsw.event.data.EventData;
import it.polimi.ingsw.event.receiver.CastEventReceiver;
import it.polimi.ingsw.event.receiver.EventReceiver;
import it.polimi.ingsw.event.transmitter.EventTransmitter;
import it.polimi.ingsw.model.bookshelf.BookshelfMaskSet;
import it.polimi.ingsw.model.game.Player;

import java.util.function.Function;

public class PlayerPointsChangeEventData implements EventData {
    private final BookshelfMaskSet maskSet;
    private final Player player;

    public static final String ID = "PLAYER_POINTS_CHANGE";

    public PlayerPointsChangeEventData(Player player, BookshelfMaskSet maskSet) {
        this.player = player;
        this.maskSet = maskSet;
    }

    public BookshelfMaskSet getBookshelfMaskSet() {
        return this.maskSet;
    }

    public Player getPlayer() {
        return this.player;
    }

    public static CastEventReceiver<PlayerPointsChangeEventData> castEventReceiver(EventReceiver<EventData> receiver) {
        return new CastEventReceiver<>(ID, receiver);
    }

    public static <T extends EventData> Requester<PlayerPointsChangeEventData, T> requester(EventTransmitter transmitter,
                                                                                 EventReceiver<EventData> receiver) {
        return new Requester<>(ID, transmitter, receiver);
    }

    public static <T extends EventData> Responder<PlayerPointsChangeEventData, T> responder(EventTransmitter transmitter,
                                                                                 EventReceiver<EventData> receiver,
                                                                                 Function<PlayerPointsChangeEventData, T> response) {
        return new Responder<>(ID, transmitter, receiver, response);
    }

    @Override
    public String getId() {
        return ID;
    }
}
