package it.polimi.ingsw.event.data.wrapper;

import it.polimi.ingsw.event.data.EventData;

public class SyncEventDataWrapper<T extends EventData> extends EventDataWrapper<T> {
    private final int count;

    public SyncEventDataWrapper(int count, T dataToWrap) {
        super(dataToWrap);
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public static final String WRAPPER_ID = "SYNCHRONIZED";

    @Override
    public String getWrapperId() {
        return WRAPPER_ID;
    }
}
