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

public class CommonGoalCompletedEventData implements EventData {
    private final BookshelfMaskSet maskSet;
    private final Player player;
    private final int commonGoalCompleted;
    private final int points;

    public static final String ID = "COMMON_GOAL_COMPLETED";

    public CommonGoalCompletedEventData(Player player, int points, BookshelfMaskSet maskSet, int commonGoalCompleted) {
        this.player = player;
        this.maskSet = maskSet;
        this.commonGoalCompleted = commonGoalCompleted;
        this.points = points;
    }

    public int getPoints () {
        return points;
    }

    public BookshelfMaskSet getBookshelfMaskSet() {
        return this.maskSet;
    }

    public Player getPlayer() {
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
