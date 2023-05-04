package it.polimi.ingsw.event.data.client;

import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.Responder;
import it.polimi.ingsw.event.data.EventData;
import it.polimi.ingsw.event.receiver.CastEventReceiver;
import it.polimi.ingsw.event.receiver.EventReceiver;
import it.polimi.ingsw.event.transmitter.EventTransmitter;

import java.util.function.Function;


/**
 * utente chiede di uscire dalla lobby
 */
public class ExitLobbyEventData implements EventData {
    public static final String ID = "EXIT_LOBBY";

    public ExitLobbyEventData() {

    }

    public static CastEventReceiver<ExitLobbyEventData> castEventReceiver(EventReceiver<EventData> receiver) {
        return new CastEventReceiver<>(ID, receiver);
    }

    public static <T extends EventData> Requester<ExitLobbyEventData, T> requester(EventTransmitter transmitter,
                                                                                EventReceiver<EventData> receiver,
                                                                                Object responsesLock) {
        return new Requester<>(ID, transmitter, receiver, responsesLock);
    }

    public static <T extends EventData> Responder<ExitLobbyEventData, T> responder(EventTransmitter transmitter,
                                                                                EventReceiver<EventData> receiver,
                                                                                Function<ExitLobbyEventData, T> response) {
        return new Responder<>(ID, transmitter, receiver, response);
    }

    @Override
    public String getId() {
        return ID;
    }
}
