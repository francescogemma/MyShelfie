package it.polimi.ingsw.model.game;

/**
 * @author Giacomo Groppi
 * */
public class OverState extends GameState{
    public OverState (GameData d) {
        super(d);
    }

    @Override
    public Player getWinner () {
        assert this.gameData.winner.isPresent() : "Winner is not present ....";
        return this.gameData.winner.get();
    }
}
