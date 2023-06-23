package it.polimi.ingsw.event.data;

/**
 * Represents an EventData which does not carry any information.
 * @author Giacomo Groppi
 */
public class VoidEventData implements EventData {
    public final static String ID = "VOID";

    @Override
    public String getId() {
        return ID;
    }
}
