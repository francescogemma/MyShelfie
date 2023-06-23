package it.polimi.ingsw.event.data.game;

import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.Responder;
import it.polimi.ingsw.event.data.EventData;
import it.polimi.ingsw.event.receiver.CastEventReceiver;
import it.polimi.ingsw.event.receiver.EventReceiver;
import it.polimi.ingsw.event.transmitter.EventTransmitter;
import it.polimi.ingsw.utils.Coordinate;
import it.polimi.ingsw.utils.Logger;

import java.util.function.Function;

/**
 * Event sent to notify the players that the current player has deselected a tile from the
 * {@link it.polimi.ingsw.model.board.Board}.
 * @param coordinate The coordinate of the tile that has been deselected.
 * @author Giacomo Groppi
 */
public record PlayerHasDeselectTile(Coordinate coordinate) implements EventData {
    public static final String ID = "PLAYER_HAS_DESELECT_TILE";

    public static CastEventReceiver<PlayerHasDeselectTile> castEventReceiver(EventReceiver<EventData> receiver) {
        return new CastEventReceiver<>(ID, receiver);
    }

    public static <T extends EventData> Requester<PlayerHasDeselectTile, T> requester(EventTransmitter transmitter,
                                                                                      EventReceiver<EventData> receiver,
                                                                                      Object responsesLock) {
        return new Requester<>(ID, transmitter, receiver, responsesLock);
    }

    public static <T extends EventData> Responder<PlayerHasDeselectTile, T> responder(EventTransmitter transmitter,
                                                                                      EventReceiver<EventData> receiver,
                                                                                      Function<PlayerHasDeselectTile, T> response) {
        return new Responder<>(ID, transmitter, receiver, response);
    }

    @Override
    public String getId() {
        return ID;
    }
}
