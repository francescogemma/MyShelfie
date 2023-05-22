package it.polimi.ingsw.event.data;

public class VoidEventData implements EventData {
    public final static String ID = "VOID";

    @Override
    public String getId() {
        return ID;
    }
}
