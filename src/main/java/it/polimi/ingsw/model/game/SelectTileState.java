package it.polimi.ingsw.model.game;

import it.polimi.ingsw.utils.Coordinate;

import java.util.Collection;

public abstract class SelectTileState extends GameState{
    protected final Player currentPlayer;
    protected boolean selectNext;

    protected SelectTileState(GameData data, Player nextPlayer) {
        super(data);
        this.currentPlayer = nextPlayer;
        this.selectNext = false;
    }

    /**
     * This function does not check if the game is ended.
     * @return The player after currentPlayer
     * */
    protected Player getNextPlayer () {
        int currentIndex = 0;

        try {
            currentIndex = this.indexOf(this.currentPlayer);
        } catch (PlayerNotInGameException ignored) {
            assert false: "nextPlayer is not a player in this game :(";
        }

        return this.gameData.players.get(
                (currentIndex + 1) % 4
        ).getKey();
    }

    @Override
    public void selectTile(Player player, Collection<Coordinate> position, int col) throws IllegalFlowException {
        assert !selectNext: "This function has already been called";
        selectNext = true;
        if (this.currentPlayer != player) {
            throw new IllegalFlowException();
        }
    }

    @Override
    public Player getCurrentPlayer() {
        return this.currentPlayer;
    }
}
