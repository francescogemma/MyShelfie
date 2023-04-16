package it.polimi.ingsw.controller.db;

public class IdentifiableNotFoundException extends Exception {
    public IdentifiableNotFoundException(String name, String typeName) {
        super("There is no saved " + typeName + " named: " + name);
    }
}
