package it.polimi.ingsw.model.game;

/**
 * Exception thrown when we try to perform an action on a player by providing a username that doesn't match
 * any username of the players in game.
 *
 * @author Cristiano Migali
 */
public class PlayerNotInGameException extends Exception {
    /**
     * Constructor of the class.
     *
     * @param username is the username with no match among the usernames of the players in game.
     * @param gameName is the name of the game.
     */
    public PlayerNotInGameException(String username, String gameName) {
        super(username + " is not in " + gameName);
    }
}
