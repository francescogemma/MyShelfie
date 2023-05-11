package it.polimi.ingsw.controller;

public enum ResponseStatus {
    SUCCESS,
    FAILURE;

    @Override
    public String toString() {
        return this.name();
    }
}
