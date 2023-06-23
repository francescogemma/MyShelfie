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

/**
 * This event indicates that a player has completed a common goal.
 * @author Giacomo Groppi
 * */
public class CommonGoalCompletedEventData implements EventData {
    /**
     * The {@link BookshelfMaskSet} of the goal completed.
     * */
    private final BookshelfMaskSet maskSet;

    /**
     * The {@link Player} that completed the goal.
     */
    private final Player player;

    /**
     * The index of the common goal completed.
     * */
    private final int commonGoalCompleted;

    /**
     * The points earned by the player.
     * */
    private final int points;

    public static final String ID = "COMMON_GOAL_COMPLETED";

    /**
     * Constructor of the event.
     * @param player The {@link Player} that completed the goal.
     * @param points The points earned by the player.
     * @param maskSet The {@link BookshelfMaskSet} of the goal completed.
     * @param commonGoalCompleted The index of the common goal completed.
     * */
    public CommonGoalCompletedEventData(Player player, int points, BookshelfMaskSet maskSet, int commonGoalCompleted) {
        this.player = player;
        this.maskSet = maskSet;
        this.commonGoalCompleted = commonGoalCompleted;
        this.points = points;
    }

    /**
     * Getter for the points earned by the player.
     * @return The points earned by the player.
     * */
    public int getPoints () {
        return points;
    }

    /**
     * Getter for the {@link BookshelfMaskSet} of the goal completed.
     * @return The {@link BookshelfMaskSet} of the goal completed.
     * */
    public BookshelfMaskSet getBookshelfMaskSet() {
        return this.maskSet;
    }

    /**
     * Getter for the {@link Player} that completed the goal.
     * @return The {@link Player} that completed the goal.
     * */
    public Player getPlayer() {
        return new Player(player);
    }

    /**
     * Getter for the index of the common goal completed.
     * @return The index of the common goal completed.
     * */
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
