package it.polimi.ingsw.event.data.wrapper;

import it.polimi.ingsw.event.data.EventData;

/**
 * Represents an {@link EventData} which decorates another EventData through additional
 * information. It is useful when we want to add the same kind of information to different types of events
 * without redundancy. (It is possible to recursively wrap a wrapper inside another wrapper).
 * As an example it is used for performing synchronous requests for different events. In fact, in order to support
 * synchronous requests, an integer is attached to the primitive EventData of the request; the same integer
 * will be added to the response, allowing for request-response coupling.
 * Since EventDataWrapper is an EventData, it must provide an identifier. Of course the identifier should depend
 * also on the type of the wrapped EventData. For this reason every EventDataWrapper provides a "wrapper
 * identifier" through {@link EventDataWrapper#getWrapperId()}. The identifier of the whole EventData will be
 * {@code getWrapperId() + "_" + wrappedEventData.getId()}.
 * By convention the EventDataWrapper subclass referred to "some event wrapper name" should be called
 * SomeEventWrapperNameEventDataWrapper and its wrapper identifier should be SOME_EVENT_WRAPPER_NAME.
 *
 * @param <T> is the type of the wrapped EventData.
 *
 * @author Cristiano Migali
 */
public abstract class EventDataWrapper<T extends EventData> implements EventData {
    /**
     * @return the wrapper id to allow events deserialization.
     */
    protected abstract String getWrapperId();

    /**
     * Is the wrapped EventData.
     */
    protected final T wrappedData;

    /**
     * Constructor of the class. Assigns the wrapped EventData.
     *
     * @param dataToWrap is the EventData which will be wrapped.
     */
    protected EventDataWrapper(T dataToWrap) {
        this.wrappedData = dataToWrap;
    }

    /**
     * @return the wrapped EventData.
     */
    public T getWrappedData() {
        return wrappedData;
    }

    @Override
    public String getId() {
        return getWrapperId() + "_" + wrappedData.getId();
    }
}
