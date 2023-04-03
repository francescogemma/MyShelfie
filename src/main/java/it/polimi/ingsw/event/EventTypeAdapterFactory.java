package it.polimi.ingsw.event;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

// https://www.javadoc.io/static/com.google.code.gson/gson/2.8.5/com/google/gson/TypeAdapterFactory.html
public class EventTypeAdapterFactory implements TypeAdapterFactory {
    private static final Map<String, Type> EVENT_DATA_TYPES = Map.of(
        "MESSAGE", MessageEventData.class
    );

    private static Type parseEventId(String eventId) {
        if (eventId.startsWith("AUTHORIZED_")) {
            return TypeToken.getParameterized(AuthenticatedEventData.class, parseEventId(
                eventId.substring("AUTHORIZED_".length())
            )).getType();
        }

        if (eventId.startsWith("SYNCHRONIZED_")) {
            return TypeToken.getParameterized(SyncEventData.class, parseEventId(
                eventId.substring("SYNCHRONIZED_".length())
            )).getType();
        }

        return EVENT_DATA_TYPES.get(eventId);
    }

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
        Type type = typeToken.getType();
        if (typeToken.getRawType() != Event.class || !(type instanceof ParameterizedType)
            || !((ParameterizedType) type).getActualTypeArguments()[0].equals(Object.class)) {
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

                jsonReader.nextName();
                String eventId = jsonReader.nextString();
                jsonReader.nextName();
                Object data = gson.getAdapter(TypeToken.get(parseEventId(eventId)))
                    .read(jsonReader);

                jsonReader.endObject();

                return (T) new Event<Object>(eventId, data);
            }

            @Override
            public void write(JsonWriter jsonWriter, T value) throws IOException {
                if (value == null) {
                    jsonWriter.nullValue();
                    return;
                }

                Event<Object> event = (Event<Object>) value;
                TypeAdapter<Object> dataAdapter = (TypeAdapter<Object>) gson.getAdapter(TypeToken.get(parseEventId(event.getId())));

                jsonWriter.beginObject();

                jsonWriter.name("eventId").value(event.getId());

                jsonWriter.name("data");

                dataAdapter.write(jsonWriter, event.getData());

                jsonWriter.endObject();
            }
        };
    }
}