package it.polimi.ingsw.event.data.clientEvent;

import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.Responder;
import it.polimi.ingsw.event.data.EventData;
import it.polimi.ingsw.event.receiver.CastEventReceiver;
import it.polimi.ingsw.event.receiver.EventReceiver;
import it.polimi.ingsw.event.transmitter.EventTransmitter;
import it.polimi.ingsw.utils.Coordinate;

import java.util.function.Function;

public class DeselectTileEventData implements EventData {
    public static final String ID = "DESELECT_TILE";
    private final Coordinate coordinate;

    public DeselectTileEventData(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    public Coordinate getCoordinate () {
        return this.coordinate;
    }

    public static <T extends DeselectTileEventData> CastEventReceiver<T> castEventReceiver(EventReceiver<EventData> receiver) {
        return new CastEventReceiver<>(ID, receiver);
    }

    public static <T extends EventData> Requester<DeselectTileEventData, T> requester(EventTransmitter transmitter,
                                                                                  EventReceiver<EventData> receiver) {
        return new Requester<>(ID, transmitter, receiver);
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
