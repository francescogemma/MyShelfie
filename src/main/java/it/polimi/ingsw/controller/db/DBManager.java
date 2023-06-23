package it.polimi.ingsw.controller.db;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.polimi.ingsw.controller.User;
import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.model.goal.CommonGoal;
import it.polimi.ingsw.model.goal.PersonalGoal;
import it.polimi.ingsw.model.tile.Tile;

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

/**
 * Allows to save {@link Identifiable}s of a specific type to disk and retrieve them back.
 * They are saved as JSON files in a specified folder
 *
 * @param <T> is the type of the {@link Identifiable}.
 *
 * @author Cristiano Migali
 */
public class DBManager<T extends Identifiable> {
    /**
     * Name of the root of the DB folder.
     */
    private static String ROOT_FOLDER_NAME = "db";

    /**
     * Name of the folder inside the root one where {@link Identifiable}s are stored.
     */
    private final String folderName;

    /**
     * It is the {@link Type} of the {@link Identifiable}s that this DBManager can save and retrieve.
     */
    private final Type type;

    /**
     * {@link Gson} object used for serialization and deserialization.
     */
    private final Gson gson;

    /**
     * Allows to change the name of the root folder of the DB.
     * It is used for testing purpose, to prevent override of save {@link Identifiable}s in the actual db.
     *
     * @param rootFolderName is the name to be set for the root folder of the DB.
     */
    static void setRootFolderName(String rootFolderName) {
        ROOT_FOLDER_NAME = rootFolderName;
    }

    /**
     * @return the path to the folder where the {@link Identifiable}s saved and retrieved by this DBManager
     * are stored.
     */
    private Path getFolderPath() {
        return Paths.get(ROOT_FOLDER_NAME, folderName);
    }

    /**
     * It is the extension to be added to the saved {@link Identifiable}s files.
     */
    private static final String JSON_EXTENSION = ".json";

    /**
     * @param name is the name of the {@link Identifiable} for which we want to retrieve the corresponding file path.
     * @return the {@link Path} to the file where the {@link Identifiable} with the given name has been saved.
     */
    Path getIdentifiableFilePath(String name) {
        return Paths.get(ROOT_FOLDER_NAME, folderName, name.toLowerCase() + JSON_EXTENSION);
    }

    /**
     * Constructor of the class.
     *
     * @param folderName is the name of the folder inside the root one where the {@link Identifiable}s managed
     *                   by this DBManager will be stored.
     * @param type is the {@link Type} of the {@link Identifiable}s managed by this DBManager.
     */
    private DBManager(String folderName, Type type) {
        this.folderName = folderName;
        this.type = type;

        gson = new GsonBuilder().setPrettyPrinting()
            .registerTypeAdapter(PersonalGoal.class, new PersonalGoalTypeAdapter())
            .registerTypeAdapter(CommonGoal.class, new CommonGoalTypeAdapter())
            .registerTypeAdapter(Tile.class, new TileTypeAdapter())
            .create();
    }

    /**
     * Lock object used to synchronize the save, load and delete operations of {@link Identifiable}s managed
     * by this DBManager.
     */
    private final Object lock = new Object();

    /**
     * Loads the {@link Identifiable} with the given name from disk.
     *
     * @param name is the name of the {@link Identifiable} that we want to retrieve.
     * @return the {@link Identifiable} with the provided name, loaded from disk.
     * @throws IdentifiableNotFoundException if there is no {@link Identifiable} with the given name saved on the disk.
     */
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

    /**
     * Saves the provided {@link Identifiable} to disk.
     *
     * @param identifiable is the {@link Identifiable} to be saved on disk.
     */
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

    /**
     * Deletes the provided {@link Identifiable} from disk.
     *
     * @param identifiable is the {@link Identifiable} to be deleted from disk-
     */
    public void delete(T identifiable) {
        synchronized (lock) {
            getIdentifiableFilePath(identifiable.getName()).toFile().delete();
        }
    }

    /**
     * @param name is the name that we want to check if it is in use or not.
     * @return true iff there is an {@link Identifiable} with the provided name, managed by this DBManager,
     * which is saved on disk.
     */
    public boolean nameAlreadyInUse(String name) {
        synchronized (lock) {
            return getIdentifiableFilePath(name).toFile().exists();
        }
    }

    /**
     * @return the set of all the names of {@link Identifiable}s saved on disk, managed by this DBMangaer.
     */
    private Set<String> getSavedIdentifiablesNames() {
        synchronized (lock) {
            return Optional.ofNullable(getFolderPath().toFile().list()).map(
                    fileNames -> Arrays.stream(fileNames).map(fileName ->
                            fileName.substring(0, fileName.length() - JSON_EXTENSION.length()))
                            .collect(Collectors.toSet())
                ).orElse(Set.of());
        }
    }

    /**
     * @return a list with all the {@link Identifiable}s managed by this DBManager which have been saved on disk.
     *
     * @author Giacomo Groppi
     */
    public List<T> loadAllInFolder() {
        synchronized (lock) {
            Set<String> positions = this.getSavedIdentifiablesNames();
            List<T> res = new ArrayList<>();

            for (String position : positions) {
                try {
                    res.add(this.load(position));
                } catch (IdentifiableNotFoundException e) {
                    throw new IllegalStateException("An identifiable whose name was saved can't be loaded");
                }
            }

            return res;
        }
    }

    /**
     * It is the instance of the DBManager which manages {@link User}s, used to implement a singleton pattern.
     */
    private static final DBManager<User> USERS_DB_MANAGER_INSTANCE = new DBManager<>("users", User.class);

    /**
     * @return the instance of the DBManager which manages {@link User}s. This is the only way of doing so.
     */
    public static DBManager<User> getUsersDBManager() {
        return USERS_DB_MANAGER_INSTANCE;
    }

    /**
     * It is the instance of the DBManager which manages {@link Game}s, used to implement a singleton pattern.
     */
    private static final DBManager<Game> GAMES_DB_MANAGER_INSTANCE = new DBManager<>("games", Game.class);

    /**
     * @return the instance of the DBManager which manages {@link Game}s. This is the only way of doing so.
     */
    public static DBManager<Game> getGamesDBManager() {
        return GAMES_DB_MANAGER_INSTANCE;
    }
}
