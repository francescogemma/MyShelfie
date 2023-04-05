package it.polimi.ingsw.model.game;

import it.polimi.ingsw.utils.Coordinate;

import java.util.Collection;
import java.util.Optional;

abstract class GameState{
    protected GameData gameData;

    protected GameState (GameData data) {
        this.gameData = data;
    }

    @SuppressWarnings("unused")
    protected void addPlayer(Player ignored) throws IllegalFlowException {
        throw new IllegalFlowException();
    }

    @SuppressWarnings("unused")
    protected void selectTile (Player player, Collection<Coordinate> coordinate, int col) throws IllegalFlowException {
        throw new IllegalFlowException();
    }

    protected void startGame () throws IllegalFlowException {
        throw new IllegalFlowException();
    }

    protected GameState getNextState () throws IllegalFlowException {
        throw new IllegalFlowException();
    }

    @SuppressWarnings("unused")
    protected Player getCurrentPlayer() throws IllegalFlowException {
        throw new IllegalFlowException();
    }

    Player getStartingPlayer () {
        return this.gameData.players.get(GameData.INDEX_FIRST_PLAYER).getKey();
    }

    boolean isOver () {
        return this.gameData.winner.isPresent();
    }
    boolean isConnected (Player p) throws PlayerNotInGameException {
        final int index = this.indexOf(p);
        return this.gameData.players.get(index).getValue();
    }

    String getName () {
        return this.gameData.name;
    }

    protected int indexOf(Player player) throws PlayerNotInGameException {
        int i;
        for (i = 0; i < this.gameData.players.size(); i++) {
            if (this.gameData.players.get(i).getKey().equals(player)) {
                return i;
            }
        }
        throw new PlayerNotInGameException();
    }

    private void setConnected(Player player, boolean connect) throws PlayerNotInGameException {
        final int index = indexOf(player);
        this.gameData.players.get(index).setValue(connect);
    }

    void connect(Player player) throws PlayerNotInGameException {
        setConnected(player, true);
    }

    void disconnect(Player player) throws PlayerNotInGameException {
        setConnected(player, false);
    }

    protected void setWinner(Player player) throws PlayerNotInGameException {
        this.indexOf(player);

        if (this.gameData.winner.isPresent())
            throw new IllegalArgumentException("Winner already set");

        this.gameData.winner = Optional.of(player);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj.getClass() != this.getClass())
            return false;
        return this.gameData.equals(((GameState) obj).gameData);
    }
}
