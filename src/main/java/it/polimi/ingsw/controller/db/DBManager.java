package it.polimi.ingsw.controller.db;

import com.google.gson.Gson;
import it.polimi.ingsw.controller.User;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class DBManager<T extends Identifiable> {
    private static final Object lock = new Object();

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

    // Used also for testing purpose
    Path getIdentifiableFilePath(String name) {
        return Paths.get(ROOT_FOLDER_NAME, folderName, name + ".json");
    }

    private DBManager(String folderName, Type type) {
        this.folderName = folderName;
        this.type = type;

        gson = new Gson();

        try {
            Files.createDirectories(getFolderPath());
        } catch (IOException e) {
            throw new IllegalStateException("Got: " + e + " while trying to create a db folder");
        }
    }

    public T load(String name) throws IdentifiableNotFoundException {
        synchronized (lock) {
            File identifiableFile = getIdentifiableFilePath(name).toFile();

            if (!identifiableFile.exists()) {
                throw new IdentifiableNotFoundException(name, folderName.substring(0, folderName.length() - 1));
            }

            if (identifiableFile.isDirectory()) {
                throw new IllegalStateException("An identifiable file is actually a directory");
            }

            String identifiableJSON;
            try (Scanner identifiableFileScanner = new Scanner(identifiableFile).useDelimiter("\n")) {
                identifiableJSON = identifiableFileScanner.next();
            } catch (FileNotFoundException e) {
                throw new IllegalStateException("Identifiable file not found: " + e);
            }

            return (T) gson.fromJson(identifiableJSON, type);
        }
    }

    public void save(T identifiable) {
        synchronized (lock) {
            try (FileWriter writer = new FileWriter(getIdentifiableFilePath(identifiable.getName()).toFile())) {
                writer.write(gson.toJson(identifiable) + "\n");
            } catch (IOException e) {
                throw new IllegalStateException("Got: " + e + " while trying to save identifiable to disk");
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

    // We can't directly instantiate this object since, for testing purpose, ROOT_FOLDER_NAME must be set before.
    private static DBManager<User> USERS_DB_MANAGER_INSTANCE = null;

    public static DBManager<User> getUsersDBManager() {
        synchronized (lock) {
            if (USERS_DB_MANAGER_INSTANCE == null) {
                USERS_DB_MANAGER_INSTANCE = new DBManager<>("users", User.class);
            }

            return USERS_DB_MANAGER_INSTANCE;
        }
    }
}
