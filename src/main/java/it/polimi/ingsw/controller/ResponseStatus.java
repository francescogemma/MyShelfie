package it.polimi.ingsw.controller;

/**
 * It is the result status of an operation performed server-side after a synchronous request from the client.
 *
 * @author Giacomo Groppi
 */
public enum ResponseStatus {
    /**
     * The operation has succeeded.
     */
    SUCCESS,

    /**
     * The operation has failed.
     */
    FAILURE;

    @Override
    public String toString() {
        return this.name();
    }
}
