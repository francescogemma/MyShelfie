package it.polimi.ingsw.controller;

public class Response {
    private final String message;
    private final ResponseStatus status;

    public Response(String message, ResponseStatus status) {
        this.message = message;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public ResponseStatus getStatus() {
        return status;
    }
}
