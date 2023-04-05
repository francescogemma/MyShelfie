package it.polimi.ingsw.model.game;

class SelectTileWithWinnerState extends SelectTileState{
    protected SelectTileWithWinnerState(GameData data, Player nextPlayer) {
        super(data, nextPlayer);
    }

    @Override
    protected GameState getNextState () {
        GameState nextState;
        Player next = this.getNextPlayer();
        if (next == super.currentPlayer) {
            // we need to change the state into gameover
            nextState = new OverState(this.gameData);
        } else {
            nextState = new SelectTileWithWinnerState(
                    this.gameData,
                    next
            );
        }
        return nextState;
    }

}
