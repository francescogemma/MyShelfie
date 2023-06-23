package it.polimi.ingsw.view.displayable;

import it.polimi.ingsw.model.game.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a score board which can be displayed in the game layout.
 * It is an ordered list of {@link DisplayablePlayer}s according to the ordering defined by
 * {@link DisplayablePlayer#compare(DisplayablePlayer)}.
 *
 * @author Cristiano Migali
 */
public class DisplayableScoreBoard {
    /**
     * It is an ordered list of {@link DisplayablePlayer} according to the ordering defined by
     * {@link DisplayablePlayer#compare(DisplayablePlayer)}.
     */
    private final List<DisplayablePlayer> displayablePlayers = new ArrayList<>();

    /**
     * It is the username of the player whose machine the program is running on.
     */
    private final String clientPlayerName;

    /**
     * It is the original index (in lobby original ordering) of the user who is playing the current turn.
     */
    private int playingPlayerOriginalIndex;

    /**
     * Constructor of the class.
     *
     * @param clientPlayerName is the username of the player whose machine the program is running on.
     */
    public DisplayableScoreBoard(String clientPlayerName) {
        this.clientPlayerName = clientPlayerName;
    }

    /**
     * Converts the name of a player to its index in the score board.
     *
     * @param playerName is the name of the player for which we want to retrieve the correspondent index in the score board.
     * @return the index in the score board of the player identified by the provided username.
     *
     * @throws IllegalArgumentException if there is no player with the provided name.
     */
    private int playerNameToIndex(String playerName) {
        for (int i = 0; i < displayablePlayers.size(); i++) {
            if (displayablePlayers.get(i).getName().equals(playerName)) {
                return i;
            }
        }

        throw new IllegalArgumentException(playerName + " not found in the scoreboard");
    }

    /**
     * Converts the original index of a player (in lobby ordering) to the index of that same player in the score board.
     *
     * @param playerOriginalIndex is the original index (in lobby ordering) for which we want to retrieve the correspondent
     *                            index in the score board.
     * @return the index in the score board of the player identified by the provided original index.
     *
     * @throws IllegalArgumentException if there is no player with the provided original index.
     */
    private int playerOriginalIndexToIndex(int playerOriginalIndex) {
        for (int i = 0; i < displayablePlayers.size(); i++) {
            if (displayablePlayers.get(i).getOriginalIndex() == playerOriginalIndex) {
                return i;
            }
        }

        throw new IllegalArgumentException("Player " + playerOriginalIndex + " not found in the scoreboard");
    }

    /**
     * Adds a {@link DisplayablePlayer} to the score board, keeping it ordered.
     *
     * @param player is the {@link Player} with the name, points and connection state of the {@link DisplayablePlayer}
     *               that we want to add to the scoreboard.
     * @param playerOriginalIndex is the original index of the {@link DisplayablePlayer} in lobby ordering.
     */
    public void addDisplayablePlayer(Player player, int playerOriginalIndex) {
        displayablePlayers.add(new DisplayablePlayer(player, playerOriginalIndex,
            player.getUsername().equals(clientPlayerName)));

        recalculatePositions(player.getUsername());
    }

    /**
     * Reorders the score board assuming that every {@link DisplayablePlayer} is in the right place except
     * for the one with the provided name.
     *
     * @param modifiedPlayerName is the name of the {@link DisplayablePlayer} which has been modified and should
     *                           be replaced inside the score board.
     */
    private void recalculatePositions(String modifiedPlayerName) {
        int modifiedPlayerIndex = playerNameToIndex(modifiedPlayerName);

        if (modifiedPlayerIndex < displayablePlayers.size() - 1 &&
            displayablePlayers.get(modifiedPlayerIndex).getPosition() <
                displayablePlayers.get(modifiedPlayerIndex + 1).getPosition() &&
            (modifiedPlayerIndex == 0 || displayablePlayers.get(modifiedPlayerIndex - 1).getPosition()
                < displayablePlayers.get(modifiedPlayerIndex).getPosition())) {

            for (int i = modifiedPlayerIndex + 1; i < displayablePlayers.size(); i++) {
                displayablePlayers.get(i).setPosition(displayablePlayers.get(i).getPosition() - 1);
            }
        }

        DisplayablePlayer modifiedPlayer = displayablePlayers.remove(modifiedPlayerIndex);

        int previousPlayerPosition = 0;

        for (int i = 0; i < displayablePlayers.size(); i++) {
            if (modifiedPlayer.compare(displayablePlayers.get(i)) == 1) {
                previousPlayerPosition = displayablePlayers.get(i).getPosition();
            } else {
                modifiedPlayer.setPosition(previousPlayerPosition + 1);

                if (modifiedPlayer.compare(displayablePlayers.get(i)) == -1) {
                    for (int j = i; j < displayablePlayers.size(); j++) {
                        displayablePlayers.get(j).setPosition(displayablePlayers.get(j).getPosition() + 1);
                    }
                }

                displayablePlayers.add(i, modifiedPlayer);
                return;
            }
        }

        modifiedPlayer.setPosition(previousPlayerPosition + 1);
        displayablePlayers.add(modifiedPlayer);
    }

    /**
     * Adds additional points to the player that has recently scored them through goal completion.
     * The player is identified through its name.
     *
     * @param playerName is the name of the player which has recently scored points by completing a goal.
     * @param additionalPoints is the number of points that the player has scored by completing the goal.
     */
    public void addAdditionalPoints(String playerName, int additionalPoints) {
        displayablePlayers.get(playerNameToIndex(playerName)).setAdditionalPoints(additionalPoints);

        recalculatePositions(playerName);
    }

    /**
     * Sums the previous points of a player with the ones that has recently scored (additional points).
     *
     * @param playerName is the name of the player whose points have to be summed together.
     */
    public void sumPoints(String playerName) {
        displayablePlayers.get(playerNameToIndex(playerName)).sumPoints();
    }

    /**
     * Sets the specified player as blurred inside the score board.
     *
     * @param playerName is the name of the player that must be blurred.
     */
    public void blur(String playerName) {
        displayablePlayers.get(playerNameToIndex(playerName)).setIsBlurred(true);
    }

    /**
     * Sets the specified player as not blurred inside the score board.
     *
     * @param playerName is the name of the player must be unblurred.
     */
    public void unblur(String playerName) {
        displayablePlayers.get(playerNameToIndex(playerName)).setIsBlurred(false);
    }

    /**
     * Sets the connection state for the specified player.
     *
     * @param playerName is the name of the player for which we want to set the connection state.
     * @param isConnected is the connection state that will be set for the plater.
     */
    public void setConnectionState(String playerName, boolean isConnected) {
        displayablePlayers.get(playerNameToIndex(playerName)).setConnectionState(isConnected);

        recalculatePositions(playerName);
    }

    /**
     * Sets if the specified player is the winner or not.
     *
     * @param playerName is the name of player for which we want to set if it is the winner or not.
     * @param isWinner must be true iff the player identified through playerName is a winner.
     */
    public void setIsWinner(String playerName, boolean isWinner) {
        displayablePlayers.get(playerNameToIndex(playerName)).setIsWinner(isWinner);
    }

    /**
     * @return the ordered list of {@link DisplayablePlayer}s inside this score board.
     */
    public List<DisplayablePlayer> getDisplayablePlayers() {
        return new ArrayList<>(displayablePlayers.stream().map(DisplayablePlayer::new).toList());
    }

    /**
     * Sets the original index (in lobby ordering) of the player that is playing the current turn.
     *
     * @param playingPlayerOriginalIndex is the original index of the player that is playing the current turn.
     */
    public void setPlayingPlayerOriginalIndex(int playingPlayerOriginalIndex) {
        this.playingPlayerOriginalIndex = playingPlayerOriginalIndex;
    }

    /**
     * Sets the original index (in lobby ordering) of the player that is playing the current turn.
     *
     * @param playingPlayerName is the name of the player that is playing the current turn.
     */
    public void setPlayingPlayerOriginalIndex(String playingPlayerName) {
        playingPlayerOriginalIndex = displayablePlayers.get(playerNameToIndex(playingPlayerName))
            .getOriginalIndex();
    }

    /**
     * @return true iff the player who is playing the current turn is the one whose machine the program is running
     * on.
     */
    public boolean isClientPlaying() {
        return displayablePlayers.get(playerOriginalIndexToIndex(playingPlayerOriginalIndex))
            .isClientPlayer();
    }

    /**
     * @return the {@link DisplayablePlayer} associated with the user whose machine the program is running on.
     */
    public DisplayablePlayer getClientPlayer() {
        return new DisplayablePlayer(displayablePlayers.get(playerNameToIndex(clientPlayerName)));
    }

    /**
     * @return the {@link DisplayablePlayer} who is playing the current turn.
     */
    public DisplayablePlayer getPlayingPlayer() {
        return new DisplayablePlayer(displayablePlayers.get(playerOriginalIndexToIndex(playingPlayerOriginalIndex)));
    }

    /**
     * @param playerName is the name of the {@link DisplayablePlayer} that we want to retrieve.
     * @return the {@link DisplayablePlayer} in the score board with the specified name.
     *
     * @throws IllegalArgumentException if there is no {@link DisplayablePlayer} with the specified name in the
     * score board.
     */
    public DisplayablePlayer getDisplayablePlayer(String playerName) {
        return new DisplayablePlayer(displayablePlayers.get(playerNameToIndex(playerName)));
    }

    /**
     * @param playerOriginalIndex is the original index (in lobby ordering) of the {@link DisplayablePlayer} that we
     *                            want to retrieve.
     * @return the {@link DisplayablePlayer} in the score board with the specified original index.
     *
     * @throws IllegalArgumentException if there is no {@link DisplayablePlayer} with the specified original index
     * in the score board.
     */
    public DisplayablePlayer getDisplayablePlayer(int playerOriginalIndex) {
        return new DisplayablePlayer(displayablePlayers.get(playerOriginalIndexToIndex(playerOriginalIndex)));
    }
}
