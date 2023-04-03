package it.polimi.ingsw.event;

public class CastEventTransmitter<T> implements EventTransmitter<T> {
    private final EventTransmitter<Object> transmitter;

    public CastEventTransmitter(EventTransmitter<Object> transmitter) {
        this.transmitter = transmitter;
    }

    @Override
    public void broadcast(Event<T> event) {
        transmitter.broadcast(new Event<Object>(event.getId(), event.getData()));
    }
}
