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
 * Use this event to request the server to select the Tile at the specified coordinate.
 *
 * @param coordinate The coordinate where the Tile should be selected.
 * @see Coordinate
 * @see it.polimi.ingsw.model.game.Game#selectTile(String, Coordinate)
 * @see it.polimi.ingsw.controller.servercontroller.GameController#selectTile(String, Coordinate) 
 * @author Giacomo Groppi
 * */
public record SelectTileEventData(Coordinate coordinate) implements EventData {
    public static final String ID = "SELECT_TILE";

    public static CastEventReceiver<SelectTileEventData> castEventReceiver(EventReceiver<EventData> receiver) {
        return new CastEventReceiver<>(ID, receiver);
    }

    public static <T extends EventData> Requester<SelectTileEventData, T> requester(EventTransmitter transmitter,
                                                                                    EventReceiver<EventData> receiver,
                                                                                    Object responsesLock) {
        return new Requester<>(ID, transmitter, receiver, responsesLock);
    }

    public static <T extends EventData> Responder<SelectTileEventData, T> responder(EventTransmitter transmitter,
                                                                                    EventReceiver<EventData> receiver,
                                                                                    Function<SelectTileEventData, T> response) {
        return new Responder<>(ID, transmitter, receiver, response);
    }

    @Override
    public String getId() {
        return ID;
    }
}
