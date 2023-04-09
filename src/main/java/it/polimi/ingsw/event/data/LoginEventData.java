package it.polimi.ingsw.event.data;

import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.Responder;
import it.polimi.ingsw.event.receiver.CastEventReceiver;
import it.polimi.ingsw.event.receiver.EventReceiver;
import it.polimi.ingsw.event.transmitter.EventTransmitter;

import java.util.function.Function;

public class LoginEventData implements EventData {
    private final String username;
    private final String password;

    public LoginEventData(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public static final String ID = "LOGIN";

    @Override
    public String getId() {
        return ID;
    }

    public static CastEventReceiver<LoginEventData> castEventReceiver(EventReceiver<EventData> receiver) {
        return new CastEventReceiver<>(ID, receiver);
    }

    public static <T extends EventData> Requester<LoginEventData, T> requester(EventTransmitter transmitter,
                                                             EventReceiver<EventData> receiver) {
        return new Requester<>(ID, transmitter, receiver);
    }

    public static <T extends EventData>Responder<LoginEventData, T> responder(EventTransmitter transmitter,
                                                                              EventReceiver<EventData> receiver,
                                                                              Function<LoginEventData, T> response) {
        return new Responder<>(ID, transmitter, receiver, response);
    }
}
