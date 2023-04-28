package it.polimi.ingsw.event.data.game;

import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.Responder;
import it.polimi.ingsw.event.data.EventData;
import it.polimi.ingsw.event.receiver.CastEventReceiver;
import it.polimi.ingsw.event.receiver.EventReceiver;
import it.polimi.ingsw.event.transmitter.EventTransmitter;
import it.polimi.ingsw.model.bookshelf.BookshelfMaskSet;
import it.polimi.ingsw.model.game.Player;
import it.polimi.ingsw.model.game.PlayerView;

import java.util.function.Function;

public class CommonGoalCompletedEventData implements EventData {
    private final BookshelfMaskSet maskSet;
    private final PlayerView player;
    private final int commonGoalCompleted;

    public static final String ID = "COMMON_GOAL_COMPLETED";

    public CommonGoalCompletedEventData(PlayerView player, BookshelfMaskSet maskSet, int commonGoalCompleted) {
        assert player.getClass() == PlayerView.class;
        this.player = player;
        this.maskSet = maskSet;
        this.commonGoalCompleted = commonGoalCompleted;
    }

    public BookshelfMaskSet getBookshelfMaskSet() {
        return this.maskSet;
    }

    public PlayerView getPlayer() {
        return this.player;
    }

    public int getCommonGoalCompleted () {
        return this.commonGoalCompleted;
    }

    public static CastEventReceiver<CommonGoalCompletedEventData> castEventReceiver(EventReceiver<EventData> receiver) {
        return new CastEventReceiver<>(ID, receiver);
    }

    public static <T extends EventData> Requester<CommonGoalCompletedEventData, T> requester(EventTransmitter transmitter,
                                                                                             EventReceiver<EventData> receiver,
                                                                                             Object responsesLock) {
        return new Requester<>(ID, transmitter, receiver, responsesLock);
    }

    public static <T extends EventData> Responder<CommonGoalCompletedEventData, T> responder(EventTransmitter transmitter,
                                                                                             EventReceiver<EventData> receiver,
                                                                                             Function<CommonGoalCompletedEventData, T> response) {
        return new Responder<>(ID, transmitter, receiver, response);
    }

    @Override
    public String getId() {
        return ID;
    }
}
