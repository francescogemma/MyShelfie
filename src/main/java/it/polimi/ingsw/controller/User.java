package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.db.Identifiable;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class User implements Identifiable {
    private final String name;
    private final String hashedPassword;

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

    public User(String name, String password) {
        this.name = name;
        hashedPassword = hashPassword(password);
    }

    @Override
    public String getName() {
        return name;
    }

    public boolean passwordMatches(String password) {
        return hashPassword(password).equals(hashedPassword);
    }
}
