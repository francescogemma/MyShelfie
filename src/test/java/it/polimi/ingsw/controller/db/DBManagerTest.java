package it.polimi.ingsw.controller.db;

import it.polimi.ingsw.controller.User;
import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.model.game.Player;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

class DBManagerTest {
    private static void deleteDirectoryRecursively(Path directoryPath) {
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(directoryPath)) {
            directoryStream.forEach(path -> {
                if (path.toFile().isDirectory()) {
                    deleteDirectoryRecursively(path);
                } else {
                    Assertions.assertTrue(path.toFile().delete());
                }
            });
        } catch (IOException e) {
            throw new IllegalStateException("Got: " + e + " while clearing the testing db");
        }

        Assertions.assertTrue(directoryPath.toFile().delete());
    }

    private static final String TEST_ROOT_FOLDER_NAME = "testdb";

    @BeforeEach
    public void setUp() {
        DBManager.setRootFolderName(TEST_ROOT_FOLDER_NAME);
    }

    @AfterEach
    public void tearDown() {
        Path rootFolderPath = Paths.get(TEST_ROOT_FOLDER_NAME);
        if (rootFolderPath.toFile().exists()) {
            if (!rootFolderPath.toFile().isDirectory()) {
                throw new IllegalStateException("The root db folder isn't actually a directory");
            }

            deleteDirectoryRecursively(rootFolderPath);
        }
    }

    @Test
    @DisplayName("Save a user")
    void saveUser_newUser_userSavedOnDisk() {
        User user = new User("foo", "bar");

        DBManager.getUsersDBManager().save(user);

        Assertions.assertTrue(DBManager.getUsersDBManager().getIdentifiableFilePath("foo").toFile().exists());
    }

    @Test
    @DisplayName("Save and load a user")
    void saveUserAndLoadUser_newUser_sameUser() {
        User userToSave = new User("one", "two");

        DBManager.getUsersDBManager().save(userToSave);

        User loadedUser;
        try {
            loadedUser = DBManager.getUsersDBManager().load(userToSave.getName());
        } catch (IdentifiableNotFoundException e) {
            throw new IllegalStateException("The user just saved can't be loaded");
        }

        Assertions.assertEquals(userToSave.getName(), loadedUser.getName());
        Assertions.assertTrue(loadedUser.passwordMatches("two"));
    }

    @Test
    @DisplayName("Try to load not saved user, should throw exception")
    void loadUser_userNotSaved_throwsIdentifiableNotFoundException() {
        Assertions.assertThrows(IdentifiableNotFoundException.class, () -> {
            DBManager.getUsersDBManager().load("not_saved");
        });
    }

    @Test
    @DisplayName("Modify a saved user")
    void saveUserAndLoadUser_userAlreadySaved_userModified() {
        User newUser = new User("tizio", "caio");

        DBManager.getUsersDBManager().save(newUser);

        User userWithDifferentPassword = new User("tizio", "sempronio");

        DBManager.getUsersDBManager().save(userWithDifferentPassword);

        User loadedUser;
        try {
            loadedUser = DBManager.getUsersDBManager().load(userWithDifferentPassword.getName());
        } catch (IdentifiableNotFoundException e) {
            throw new IllegalStateException("The user just modified can't be loaded");
        }

        Assertions.assertTrue(loadedUser.passwordMatches("sempronio"));
    }

    @Test
    @DisplayName("Check if a name not in use is already in use")
    void nameAlreadyInUse_nameNotInUse_false() {
        Assertions.assertFalse(DBManager.getUsersDBManager().nameAlreadyInUse("name_not_in_use"));
    }

    @Test
    @DisplayName("Check if a name actually in use is already in use")
    void nameAlreadyInUse_nameInUse_true() {
        User user = new User("name_in_use", "password");

        DBManager.getUsersDBManager().save(user);

        Assertions.assertTrue(DBManager.getUsersDBManager().nameAlreadyInUse("name_in_use"));
    }

    @Test
    @DisplayName("Delete a saved user")
    void saveUserAndDeleteUser_userAlreadySaved_userDeleted() {
        User userToDelete = new User("delete_me", "password");

        DBManager.getUsersDBManager().save(userToDelete);

        DBManager.getUsersDBManager().delete(userToDelete);

        Assertions.assertFalse(DBManager.getUsersDBManager().getIdentifiableFilePath("delete_me")
            .toFile().exists());
    }

    @Test
    @DisplayName("Get the list of names in use")
    void getSavedIdentifiablesNames__correctOutput() {
        User firstUser = new User("first", "passwrd");
        User secondUser = new User("second", "password");
        User thirdUser = new User("third", "password");

        DBManager.getUsersDBManager().save(firstUser);
        DBManager.getUsersDBManager().save(secondUser);
        DBManager.getUsersDBManager().save(thirdUser);

        Assertions.assertEquals(Set.of("first", "second", "third"),
            DBManager.getUsersDBManager().getSavedIdentifiablesNames());
    }

    @Test
    @DisplayName("Get the list of names in use when the db is empty")
    void getSavedIdentifiablesNames_emptyDB_emptySet() {
        Assertions.assertEquals(Set.of(),
            DBManager.getGamesDBManager().getSavedIdentifiablesNames());
    }

    // TODO: Add tests for GamesDBManager
}
