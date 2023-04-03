package it.polimi.ingsw.event;

public class Event<T> {
    private final String id;
    private final T data;

    public Event(String id, T data) {
        this.id = id;
        this.data = data;
    }

    public String getId() {
        return id;
    }

    public T getData() {
        return data;
    }
}
