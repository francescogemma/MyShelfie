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
import it.polimi.ingsw.event.data.VoidEventData;
import it.polimi.ingsw.event.data.client.LoginEventData;
import it.polimi.ingsw.event.data.client.*;
import it.polimi.ingsw.event.data.game.*;
import it.polimi.ingsw.event.data.internal.GameHasBeenStoppedInternalEventData;
import it.polimi.ingsw.event.data.internal.GameOverInternalEventData;
import it.polimi.ingsw.event.data.internal.PlayerDisconnectedInternalEventData;
import it.polimi.ingsw.event.data.wrapper.SyncEventDataWrapper;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * Is a {@link TypeAdapterFactory} defined following
 * <a href="https://www.javadoc.io/static/com.google.code.gson/gson/2.8.5/com/google/gson/TypeAdapterFactory.html">GSON documentation</a>
 * which allows for serialization and deserialization of every concrete type of {@link EventData}.
 *
 * @author Cristiano Migali
 */
public class EventDataTypeAdapterFactory implements TypeAdapterFactory {
    /**
     * Maps a primitive {@link EventData} identifier with its concrete type. It is used to retrieve
     * the correct {@link TypeAdapter} during deserialization, given the event identifier in the event JSON.
     */
    private static final Map<String, Type> EVENT_DATA_TYPES = Map.ofEntries(
            Map.entry(LoginEventData.ID, LoginEventData.class),
            Map.entry(CommonGoalCompletedEventData.ID, CommonGoalCompletedEventData.class),
            Map.entry(BoardChangedEventData.ID, BoardChangedEventData.class),
            Map.entry(GameOverEventData.ID, GameOverEventData.class),
            Map.entry(CurrentPlayerChangedEventData.ID, CurrentPlayerChangedEventData.class),
            Map.entry(GameHasStartedEventData.ID, GameHasStartedEventData.class),
            Map.entry(PlayerHasDisconnectedEventData.ID, PlayerHasDisconnectedEventData.class),
            Map.entry(PlayerHasJoinGameEventData.ID, PlayerHasJoinGameEventData.class),
            Map.entry(JoinLobbyEventData.ID, JoinLobbyEventData.class),
            Map.entry(GameIsNoLongerAvailableEventData.ID, GameIsNoLongerAvailableEventData.class),
            Map.entry(StartGameEventData.ID, StartGameEventData.class),
            Map.entry(CreateNewGameEventData.ID, CreateNewGameEventData.class),
            Map.entry(InsertTileEventData.ID, InsertTileEventData.class),
            Map.entry(SelectTileEventData.ID, SelectTileEventData.class),
            Map.entry(DeselectTileEventData.ID, DeselectTileEventData.class),
            Map.entry(GameHasBeenCreatedEventData.ID, GameHasBeenCreatedEventData.class),
            Map.entry(PlayerHasJoinMenu.ID, PlayerHasJoinMenu.class),
            Map.entry(InitialGameEventData.ID, InitialGameEventData.class),
            Map.entry(JoinGameEventData.ID, JoinGameEventData.class),
            Map.entry(PlayerHasDeselectTile.ID, PlayerHasDeselectTile.class),
            Map.entry(BookshelfHasChangedEventData.ID, BookshelfHasChangedEventData.class),
            Map.entry(PersonalGoalSetEventData.ID, PersonalGoalSetEventData.class),
            Map.entry(PlayerExitGame.ID, PlayerExitGame.class),
            Map.entry(PauseGameEventData.ID, PauseGameEventData.class),
            Map.entry(GameHasBeenStoppedEventData.ID, GameHasBeenStoppedEventData.class),
            Map.entry(PlayerDisconnectedInternalEventData.ID, PlayerDisconnectedInternalEventData.class),
            Map.entry(FirstFullBookshelfEventData.ID, FirstFullBookshelfEventData.class),
            Map.entry(LogoutEventData.ID, LogoutEventData.class),
            Map.entry(GameHasBeenPauseEventData.ID, GameHasBeenPauseEventData.class),
            Map.entry(GameHasResumeEventData.ID, GameHasResumeEventData.class),
            Map.entry(ExitLobbyEventData.ID, ExitLobbyEventData.class),
            Map.entry(PlayerHasExitLobbyEventData.ID, PlayerHasExitLobbyEventData.class),
            Map.entry(PlayerHasJoinLobbyEventData.ID, PlayerHasJoinLobbyEventData.class),
            Map.entry(RestartGameEventData.ID, RestartGameEventData.class),
            Map.entry(GameOverInternalEventData.ID, GameOverInternalEventData.class),
            Map.entry(GameHasBeenStoppedInternalEventData.ID, GameHasBeenStoppedInternalEventData.class),
            Map.entry(VoidEventData.ID, VoidEventData.class),
            Map.entry(UsernameEventData.ID, UsernameEventData.class)
    );

    /**
     * Maps a {@link it.polimi.ingsw.event.data.wrapper.EventDataWrapper} wrapper identifier to the
     * wrapper concrete type. It is used in {@link EventDataTypeAdapterFactory#parseEventId(String)} to
     * retrieve the correct {@link TypeAdapter} during deserialization, given the whole wrapped event identifier
     * in the event JSON.
     */
    private static final Map<String, Type> WRAPPER_DATA_TYPES = Map.of(
        SyncEventDataWrapper.WRAPPER_ID, SyncEventDataWrapper.class,
        Response.WRAPPER_ID, Response.class
    );

    /**
     * Retrieves the concrete type of an {@link EventData} (even if it there is recursive wrapping of events
     * through {@link it.polimi.ingsw.event.data.wrapper.EventDataWrapper} given its identifier.
     * If the event is a wrapped event, the type is a {@link java.lang.reflect.ParameterizedType}. To handle
     * parameterized types despite type erasure we exploit utilities provided in the GSON library.
     *
     * @param eventId is the identifier of the {@link EventData} for which we want to retrieve the concrete type.
     *
     * @return the concrete type matching the given event identifier. In general this type is a
     * {@link java.lang.reflect.ParameterizedType} which allows to handle the case of events obtained by wrapping
     * other events.
     */
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

    /**
     * The name of the field corresponding to the {@link EventData} identifier in the serialized JSON.
     */
    private static final String EVENT_ID_FIELD_NAME = "eventId";

    /**
     * The name of the field corresponding to the {@link EventData} actual content in the serialized JSON.
     */
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

                // We exploit default GSON type adapter for the obtained concrete type of the event.
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
