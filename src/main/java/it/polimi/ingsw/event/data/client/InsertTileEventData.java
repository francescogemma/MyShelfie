package it.polimi.ingsw.event.data.client;

import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.Responder;
import it.polimi.ingsw.event.data.EventData;
import it.polimi.ingsw.event.receiver.CastEventReceiver;
import it.polimi.ingsw.event.receiver.EventReceiver;
import it.polimi.ingsw.event.transmitter.EventTransmitter;

import java.util.function.Function;

public class InsertTileEventData implements EventData {
    public static final String ID = "INSERT_TILE";
    private final int column;

    public InsertTileEventData(int column) {
        this.column = column;
    }

    public int getColumn () {
        return this.column;
    }

    public static CastEventReceiver<InsertTileEventData> castEventReceiver(EventReceiver<EventData> receiver) {
        return new CastEventReceiver<>(ID, receiver);
    }

    public static <T extends EventData> Requester<InsertTileEventData, T> requester(EventTransmitter transmitter,
                                                                                  EventReceiver<EventData> receiver) {
        return new Requester<>(ID, transmitter, receiver);
    }

    public static <T extends EventData> Responder<InsertTileEventData, T> responder(EventTransmitter transmitter,
                                                                                  EventReceiver<EventData> receiver,
                                                                                  Function<InsertTileEventData, T> response) {
        return new Responder<>(ID, transmitter, receiver, response);
    }

    @Override
    public String getId() {
        return ID;
    }
}
