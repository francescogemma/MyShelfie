package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.db.DBManager;
import it.polimi.ingsw.event.LocalEventTransceiver;
import it.polimi.ingsw.event.data.clientEvent.StartGameEventData;
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

        transceiver.registerListener(event ->
                clients.forEach(
                        virtualView -> {
                            virtualView.getNetworkTransmitter().broadcast(event);
                            synchronized (this) {
                                // TODO --> don't pass game, pass a GameView
                                if (this.game.isStarted())
                                    DBManager.getGamesDBManager().save(game);
                            }
                        }
                )
        );
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
        }

        StartGameEventData.responder(newClient.getNetworkTransmitter(), newClient.getNetworkReceiver(), event -> {
            return this.startGame(newClient);
        });

        return new Response("You've joined the game", ResponseStatus.SUCCESS);
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

    private Response startGame(VirtualView view) {
        synchronized (this) {
            try {
                if (!view.getUsername().equals(game.getStartingPlayer().getUsername())) {
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

    public boolean contains(VirtualView virtualView) {
        synchronized (this) {
            return this.clients.contains(virtualView);
        }
    }

    public String gameName () {
        return this.game.getName();
    }
}
