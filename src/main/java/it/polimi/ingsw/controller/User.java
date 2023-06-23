package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.db.Identifiable;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * It is an {@link Identifiable} with an associated password. It is used to store and manage user accounts server side.
 * The password is not stored plainly but as a SHA256 hash.
 * This class provides utilities to check if a given couple of username and password matches the credentials of a
 * certain User.
 *
 * @author Cristiano Migali
 */
public class User implements Identifiable {
    /**
     * It is the username which uniquely identifies a User.
     */
    private final String name;

    /**
     * It is the SHA256 hash of the password associated with this User.
     */
    private final String hashedPassword;

    /**
     * It is true iff this User has an active connection to the server.
     */
    private transient boolean connected;

    /**
     * @param password is the password that will be hashed.
     * @return the SHA256 hash of the provided password.
     */
    private static String hashPassword(String password) {
        StringBuilder hashedPasswordBuilder = new StringBuilder(64);

        try {
            byte[] hashedPasswordBytes = MessageDigest.getInstance("SHA-256").digest(
                password.getBytes()
            );

            for (byte hashedPasswordByte : hashedPasswordBytes) {
                hashedPasswordBuilder.append(String.format("%02x", hashedPasswordByte));
            }
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Got: " + e.getMessage() + " while trying to hash user password");
        }

        return hashedPasswordBuilder.toString();
    }

    /**
     * Constructor of the class.
     * It sets the username and hash its password.
     *
     * @param name is the unique username of the constructed User.
     * @param password is the password associated with the constructed User.
     */
    public User(String name, String password) {
        this.name = name;
        this.connected = false;
        hashedPassword = hashPassword(password);
    }

    /**
     * @return true iff this User has an active connection to the server.
     */
    public boolean isConnected() {
        return this.connected;
    }

    /**
     * Allows to set the connection state of this User.
     *
     * @param connected must be true iff the User has an active connection to the server.
     */
    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    /**
     * Provides a case-insensitive comparison with a given username.
     *
     * @param username is the username which we want to compare to the one of this User.
     * @return true iff the username of this User matches the provided one, ignoring the case.
     */
    public boolean is(String username) {
        return name.equalsIgnoreCase(username);
    }

    /**
     * @return the username which uniquely identifies this User.
     */
    public String getUsername() {
        return this.name;
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * @param password is the password which we want to compare to the one of this User.
     * @return true iff the given password matches the one of the user.
     */
    public boolean passwordMatches(String password) {
        return hashPassword(password).equals(hashedPassword);
    }
}
