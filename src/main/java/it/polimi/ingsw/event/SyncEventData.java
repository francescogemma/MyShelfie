package it.polimi.ingsw.event;

public class SyncEventData<T> {
    private final int count;
    private final T wrappedData;

    public SyncEventData(int count, T wrappedData) {
        this.count = count;
        this.wrappedData = wrappedData;
    }

    public int getCount() {
        return count;
    }

    public T getWrappedData() {
        return wrappedData;
    }
}
