package it.polimi.ingsw.controller;

import it.polimi.ingsw.event.LocalEventTransceiver;
import it.polimi.ingsw.event.data.gameEvent.*;
import it.polimi.ingsw.model.board.FullSelectionException;
import it.polimi.ingsw.model.board.IllegalExtractionException;
import it.polimi.ingsw.model.board.RemoveNotLastSelectedException;
import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.model.game.IllegalFlowException;
import it.polimi.ingsw.model.game.PlayerAlreadyInGameException;
import it.polimi.ingsw.utils.Coordinate;

// events

import java.util.ArrayList;
import java.util.List;

public class GameController {
    private final Game game;
    private final List<VirtualView> clients;
    private final LocalEventTransceiver transceiver;

    public GameController(Game game) {
        this.game = game;
        this.clients = new ArrayList<>();
        this.transceiver = new LocalEventTransceiver();

        game.setTransceiver(this.transceiver);

        PlayerPointsChangeEventData.castEventReceiver(transceiver).registerListener(event ->
            clients.forEach(client ->
                    client.notifyPlayerHasScoredPoints(
                            event.getPlayer().getUsername(),
                            event.getPlayer().getPoints(),
                            event.getBookshelfMaskSet()
                    )
        ));

        BoardChangedEventData.castEventReceiver(transceiver).registerListener(event ->
            clients.forEach(virtualView -> virtualView.notifyBoardUpdate(event.getBoard()))
        );

        GameOverEventData.castEventReceiver(transceiver).registerListener(event ->
                clients.forEach(virtualView -> virtualView.notifyGameIsOver(event.getWinnersUsername())));

        PlayerHasDisconnectedEventData.castEventReceiver(transceiver).registerListener(event ->
                clients.forEach(virtualView -> virtualView.notifyPlayerHasDisconnected(event.getUsername())));

        BoardChangedEventData.castEventReceiver(transceiver).registerListener(event ->
                clients.forEach(virtualView -> virtualView.notifyBoardUpdate(event.getBoard())));

        GameHasStartedEventData.castEventReceiver(transceiver).registerListener(event ->
                clients.forEach(VirtualView::notifyGameHasStarted));
    }

    public Response join(VirtualView newClient) {
        if (newClient == null) {
            throw new NullPointerException();
        }

        synchronized (this) {
            try {
                game.addPlayer(newClient.getUsername());
            } catch (IllegalFlowException | PlayerAlreadyInGameException e) {
                return new Response(e.toString(), ResponseStatus.FAILURE);
            }

            clients.add(newClient);

            return new Response("You've joined the game", ResponseStatus.SUCCESS);
        }
    }

    public Response selectTile(String username, Coordinate coordinate) {
        synchronized (this) {
            try {
                this.game.selectTile(username, coordinate);
            } catch (IllegalExtractionException | FullSelectionException | IllegalFlowException e) {
                return new Response(e.toString(), ResponseStatus.FAILURE);
            }

            return new Response("You've selected a tile", ResponseStatus.SUCCESS);
        }
    }

    public Response deselectTile(String username, Coordinate coordinate) {
        synchronized (this) {
            try {
                this.game.forgetLastSelection(username, coordinate);
            } catch (IllegalFlowException  | RemoveNotLastSelectedException | IllegalArgumentException e) {
                return new Response(e.toString(), ResponseStatus.FAILURE);
            }

            return new Response("You've deselected a tile", ResponseStatus.SUCCESS);
        }
    }

    public Response insertSelectedTilesInBookshelf(String username, int column) {
        synchronized (this) {
            try {
                this.game.insertTile(username, column);
                return new Response("Ok!", ResponseStatus.SUCCESS);
            } catch (IllegalExtractionException | IllegalFlowException e) {
                return new Response(e.getMessage(), ResponseStatus.FAILURE);
            }
        }
    }

    public Response startGame(String username) {
        synchronized (this) {
            try {
                if (!username.equals(game.getStartingPlayer().getUsername())) {
                    return new Response("Only the starting player: " + game.getStartingPlayer().getUsername() +
                        " can start the game", ResponseStatus.FAILURE);
                }
            } catch (IllegalFlowException e) {
                return new Response(e.getMessage(), ResponseStatus.FAILURE);
            }

            try {
                game.startGame();
            } catch (IllegalFlowException e) {
                return new Response(e.getMessage(), ResponseStatus.FAILURE);
            }

            return new Response("The game has started", ResponseStatus.SUCCESS);
        }
    }

    public Response disconnect(String username) {
        synchronized (this) {
            try {
                this.game.disconnectPlayer(username);
            } catch (IllegalArgumentException e) {
                return new Response("You're not connected to the game", ResponseStatus.FAILURE);
            }

            for (int i = 0; i < clients.size(); i++) {
                if (this.clients.get(i).getUsername().equals(username)) {
                    this.clients.remove(i);
                    return new Response("You're now disconnected to the game", ResponseStatus.SUCCESS);
                }
            }

            return new Response("You're not connected to the game", ResponseStatus.FAILURE);
        }
    }
}
