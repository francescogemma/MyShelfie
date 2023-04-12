package it.polimi.ingsw.model.game;

public class PlayerAlreadyInGameException extends Exception {
    public PlayerAlreadyInGameException(String username) {
        super(username + " is already in the game");
    }
}
