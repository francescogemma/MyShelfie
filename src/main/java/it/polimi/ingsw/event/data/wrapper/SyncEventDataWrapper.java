package it.polimi.ingsw.event.data.wrapper;

import it.polimi.ingsw.event.data.EventData;

/**
 * Is the {@link EventDataWrapper} used to perform synchronous requests.
 * It allows to attach an integer to the primitive {@link EventData} of the request: the same integer
 * will be attached to the primitive EventData of the response, allowing to identify the response to a specific
 * request.
 *
 * @param <T> is the type of the primitive EventData of the request or the response.
 *
 * @author Cristiano Migali
 */
public class SyncEventDataWrapper<T extends EventData> extends EventDataWrapper<T> {
    /**
     * Is the integer attached to the primitive {@link EventData} of the request and the response.
     * It allows to couple a request with a response.
     */
    private final int count;

    /**
     * Constructor of the class.
     * It initializes the count attribute, that is the integer which allows synchronization, and the primitive
     * {@link EventData} of the request or the response.
     *
     * @param count is the integer that will be attached to the primitive EventData.
     * @param dataToWrap is the primitive EventData.
     */
    public SyncEventDataWrapper(int count, T dataToWrap) {
        super(dataToWrap);
        this.count = count;
    }

    /**
     * @return the attached integer.
     */
    public int getCount() {
        return count;
    }

    /**
     * Is the wrapper identifier of a SyncEventDataWrapper.
     */
    public static final String WRAPPER_ID = "SYNCHRONIZED";

    @Override
    public String getWrapperId() {
        return WRAPPER_ID;
    }
}
