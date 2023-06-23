package it.polimi.ingsw.controller.db;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import it.polimi.ingsw.model.goal.PersonalGoal;

import java.io.IOException;

/**
 * {@link TypeAdapter} used to serialize personal goals to JSON in an efficient way.
 * In particular, every personal goal is serialized as an integer which uniquely identifies it.
 *
 * @author Cristiano Migali
 */
public class PersonalGoalTypeAdapter extends TypeAdapter<PersonalGoal> {
    @Override
    public void write(JsonWriter jsonWriter, PersonalGoal personalGoal) throws IOException {
        jsonWriter.value(personalGoal.getIndex());
    }

    @Override
    public PersonalGoal read(JsonReader jsonReader) throws IOException {
        return PersonalGoal.fromIndex(jsonReader.nextInt());
    }
}
