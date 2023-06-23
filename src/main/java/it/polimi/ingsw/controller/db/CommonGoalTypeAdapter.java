package it.polimi.ingsw.controller.db;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import it.polimi.ingsw.model.goal.CommonGoal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link TypeAdapter} used to serialize common goals to JSON in an efficient way.
 * In particular, every common goal is serialized as an integer which indicates the type of the common
 * goal and a list which corresponds to its current points stack.
 *
 * @author Cristiano Migali
 */
public class CommonGoalTypeAdapter extends TypeAdapter<CommonGoal> {
    @Override
    public void write(JsonWriter jsonWriter, CommonGoal commonGoal) throws IOException {
        jsonWriter.beginObject();

        jsonWriter.name("index").value(commonGoal.getIndex());

        jsonWriter.name("pointStack");

        jsonWriter.beginArray();

        for (int point : commonGoal.getPointStack()) {
            jsonWriter.value(point);
        }

        jsonWriter.endArray();

        jsonWriter.endObject();
    }

    @Override
    public CommonGoal read(JsonReader jsonReader) throws IOException {
        jsonReader.beginObject();

        String indexFieldName = jsonReader.nextName();
        if (!indexFieldName.equals("index")) {
            throw new IOException("Expected common goal index field, got: " + indexFieldName);
        }

        int index = jsonReader.nextInt();

        String pointStackFieldName = jsonReader.nextName();
        if (!pointStackFieldName.equals("pointStack")) {
            throw new IOException("Expected common goal point stack field , got: " + pointStackFieldName);
        }

        List<Integer> pointStack = new ArrayList<>();

        jsonReader.beginArray();

        while (jsonReader.peek() == JsonToken.NUMBER) {
            pointStack.add(jsonReader.nextInt());
        }

        jsonReader.endArray();

        jsonReader.endObject();

        CommonGoal commonGoal = CommonGoal.fromIndex(index);

        commonGoal.setPointStack(pointStack);

        return commonGoal;
    }
}
