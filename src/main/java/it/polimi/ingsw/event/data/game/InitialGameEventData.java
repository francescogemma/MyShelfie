package it.polimi.ingsw.event.data.game;

import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.Responder;
import it.polimi.ingsw.event.data.EventData;
import it.polimi.ingsw.event.receiver.CastEventReceiver;
import it.polimi.ingsw.event.receiver.EventReceiver;
import it.polimi.ingsw.event.transmitter.EventTransmitter;
import it.polimi.ingsw.model.game.GameView;
import it.polimi.ingsw.utils.Logger;

import java.util.Objects;
import java.util.function.Function;

/**
 * Event sent to the client when the player has is ready to receive the initial game data.
 * @param gameView The game view of the game that the player is joining.
 * @author Giacomo Groppi
 */
public record InitialGameEventData(GameView gameView) implements EventData {
    public static final String ID = "INITIAL_GAME";

    public InitialGameEventData {
        Objects.requireNonNull(gameView);
        Objects.requireNonNull(gameView.getBoard());
        Objects.requireNonNull(gameView.getPlayers());
    }

    public static CastEventReceiver<InitialGameEventData> castEventReceiver(EventReceiver<EventData> receiver) {
        return new CastEventReceiver<>(ID, receiver);
    }

    public static <T extends EventData> Requester<InitialGameEventData, T> requester(EventTransmitter transmitter,
                                                                                     EventReceiver<EventData> receiver,
                                                                                     Object responsesLock) {
        return new Requester<>(ID, transmitter, receiver, responsesLock);
    }

    public static <T extends EventData> Responder<InitialGameEventData, T> responder(EventTransmitter transmitter,
                                                                                     EventReceiver<EventData> receiver,
                                                                                     Function<InitialGameEventData, T> response) {
        return new Responder<>(ID, transmitter, receiver, response);
    }

    @Override
    public String getId() {
        return ID;
    }
}
