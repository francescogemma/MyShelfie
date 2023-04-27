package it.polimi.ingsw.event;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import it.polimi.ingsw.controller.Response;
import it.polimi.ingsw.event.data.EventData;
import it.polimi.ingsw.event.data.LoginEventData;
import it.polimi.ingsw.event.data.client.*;
import it.polimi.ingsw.event.data.game.*;
import it.polimi.ingsw.event.data.wrapper.SyncEventDataWrapper;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

// https://www.javadoc.io/static/com.google.code.gson/gson/2.8.5/com/google/gson/TypeAdapterFactory.html
public class EventDataTypeAdapterFactory implements TypeAdapterFactory {
    private static final Map<String, Type> EVENT_DATA_TYPES = Map.ofEntries(
            Map.entry(LoginEventData.ID, LoginEventData.class),
            Map.entry(PlayerPointsChangeEventData.ID, PlayerPointsChangeEventData.class),
            Map.entry(BoardChangedEventData.ID, BoardChangedEventData.class),
            Map.entry(GameOverEventData.ID, GameOverEventData.class),
            Map.entry(CurrentPlayerChangedEventData.ID, CurrentPlayerChangedEventData.class),
            Map.entry(GameHasStartedEventData.ID, GameHasStartedEventData.class),
            Map.entry(PlayerHasDisconnectedEventData.ID, PlayerHasDisconnectedEventData.class),
            Map.entry(PlayerHasJoinEventData.ID, PlayerHasJoinEventData.class),
            Map.entry(Response.ID, Response.class),
            Map.entry(JoinGameEventData.ID, JoinGameEventData.class),
            Map.entry(GameIsNoLongerAvailableEventData.ID, GameIsNoLongerAvailableEventData.class),
            Map.entry(StartGameEventData.ID, StartGameEventData.class),
            Map.entry(CreateNewGameEventData.ID, CreateNewGameEventData.class),
            Map.entry(InsertTileEventData.ID, InsertTileEventData.class),
            Map.entry(SelectTileEventData.ID, SelectTileEventData.class),
            Map.entry(DeselectTileEventData.ID, DeselectTileEventData.class),
            Map.entry(GameHasBeenCreatedEventData.ID, GameHasBeenCreatedEventData.class),
            Map.entry(PlayerHasJoinMenu.ID, PlayerHasJoinMenu.class),
            Map.entry(InitialGameEventData.ID, InitialGameEventData.class),
            Map.entry(JoinStartedGameEventData.ID, JoinStartedGameEventData.class),
            Map.entry(PlayerHasDeselectTile.ID, PlayerHasDeselectTile.class),
            Map.entry(BookshelfHasChangedEventData.ID, BookshelfHasChangedEventData.class),
            Map.entry(PersonalGoalSetEventData.ID, PersonalGoalSetEventData.class),
            Map.entry(PlayerExitGame.ID, PlayerExitGame.class)
    );

    private static final Map<String, Type> WRAPPER_DATA_TYPES = Map.of(
        SyncEventDataWrapper.WRAPPER_ID, SyncEventDataWrapper.class
    );

    private static Type parseEventId(String eventId) {
        for (Map.Entry<String, Type> entry : WRAPPER_DATA_TYPES.entrySet()) {
            if (eventId.startsWith(entry.getKey() + "_")) {
                return TypeToken.getParameterized(entry.getValue(), parseEventId(
                    eventId.substring(entry.getKey().length() + 1)
                )).getType();
            }
        }

        return EVENT_DATA_TYPES.get(eventId);
    }

    private static final String EVENT_ID_FIELD_NAME = "eventId";
    private static final String DATA_FIELD_NAME = "data";

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
        if (typeToken.getType() != EventData.class) {
            return null;
        }

        return new TypeAdapter<T>() {
            @Override
            public T read(JsonReader jsonReader) throws IOException {
                if (jsonReader.peek() == JsonToken.NULL) {
                    jsonReader.nextNull();
                    return null;
                }

                jsonReader.beginObject();

                if (!jsonReader.nextName().equals(EVENT_ID_FIELD_NAME)) {
                    throw new IOException("Event JSON must start with " + EVENT_ID_FIELD_NAME);
                }

                String eventId = jsonReader.nextString();

                if (!jsonReader.nextName().equals(DATA_FIELD_NAME)) {
                    throw new IOException("Event JSON must contain " + DATA_FIELD_NAME + " field");
                }

                Object data = gson.getAdapter(TypeToken.get(parseEventId(eventId)))
                    .read(jsonReader);

                jsonReader.endObject();

                return (T) data;
            }

            @Override
            public void write(JsonWriter jsonWriter, T value) throws IOException {
                if (value == null) {
                    jsonWriter.nullValue();
                    return;
                }

                EventData data = (EventData) value;

                jsonWriter.beginObject();

                jsonWriter.name(EVENT_ID_FIELD_NAME).value(data.getId());

                jsonWriter.name(DATA_FIELD_NAME);

                ((TypeAdapter<EventData>) gson.getAdapter(TypeToken.get(parseEventId(data.getId()))))
                    .write(jsonWriter, data);

                jsonWriter.endObject();
            }
        };
    }
}
