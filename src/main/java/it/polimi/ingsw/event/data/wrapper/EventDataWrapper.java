package it.polimi.ingsw.event.data.wrapper;

import it.polimi.ingsw.event.data.EventData;

/**
 * Represents an {@link EventData} which decorates another "primitive" EventData through additional
 * information. It is useful when we want to add information of the same type to multiple primitive events
 * without redundancy.
 * As an example it is used for performing synchronous requests for different events. In fact, in order to support
 * synchronous requests, an integer is attached to the primitive EventData of the request; the same integer
 * will be added to the response, allowing for request-response coupling.
 * Since EventDataWrapper is an EventData, it must provide an identifier. Of course the identifier should depend
 * also on the type of the primitive EventData. For this reason every EventDataWrapper provides a "wrapper
 * identifier" through {@link EventDataWrapper#getWrapperId()}. The identifier of the whole EventData will be
 * {@code getWrapperId() + "_" + primitiveEventData.getId()}.
 *
 * @param <T> is the type of the primitive EventData.
 */
public abstract class EventDataWrapper<T extends EventData> implements EventData {
    /**
     * @return the wrapper id to allow events deserialization.
     */
    protected abstract String getWrapperId();

    /**
     * Is the wrapped primitive EventData.
     */
    protected final T wrappedData;

    /**
     * Constructor of the class. Assigns the primitive EventData.
     *
     * @param dataToWrap is the primitive EventData which will be wrapped.
     */
    protected EventDataWrapper(T dataToWrap) {
        this.wrappedData = dataToWrap;
    }

    /**
     * @return the wrapped primitive EventData.
     */
    public T getWrappedData() {
        return wrappedData;
    }

    @Override
    public String getId() {
        return getWrapperId() + "_" + wrappedData.getId();
    }
}
