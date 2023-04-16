package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.bookshelf.Bookshelf;
import it.polimi.ingsw.model.bookshelf.BookshelfMaskSet;

import java.util.Collection;

public class VirtualView {
    private GameController gameController;
    private String username;

    public String getUsername() {
        return username;
    }

    public void setGameController(GameController gameController) {
        this.gameController = gameController;
    }

    public void notifyGameHasStarted() {

    }

    public void notifyPlayerHasJoined(String username) {

    }

    public void notifyPlayingPlayer(String username) {

    }

    public void notifyBoardUpdate(Board board) {

    }

    public void notifyBookshelfUpdate(String username, Bookshelf bookshelf) {

    }

    public void notifyPlayerHasScoredPoints(String username, int points, BookshelfMaskSet pointMasks) {

    }

    public void notifyGameIsOver(Collection<String> winnerUsername) {

    }

    public void notifyPlayerHasDisconnected(String username) {

    }
}
