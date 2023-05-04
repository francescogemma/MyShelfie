package it.polimi.ingsw.model.game;

public class PlayerNotInGameException extends Exception {
    public PlayerNotInGameException(String username, String gameName) {
        super(username + " is not in " + gameName);
    }
}
