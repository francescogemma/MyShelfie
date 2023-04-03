package it.polimi.ingsw.event;

public class MessageEventData {
    private final String message;

    public MessageEventData(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
