package it.polimi.ingsw.controller.db;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import it.polimi.ingsw.model.tile.Tile;
import it.polimi.ingsw.model.tile.TileColor;
import it.polimi.ingsw.model.tile.TileVersion;

import java.io.IOException;

/**
 * {@link TypeAdapter} used to serialize tiles to JSON preserving the singleton pattern.
 * In particular, every tile is serialized to a JSON with a color field and a version field.
 * During the deserialization the tile instance corresponding to the deserialized color and version is retrieved
 * through {@link Tile#getInstance(TileColor, TileVersion)} ensuring that there is only one {@link Tile} instance with
 * a certain color and version couple.
 *
 * @author Cristiano Migali1
 */
public class TileTypeAdapter extends TypeAdapter<Tile> {
    /**
     * It is the name of the color field in the tile JSON.
     */
    private static final String COLOR_FIELD_NAME = "color";

    /**
     * It is the name of the version field in the tile JSON.
     */
    private static final String VERSION_FIELD_NAME = "version";

    @Override
    public void write(JsonWriter jsonWriter, Tile tile) throws IOException {
        if (tile == null) {
            jsonWriter.nullValue();
            return;
        }

        jsonWriter.beginObject();

        jsonWriter.name(COLOR_FIELD_NAME);

        jsonWriter.value(tile.getColor().toString());

        jsonWriter.name(VERSION_FIELD_NAME);

        jsonWriter.value(tile.getVersion().toString());

        jsonWriter.endObject();
    }

    @Override
    public Tile read(JsonReader jsonReader) throws IOException {
        if (jsonReader.peek() == JsonToken.NULL) {
            jsonReader.nextNull();

            return null;
        }

        jsonReader.beginObject();

        String readColorFieldName = jsonReader.nextName();
        if (!readColorFieldName.equals(COLOR_FIELD_NAME)) {
            throw new IOException("The name for the color field in a serialized tile is " + COLOR_FIELD_NAME);
        }

        String colorName = jsonReader.nextString();

        TileColor color = null;

        for (TileColor c : TileColor.values()) {
            if (c.toString().equals(colorName)) {
                color = c;
                break;
            }
        }

        if (color == null) {
            throw new IOException("The specified color for the tile doesn't match any of the available colors");
        }

        String readVersionFieldName = jsonReader.nextName();
        if (!readVersionFieldName.equals(VERSION_FIELD_NAME)) {
            throw new IOException("The name for the version field in a serialized tile is " + VERSION_FIELD_NAME);
        }

        String versionName = jsonReader.nextString();

        TileVersion version = null;

        for (TileVersion v : TileVersion.values()) {
            if (v.toString().equals(versionName)) {
                version = v;
                break;
            }
        }

        if (version == null) {
            throw new IOException("The specified version for the tile isn't among the available versions");
        }

        jsonReader.endObject();

        return Tile.getInstance(color, version);
    }
}
