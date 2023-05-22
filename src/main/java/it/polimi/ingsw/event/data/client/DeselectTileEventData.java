package it.polimi.ingsw.event.data.client;

import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.Responder;
import it.polimi.ingsw.event.data.EventData;
import it.polimi.ingsw.event.receiver.CastEventReceiver;
import it.polimi.ingsw.event.receiver.EventReceiver;
import it.polimi.ingsw.event.transmitter.EventTransmitter;
import it.polimi.ingsw.utils.Coordinate;

import java.util.function.Function;

/**
 * This event is used to deselect the last selected tile.
 *
 * @param coordinate The coordinate of the tile to be deselected.
 * @see it.polimi.ingsw.controller.servercontroller.GameController#deselectTile(String, Coordinate)
 * @author Giacomo Groppi
 */
public record DeselectTileEventData(Coordinate coordinate) implements EventData {
    public static final String ID = "DESELECT_TILE";

    public static CastEventReceiver<DeselectTileEventData> castEventReceiver(EventReceiver<EventData> receiver) {
        return new CastEventReceiver<>(ID, receiver);
    }

    public static <T extends EventData> Requester<DeselectTileEventData, T> requester(EventTransmitter transmitter,
                                                                                      EventReceiver<EventData> receiver,
                                                                                      Object responsesLock) {
        return new Requester<>(ID, transmitter, receiver, responsesLock);
    }

    public static <T extends EventData> Responder<DeselectTileEventData, T> responder(EventTransmitter transmitter,
                                                                                      EventReceiver<EventData> receiver,
                                                                                      Function<DeselectTileEventData, T> response) {
        return new Responder<>(ID, transmitter, receiver, response);
    }

    @Override
    public String getId() {
        return ID;
    }
}
