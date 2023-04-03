package it.polimi.ingsw.event;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class Main {
    private final static Map<String, String> credentials = Map.of("foo", "bar");

    public static void main(String[] args) {
        LocalEventTransceiver transceiver = new LocalEventTransceiver();

        AuthenticatedEventReceiver<MessageEventData> authReceiver = new AuthenticatedEventReceiver<>(transceiver);

        authReceiver.registerListener("AUTHORIZED_MESSAGE", authMessageEventData -> {
            System.out.println(authMessageEventData.getUsername());
            System.out.println(authMessageEventData.getPassword());
            System.out.println(authMessageEventData.getWrappedData().getMessage());
        });

        AuthenticatedEventTransmitter<MessageEventData> authTransmitter = new AuthenticatedEventTransmitter<>("foo",
            "bar", transceiver);

        authTransmitter.broadcast(new Event<>("MESSAGE", new MessageEventData("Hello")));

        CastEventReceiver<MessageEventData> messageReceiver = new CastEventReceiver<>(transceiver);

        messageReceiver.registerListener("MESSAGE", messageEventData -> {
            System.out.println(messageEventData.getMessage());
        });

        CastEventTransmitter<MessageEventData> messageTransmitter = new CastEventTransmitter<>(transceiver);

        messageTransmitter.broadcast(new Event<MessageEventData>("MESSAGE", new MessageEventData("world")));

        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapterFactory(new EventTypeAdapterFactory());
        Gson gson = builder.create();


        SyncEventData<AuthenticatedEventData<MessageEventData>> authEventData = new SyncEventData<>(314, new AuthenticatedEventData<>("m", "n", new MessageEventData("ciao")));
        Event<Object> event = new Event<>("SYNCHRONIZED_AUTHORIZED_MESSAGE", authEventData);

        String json = gson.toJson(event, new TypeToken<Event<Object>>(){ }.getType());
        System.out.println(json);

        Event<Object> deserEvent = gson.fromJson(json, new TypeToken<Event<Object>>(){ }.getType());
        SyncEventData<AuthenticatedEventData<MessageEventData>> deserAuthEventData = (SyncEventData<AuthenticatedEventData<MessageEventData>>) deserEvent.getData();
        Event<SyncEventData<AuthenticatedEventData<MessageEventData>>> castedDeserEvent = new Event<>(deserEvent.getId(), deserAuthEventData);

        System.out.println(castedDeserEvent.getId());
        System.out.println(castedDeserEvent.getData().getCount());
        System.out.println(castedDeserEvent.getData().getWrappedData().getUsername());
        System.out.println(castedDeserEvent.getData().getWrappedData().getPassword());
        System.out.println(castedDeserEvent.getData().getWrappedData().getWrappedData().getMessage());
    }
}
