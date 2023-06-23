package it.polimi.ingsw.event.data.game;

import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.Responder;
import it.polimi.ingsw.event.data.EventData;
import it.polimi.ingsw.event.receiver.CastEventReceiver;
import it.polimi.ingsw.event.receiver.EventReceiver;
import it.polimi.ingsw.event.transmitter.EventTransmitter;
import it.polimi.ingsw.model.game.GameView;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * This event indicates that a game has been created.
 * All the game contains in the event are accessible by the player who received the event.
 * @author Giacomo Groppi
 */
public class GameHasBeenCreatedEventData implements EventData {
    /**
     * The list of the available games.
     */
    private final List<AvailableGame> games;
    public static final String ID = "GAME_HAS_BEEN_CREATED";

    /**
     * This class represents a game that is available to join for
     * the player who received the event
     */
    public static class AvailableGame {
        /**
         * The owner of the game.
         */
        private final String owner;

        /**
         * The name of the game.
         */
        private final String name;

        /**
         * Indicates if the game has started.
         */
        private final boolean isStarted;

        /**
         * Indicates if the game has stopped.
         */
        private final boolean isStopped;

        /**
         * Creates a new AvailableGame object from a GameView object.
         * @param gameView the game that the user can join
         */
        public AvailableGame(GameView gameView) {
            this.owner = gameView.getOwner();
            this.name = gameView.getName();
            this.isStarted = gameView.isStarted();
            this.isStopped = gameView.isStopped();
        }

        /**
         * Getter for the owner of the game.
         * @return The owner of the game.
         */
        public String owner() {
            return owner;
        }

        /**
         * Getter for the name of the game.
         * @return The name of the game.
         * */
        public String name() {
            return name;
        }

        /**
         * @return True if the game has started, false otherwise.
         * */
        public boolean isStarted() {
            return isStarted;
        }

        /**
         * @return True if the game has stopped, false otherwise.
         * */
        public boolean isStopped() {
            return isStopped;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this)
                return true;
            if (obj == null || obj.getClass() != this.getClass())
                return false;
            if (((AvailableGame) obj).isStopped != isStopped)
                return false;
            if (((AvailableGame) obj).isStarted != isStarted)
                return false;
            if (!((AvailableGame) obj).name.equals(name))
                return false;
            if (!((AvailableGame) obj).owner.equals(owner))
                return false;
            return true;
        }
    }

    /**
     * Create a new instance of the event.
     * @param games The list of the available games for the player.
     * */
    public GameHasBeenCreatedEventData(List<AvailableGame> games) {
        this.games = Collections.unmodifiableList(games);
    }

    /**
     * Create a new instance of the event.
     * @param gameView The game that the player can join.
     * */
    public GameHasBeenCreatedEventData(GameView gameView) {
        this.games = List.of(new AvailableGame(gameView));
    }

    public static CastEventReceiver<GameHasBeenCreatedEventData> castEventReceiver(EventReceiver<EventData> receiver) {
        return new CastEventReceiver<>(ID, receiver);
    }

    public static <T extends EventData> Requester<GameHasBeenCreatedEventData, T> requester(EventTransmitter transmitter,
                                                                                            EventReceiver<EventData> receiver,
                                                                                            Object responsesLock) {
        return new Requester<>(ID, transmitter, receiver, responsesLock);
    }

    public static <T extends EventData> Responder<GameHasBeenCreatedEventData, T> responder(EventTransmitter transmitter,
                                                                                            EventReceiver<EventData> receiver,
                                                                                            Function<GameHasBeenCreatedEventData, T> response) {
        return new Responder<>(ID, transmitter, receiver, response);
    }

    /**
     * Getter for the list of the available games.
     * @return The list of the available games.
     * */
    public List<AvailableGame> getNames () {
        return games;
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String toString() {
        return this.games.toString();
    }
}
