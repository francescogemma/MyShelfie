package it.polimi.ingsw.event.data;

import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.Responder;
import it.polimi.ingsw.event.receiver.CastEventReceiver;
import it.polimi.ingsw.event.receiver.EventReceiver;
import it.polimi.ingsw.event.transmitter.EventTransmitter;
import it.polimi.ingsw.model.tile.TileColor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class InsertTilesEventData extends EventData {
    private int column;
    private List<TileColor> tileColors;

    public InsertTilesEventData(int column, List<TileColor> tileColors) {
        this.column = column;
        this.tileColors = new ArrayList<>(tileColors);
    }

    public int getColumn() {
        return column;
    }

    public List<TileColor> getTiles() {
        return new ArrayList<>(tileColors);
    }

    public static final String ID = "INSERT_TILES";

    @Override
    public String getId() {
        return ID;
    }

    public static CastEventReceiver<InsertTilesEventData> castEventReceiver(EventReceiver<EventData> receiver) {
        return new CastEventReceiver<>(ID, receiver);
    }

    public static <T extends EventData> Requester<InsertTilesEventData, T> requester(EventTransmitter transmitter,
                                                                                     EventReceiver<EventData> receiver) {
        return new Requester<>(ID, transmitter, receiver);
    }

    public static <T extends EventData> Responder<InsertTilesEventData, T> responder(EventTransmitter transmitter,
                                                                                     EventReceiver<EventData> receiver,
                                                                                     Function<InsertTilesEventData, T> response) {
        return new Responder<>(ID, transmitter, receiver, response);
    }
}
