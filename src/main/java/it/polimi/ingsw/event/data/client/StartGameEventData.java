package it.polimi.ingsw.event.data.client;

import it.polimi.ingsw.controller.servercontroller.GameController;
import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.Responder;
import it.polimi.ingsw.event.data.EventData;
import it.polimi.ingsw.event.receiver.CastEventReceiver;
import it.polimi.ingsw.event.receiver.EventReceiver;
import it.polimi.ingsw.event.transmitter.EventTransmitter;

import java.util.function.Function;

/**
 * This event is used to request the server to start the game
 * for which the player is in the lobby.
 *
 * @see it.polimi.ingsw.controller.servercontroller.MenuController#startGame(GameController, String)
 * @author Giacomo Groppi
 * */
public class StartGameEventData implements EventData {
    public static final String ID = "START_GAME";

    public static CastEventReceiver<StartGameEventData>
            castEventReceiver(EventReceiver<EventData> receiver)
    {
        return new CastEventReceiver<>(ID, receiver);
    }

    public static <T extends EventData> Requester<StartGameEventData, T>
            requester(EventTransmitter transmitter,
                      EventReceiver<EventData> receiver,
                      Object responsesLock)
    {
        return new Requester<>(ID, transmitter, receiver, responsesLock);
    }

    public static <T extends EventData> Responder<StartGameEventData, T>
            responder(EventTransmitter transmitter,
                      EventReceiver<EventData> receiver,
                      Function<StartGameEventData, T> response)
    {
        return new Responder<>(ID, transmitter, receiver, response);
    }

    @Override
    public String getId() {
        return ID;
    }
}
