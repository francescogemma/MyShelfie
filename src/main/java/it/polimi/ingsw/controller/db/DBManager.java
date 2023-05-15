package it.polimi.ingsw.controller.db;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.polimi.ingsw.controller.User;
import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.model.goal.CommonGoal;
import it.polimi.ingsw.model.goal.PersonalGoal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class DBManager<T extends Identifiable> {
    private static String ROOT_FOLDER_NAME = "db";

    private final String folderName;
    private final Type type;

    private final Gson gson;

    // Used only for testing purpose
    static void setRootFolderName(String rootFolderName) {
        ROOT_FOLDER_NAME = rootFolderName;
    }

    private Path getFolderPath() {
        return Paths.get(ROOT_FOLDER_NAME, folderName);
    }

    private static final String JSON_EXTENSION = ".json";

    // Used also for testing purpose
    Path getIdentifiableFilePath(String name) {
        return Paths.get(ROOT_FOLDER_NAME, folderName, name.toLowerCase() + JSON_EXTENSION);
    }

    private DBManager(String folderName, Type type) {
        this.folderName = folderName;
        this.type = type;

        gson = new GsonBuilder().setPrettyPrinting()
            .registerTypeAdapter(PersonalGoal.class, new PersonalGoalTypeAdapter())
            .registerTypeAdapter(CommonGoal.class, new CommonGoalTypeAdapter())
            .create();
    }

    private final Object lock = new Object();

    public T load(String name) throws IdentifiableNotFoundException {
        synchronized (lock) {
            File identifiableFile = getIdentifiableFilePath(name).toFile();

            if (!identifiableFile.exists()) {
                throw new IdentifiableNotFoundException(name, folderName.substring(0, folderName.length() - 1));
            }

            if (identifiableFile.isDirectory()) {
                throw new IllegalStateException("An identifiable file is actually a directory");
            }

            StringBuilder identifiableJSON = new StringBuilder();
            try (Scanner identifiableFileScanner = new Scanner(identifiableFile)) {
                while (identifiableFileScanner.hasNextLine()) {
                    identifiableJSON.append(identifiableFileScanner.nextLine());
                }
            } catch (FileNotFoundException e) {
                throw new IllegalStateException("Got: " + e + " while trying to read " + name + "'s data in the "
                    + folderName + " db");
            }

            return (T) gson.fromJson(identifiableJSON.toString(), type);
        }
    }

    public void save(T identifiable) {
        synchronized (lock) {
            try {
                Files.createDirectories(getFolderPath());
            } catch (IOException e) {
                throw new IllegalStateException("Got: " + e + " while trying to create a db folder for " + folderName);
            }

            try (FileWriter writer = new FileWriter(getIdentifiableFilePath(identifiable.getName()).toFile())) {
                writer.write(gson.toJson(identifiable));
            } catch (IOException e) {
                throw new IllegalStateException("Got: " + e + " while trying to save " + identifiable.getName()
                    + "'s data in the " + folderName + " db");
            }
        }
    }

    public void delete(T identifiable) {
        synchronized (lock) {
            getIdentifiableFilePath(identifiable.getName()).toFile().delete();
        }
    }

    public boolean nameAlreadyInUse(String name) {
        synchronized (lock) {
            return getIdentifiableFilePath(name).toFile().exists();
        }
    }

    private Set<String> getSavedIdentifiablesNames() {
        synchronized (lock) {
            return Optional.ofNullable(getFolderPath().toFile().list()).map(
                    fileNames -> Arrays.stream(fileNames).map(fileName ->
                            fileName.substring(0, fileName.length() - JSON_EXTENSION.length()))
                            .collect(Collectors.toSet())
                ).orElse(Set.of());
        }
    }

    public List<T> loadAllInFolder() throws IdentifiableNotFoundException {
        Set<String> positions = this.getSavedIdentifiablesNames();
        List<T> res = new ArrayList<>();

        for (String position: positions) {
            res.add(this.load(position));
        }

        return res;
    }

    private static final DBManager<User> USERS_DB_MANAGER_INSTANCE = new DBManager<>("users", User.class);

    public static DBManager<User> getUsersDBManager() {
        return USERS_DB_MANAGER_INSTANCE;
    }

    private static final DBManager<Game> GAMES_DB_MANAGER_INSTANCE = new DBManager<>("games", Game.class);

    public static DBManager<Game> getGamesDBManager() {
        return GAMES_DB_MANAGER_INSTANCE;
    }
}
