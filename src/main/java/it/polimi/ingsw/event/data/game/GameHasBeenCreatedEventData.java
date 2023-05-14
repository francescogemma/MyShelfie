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

public class GameHasBeenCreatedEventData implements EventData {
    private final List<AvailableGame> games;
    public static final String ID = "GAME_HAS_BEEN_CREATED";

    public static class AvailableGame {
        private final String owner;
        private final String name;
        private final boolean isStarted;
        private final boolean isStopped;

        public AvailableGame(GameView gameView) {
            this.owner = gameView.getOwner();
            this.name = gameView.getName();
            this.isStarted = gameView.isStarted();
            this.isStopped = gameView.isStopped();
        }

        public String owner() {
            return owner;
        }

        public String name() {
            return name;
        }

        public boolean isStarted() {
            return isStarted;
        }

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


    public GameHasBeenCreatedEventData(List<AvailableGame> games) {
        this.games = Collections.unmodifiableList(games);
    }

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
