package it.polimi.ingsw.view.displayable;

import it.polimi.ingsw.model.game.Player;

import java.util.ArrayList;
import java.util.List;

public class DisplayableScoreBoard {
    private final List<DisplayablePlayer> displayablePlayers = new ArrayList<>();

    private final String clientPlayerName;

    private int playingPlayerOriginalIndex;

    public DisplayableScoreBoard(String clientPlayerName) {
        this.clientPlayerName = clientPlayerName;
    }

    private int playerNameToIndex(String playerName) {
        for (int i = 0; i < displayablePlayers.size(); i++) {
            if (displayablePlayers.get(i).getName().equals(playerName)) {
                return i;
            }
        }

        throw new IllegalArgumentException(playerName + " not found in the scoreboard");
    }

    private int playerOriginalIndexToIndex(int playerOriginalIndex) {
        for (int i = 0; i < displayablePlayers.size(); i++) {
            if (displayablePlayers.get(i).getOriginalIndex() == playerOriginalIndex) {
                return i;
            }
        }

        throw new IllegalArgumentException("Player " + playerOriginalIndex + " not found in the scoreboard");
    }

    public void addDisplayablePlayer(Player player, int playerOriginalIndex) {
        displayablePlayers.add(new DisplayablePlayer(player, playerOriginalIndex,
            player.getUsername().equals(clientPlayerName)));

        recalculatePositions(player.getUsername());
    }

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

    public void addAdditionalPoints(String playerName, int additionalPoints) {
        displayablePlayers.get(playerNameToIndex(playerName)).setAdditionalPoints(additionalPoints);

        recalculatePositions(playerName);
    }

    public void sumPoints(String playerName) {
        displayablePlayers.get(playerNameToIndex(playerName)).sumPoints();
    }

    public void blur(String playerName) {
        displayablePlayers.get(playerNameToIndex(playerName)).setIsBlurred(true);
    }

    public void unblur(String playerName) {
        displayablePlayers.get(playerNameToIndex(playerName)).setIsBlurred(false);
    }

    public void setConnectionState(String playerName, boolean isConnected) {
        displayablePlayers.get(playerNameToIndex(playerName)).setConnectionState(isConnected);

        recalculatePositions(playerName);
    }

    public void setIsWinner(String playerName, boolean isWinner) {
        displayablePlayers.get(playerNameToIndex(playerName)).setIsWinner(isWinner);
    }

    public List<DisplayablePlayer> getDisplayablePlayers() {
        return new ArrayList<>(displayablePlayers.stream().map(DisplayablePlayer::new).toList());
    }

    public void setPlayingPlayerOriginalIndex(int playingPlayerOriginalIndex) {
        this.playingPlayerOriginalIndex = playingPlayerOriginalIndex;
    }

    public void setPlayingPlayerOriginalIndex(String playingPlayerName) {
        playingPlayerOriginalIndex = displayablePlayers.get(playerNameToIndex(playingPlayerName))
            .getOriginalIndex();
    }

    public boolean isClientPlaying() {
        return displayablePlayers.get(playerOriginalIndexToIndex(playingPlayerOriginalIndex))
            .isClientPlayer();
    }

    public DisplayablePlayer getClientPlayer() {
        return new DisplayablePlayer(displayablePlayers.get(playerNameToIndex(clientPlayerName)));
    }

    public DisplayablePlayer getPlayingPlayer() {
        return new DisplayablePlayer(displayablePlayers.get(playerOriginalIndexToIndex(playingPlayerOriginalIndex)));
    }

    public DisplayablePlayer getDisplayablePlayer(String playerName) {
        return new DisplayablePlayer(displayablePlayers.get(playerNameToIndex(playerName)));
    }

    public DisplayablePlayer getDisplayablePlayer(int playerOriginalIndex) {
        return new DisplayablePlayer(displayablePlayers.get(playerOriginalIndexToIndex(playerOriginalIndex)));
    }
}
