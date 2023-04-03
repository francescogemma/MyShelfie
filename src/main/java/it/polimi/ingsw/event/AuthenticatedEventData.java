package it.polimi.ingsw.event;

public class AuthenticatedEventData<T> {
    private final String username;
    private final String password;
    private final T wrappedData;

    public AuthenticatedEventData(String username, String password, T dataToWrap) {
        this.username = username;
        this.password = password;
        this.wrappedData = dataToWrap;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public T getWrappedData() {
        return wrappedData;
    }
}
