package it.polimi.ingsw.event.data.wrapper;

import it.polimi.ingsw.event.data.EventData;

/**
 * Represents an {@link EventData} which decorates another "primitive" EventData through additional
 * information. It is useful when we want to add information of the same type to multiple primitive events
 * without redundancy.
 * As an example it is used for performing synchronous requests for different events.
 *
 * TODO:
 *
 * @param <T>
 */
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
