package it.polimi.ingsw.controller.db;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import it.polimi.ingsw.model.goal.PersonalGoal;

import java.io.IOException;

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
