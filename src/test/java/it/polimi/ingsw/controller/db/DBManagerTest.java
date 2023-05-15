package it.polimi.ingsw.controller.db;

import it.polimi.ingsw.controller.servercontroller.GameController;
import it.polimi.ingsw.controller.servercontroller.MenuController;
import it.polimi.ingsw.controller.User;
import it.polimi.ingsw.event.transmitter.EventTransmitter;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Execution(ExecutionMode.SAME_THREAD)
public class DBManagerTest {
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

    public static void setPositionDebug () {
        DBManager.setRootFolderName(TEST_ROOT_FOLDER_NAME);
    }

    @BeforeEach
    public void setUp() {
        setPositionDebug();
        removeCache();
    }

    public static void removeCache () {
        Path rootFolderPath = Paths.get(TEST_ROOT_FOLDER_NAME);
        if (rootFolderPath.toFile().exists()) {
            if (!rootFolderPath.toFile().isDirectory()) {
                throw new IllegalStateException("The root db folder isn't actually a directory");
            }

            deleteDirectoryRecursively(rootFolderPath);
        }
    }

    @AfterEach
    public void tearDown() {
        removeCache();
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

    // TODO: Add tests for GamesDBManager

    public static class MenuControllerTest {

        @BeforeEach
        void t () {
            setPositionDebug();
        }

        private String getPassword(String username) {
            if (username.equals("Giacomo")) return "ciao";
            if (username.equals("Michele")) return "foo";
            if (username.equals("Cristiano")) return "paperino";
            return null;
        }

        public static void setUp () throws IllegalAccessException, NoSuchFieldException {
            Field instanceGameController = MenuController.class.getDeclaredField("gameControllerList");
            Field instanceAuthenticated = MenuController.class.getDeclaredField("authenticated");
            Field instanceUsers = MenuController.class.getDeclaredField("users");

            instanceGameController.setAccessible(true);
            instanceAuthenticated.setAccessible(true);
            instanceUsers.setAccessible(true);

            instanceGameController.set(MenuController.getInstance(), new ArrayList<GameController>());
            instanceAuthenticated.set(MenuController.getInstance(), new ArrayList<EventTransmitter>());
            instanceUsers.set(MenuController.getInstance(), new HashSet<User>());
        }

        /* @Test
        void stopGame() throws NoSuchFieldException, IllegalAccessException {
            setUp();
            VirtualView view1 = new VirtualView(new LocalEventTransceiver());
            VirtualView view2 = new VirtualView(new LocalEventTransceiver());

            MenuController.getInstance().joinMenu(view1);
            MenuController.getInstance().joinMenu(view2);

            MenuController.getInstance().authenticated(view1, "Giacomo", getPassword("Giacomo"));
            MenuController.getInstance().authenticated(view2, "Michele", getPassword("Michele"));

            MenuController.getInstance().createNewGame("Prova", "Giacomo");

            MenuController.getInstance().joinGame(view1, "Prova", "Giacomo");
            MenuController.getInstance().joinGame(view2, "Prova", "Michele");
        } */

        @AfterEach
        void t2 () {
            removeCache();
        }
    }
}
