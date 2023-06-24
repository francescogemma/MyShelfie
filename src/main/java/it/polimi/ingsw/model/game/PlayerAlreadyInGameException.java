package it.polimi.ingsw.model.game;

/**
 * Exception thrown when we try to add the same player to a game more than once.
 */
public class PlayerAlreadyInGameException extends Exception {
    /**
     * Constructor of the class.
     *
     * @param username is the username of the player that we tried to add more than once to a game.
     */
    public PlayerAlreadyInGameException(String username) {
        super(username + " is already in the game");
    }
}
