package it.polimi.ingsw.event;

public class AuthenticatedEventTransmitter<T> implements EventTransmitter<T> {
    private final String username;
    private final String password;
    private final EventTransmitter<Object> transmitter;

    public AuthenticatedEventTransmitter(String username, String password, EventTransmitter<Object> transmitter) {
        this.username = username;
        this.password = password;
        this.transmitter = transmitter;
    }

    @Override
    public void broadcast(Event<T> event) {
        transmitter.broadcast(new Event<>("AUTHORIZED_" + event.getId(),
            new AuthenticatedEventData<T>(username, password, event.getData())));
    }
}
