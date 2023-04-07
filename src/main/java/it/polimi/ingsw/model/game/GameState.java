package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.Tile;
import it.polimi.ingsw.model.board.IllegalExtractionException;
import it.polimi.ingsw.model.board.SelectionFullException;
import it.polimi.ingsw.utils.Coordinate;

import java.util.Collection;
import java.util.Optional;

/**
 * GameState defines the base class of each game state.
 * It contains all methods invoked by Game.
 * Each function if not redefined throws an IllegalFlowException exception to
 *  indicate that the function call is not legal due to the defined state.
 *
 * @author Giacomo Groppi
 * */
abstract class GameState{
    protected GameData gameData;

    protected GameState (GameData data) {
        this.gameData = data;
    }

    /**
     * Add player to the game
     * @throws IllegalFlowException iff the game has already started
     * */
    @SuppressWarnings("unused")
    protected void addPlayer(Player player) throws IllegalFlowException {
        throw new IllegalFlowException();
    }

    /**
     *
     * */
    @SuppressWarnings("unused")
    protected void selectTile (Player player, Collection<Coordinate> coordinate, int col) throws IllegalFlowException, IllegalExtractionException, SelectionFullException {
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

    /**
     * The function returns a winner only if the game is over.
     * @throws IllegalFlowException iff game is not over jet.
     * */
    public Player getWinner() throws IllegalFlowException {
        throw new IllegalFlowException();
    }

    protected boolean containsPlayer (final Player player) {
        return this.gameData.players
                .stream()
                .anyMatch(p -> p.getKey().equals(player));
    }

    public Collection<Coordinate> getSelectableTile () throws IllegalFlowException {
        throw new IllegalFlowException();
    }

    protected void fillBagIfNecessary() {
        if (!this.gameData.board.needsRefill()) {
            return;
        }

        final int n = this.gameData.players.size();
        for (;;) {
            Tile t;
            try {
                t = this.gameData.bag.getRandomTile();
            } catch (Exception e) {
                break;
            }

            try {
                this.gameData.board.fillRandomly(t, n);
            } catch (IllegalArgumentException e) {
                this.gameData.board.forgetSelection();
                break;
            }
        }
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
