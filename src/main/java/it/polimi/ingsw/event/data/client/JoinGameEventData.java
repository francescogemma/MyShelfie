package it.polimi.ingsw.event.data.client;

import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.Responder;
import it.polimi.ingsw.event.data.EventData;
import it.polimi.ingsw.event.receiver.CastEventReceiver;
import it.polimi.ingsw.event.receiver.EventReceiver;
import it.polimi.ingsw.event.transmitter.EventTransmitter;

import java.util.Objects;
import java.util.function.Function;

/**
 * This event is used to request the server to join a game. The request
 * can be made from both the lobby and the menu.
 *
 * @see it.polimi.ingsw.controller.servercontroller.MenuController#joinGame(EventTransmitter, String, String)
 * @see it.polimi.ingsw.controller.servercontroller.GameController#joinGame(String)
 * @author Giacomo Groppi
 * */
public class JoinGameEventData implements EventData {
    public static final String ID = "JOIN_GAME";

    /**
     * The name of the game you want to join.
     * */
    private final String gameName;

    /**
     * Constructor of the class.
     * @param gameName The name of the game you want to join.
     * @throws NullPointerException iff gameName is null
     * */
    public JoinGameEventData(String gameName) {
        Objects.requireNonNull(gameName);
        this.gameName = gameName;
    }

    /**
     * This method returns the name of the game you want to join.
     * @return The name of the game you want to join.
     * */
    public String getGameName() {
        return gameName;
    }

    public static CastEventReceiver<JoinGameEventData> castEventReceiver(EventReceiver<EventData> receiver) {
        return new CastEventReceiver<>(ID, receiver);
    }

    public static <T extends EventData> Requester<JoinGameEventData, T> requester(EventTransmitter transmitter,
                                                                                  EventReceiver<EventData> receiver,
                                                                                  Object responsesLock) {
        return new Requester<>(ID, transmitter, receiver, responsesLock);
    }

    public static <T extends EventData> Responder<JoinGameEventData, T> responder(EventTransmitter transmitter,
                                                                                  EventReceiver<EventData> receiver,
                                                                                  Function<JoinGameEventData, T> response) {
        return new Responder<>(ID, transmitter, receiver, response);
    }

    @Override
    public String getId() {
        return ID;
    }
}
