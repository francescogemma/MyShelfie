package it.polimi.ingsw.model.game;

public class SelectTileWithNoWinnerState extends SelectTileState{
    public SelectTileWithNoWinnerState(GameData data, Player nextPlayer) {
        super(data, nextPlayer);
    }

    private void setWinner() {
        try {
            setWinner(currentPlayer);
        } catch (PlayerNotInGameException e) {
            assert false: "Player it's not in this game";
        }
    }

    @Override
    protected GameState getNextState() {
        SelectTileState s;
        if (selectNext) {
            Player next = this.getNextPlayer();

            // we need to move to the next state
            if (this.currentPlayer.getBookshelf().isFull()) {
                setWinner();
                s = new SelectTileWithWinnerState(
                        this.gameData,
                        next
                );
            } else {
                // there is no winner
                s = new SelectTileWithNoWinnerState(
                        this.gameData,
                        next
                );
            }
        } else {
            return this;
        }

        return s;
    }
}
