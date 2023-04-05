package it.polimi.ingsw.model.game;

import it.polimi.ingsw.utils.Pair;

public class AddPlayerState extends GameState{
    private boolean askForStartGame;
    public AddPlayerState(String name) {
        super(new GameData(name));
        askForStartGame = false;
    }

    @Override
    public void addPlayer(Player player) throws IllegalFlowException {
        if (player == null)
            throw new NullPointerException();
        if (this.gameData.players.size() == 4)
            throw new IllegalFlowException("Player are already 4");
        this.gameData.players.add(new Pair<>(player, true));

        try {
            super.connect(player);
        } catch (Exception e) {
            throw new RuntimeException("The player I just inserted does not exist ...");
        }
    }

    @Override
    public void startGame() throws IllegalFlowException {
        if (this.gameData.players.isEmpty()) {
            throw new IllegalFlowException("This game contains 0 players...");
        }
        askForStartGame = true;
    }

    @Override
    public GameState getNextState () {
        if (this.askForStartGame) {
            return new SelectTileWithNoWinnerState(
                    this.gameData,
                    this.gameData.players.get(0).getKey()
            );
        } else {
            return this;
        }
    }
}
