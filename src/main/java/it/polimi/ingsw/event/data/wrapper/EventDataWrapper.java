package it.polimi.ingsw.event.data.wrapper;

import it.polimi.ingsw.event.data.EventData;

public abstract class EventDataWrapper<T extends EventData> implements EventData {
    protected abstract String getWrapperId();

    protected final T wrappedData;
    protected EventDataWrapper(T dataToWrap) {
        this.wrappedData = dataToWrap;
    }

    public T getWrappedData() {
        return wrappedData;
    }

    @Override
    public String getId() {
        return getWrapperId() + "_" + wrappedData.getId();
    }
}
