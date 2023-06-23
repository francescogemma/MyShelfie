package it.polimi.ingsw.controller.db;

/**
 * Exception thrown when we try to load from the disk an Identifiable which has never been saved.
 */
public class IdentifiableNotFoundException extends Exception {
    /**
     * Constructor of the class.
     *
     * @param name is the name which uniquely identifies the Identifiable that hasn't been found.
     * @param typeName is the name of the type of the Identifiable that hasn't been found
     *                 (for example: user, game, ...).
     */
    public IdentifiableNotFoundException(String name, String typeName) {
        super("There is no saved " + typeName + " named: " + name);
    }
}
