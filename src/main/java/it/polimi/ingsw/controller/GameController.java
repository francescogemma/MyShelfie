package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.board.FullSelectionException;
import it.polimi.ingsw.model.board.IllegalExtractionException;
import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.model.game.IllegalFlowException;
import it.polimi.ingsw.model.game.Player;
import it.polimi.ingsw.model.game.PlayerAlreadyInGameException;
import it.polimi.ingsw.model.goal.AdjacencyGoal;
import it.polimi.ingsw.model.goal.Goal;
import it.polimi.ingsw.utils.Coordinate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GameController {
    private final Game game;
    private final List<VirtualView> clients;

    private final Object lock = new Object();

    public GameController(Game game) {
        this.game = game;
        this.clients = new ArrayList<>();
    }

    public Response join(VirtualView newClient) {
        synchronized (lock) {
            try {
                game.addPlayer(newClient.getUsername());
            } catch (IllegalFlowException e) {
                return new Response(e.toString(), ResponseStatus.FAILURE);
            } catch (PlayerAlreadyInGameException e) {
                return new Response(e.toString(), ResponseStatus.FAILURE);
            }

            clients.add(newClient);

            // It is intended to notify also the client that has joined, he will know it in this way (asynchronously)
            for (VirtualView client : clients) {
                client.notifyPlayerHasJoined(newClient.getUsername());
            }

            return new Response("You've joined the game", ResponseStatus.SUCCESS);
        }
    }

    public Response selectTile(String username, Coordinate coordinate) {
        synchronized (lock) {
            try {
                if (!username.equals(game.getCurrentPlayer().getUsername())) {
                    return new Response("It is not your turn", ResponseStatus.FAILURE);
                }
            } catch (IllegalFlowException e) {
                return new Response(e.toString(), ResponseStatus.FAILURE);
            }

            try {
                game.getBoard().selectTile(coordinate);
            } catch (IllegalExtractionException e) {
                return new Response(e.toString(), ResponseStatus.FAILURE);
            } catch (FullSelectionException e) {
                return new Response(e.toString(), ResponseStatus.FAILURE);
            }

            for (VirtualView client : clients) {
                client.notifyBoardUpdate(game.getBoard());
            }

            return new Response("You've selected a tile", ResponseStatus.SUCCESS);
        }
    }

    public Response deselectTile(String username, Coordinate coordinate) {
        synchronized (lock) {
            try {
                if (!username.equals(game.getCurrentPlayer().getUsername())) {
                    return new Response("It is not your turn", ResponseStatus.FAILURE);
                }
            } catch (IllegalFlowException e) {
                return new Response(e.toString(), ResponseStatus.FAILURE);
            }

            // TODO: Wait for deselect tile to be added to Board

            return new Response("You've deselected a tile", ResponseStatus.SUCCESS);
        }
    }

    public Response insertSelectedTilesInBookshelf(String username, int column) {
        synchronized (lock) {
            try {
                if (!username.equals(game.getCurrentPlayer().getUsername())) {
                    return new Response("It is not your turn", ResponseStatus.FAILURE);
                }
            } catch (IllegalFlowException e) {
                return new Response(e.toString(), ResponseStatus.FAILURE);
            }

            Player currentPlayer;
            try {
                currentPlayer = game.getCurrentPlayer();
                currentPlayer.getBookshelf().insertTiles(
                    game.getBoard().draw(),
                    column
                );
            } catch (Exception e) {
                return new Response(e.getMessage(), ResponseStatus.FAILURE);
            }

            for (VirtualView client : clients) {
                client.notifyBoardUpdate(game.getBoard());
                client.notifyBookshelfUpdate(username, currentPlayer.getBookshelf());
            }

            for (int i = 0; i < game.getCommonGoals().length; i++) {
                if (!currentPlayer.hasAchievedCommonGoal(i)) {
                    int points = game.getCommonGoals()[i].calculatePoints(currentPlayer.getBookshelf());
                    if (points > 0) {
                        currentPlayer.addPoints(points);

                        for (VirtualView client : clients) {
                            client.notifyPlayerHasScoredPoints(username, points,
                                game.getCommonGoals()[i].getPointMasks());
                        }
                    }
                }
            }

            try {
                if (game.atLeastOneBookshelfIsFull() && username.equals(game.getLastPlayer().getUsername())) {
                    // Game ending logic:
                    for (Player player : game.getPlayers()) {
                        int personalGoalPoints = player.getPersonalGoal().calculatePoints(player.getBookshelf());
                        if (personalGoalPoints > 0) {
                            player.addPoints(personalGoalPoints);

                            for (VirtualView client : clients) {
                                client.notifyPlayerHasScoredPoints(player.getUsername(), personalGoalPoints,
                                    player.getPersonalGoal().getPointMasks());
                            }
                        }

                        Goal adjacencyGoal = new AdjacencyGoal();
                        int adjacencyGoalPoints = adjacencyGoal.calculatePoints(player.getBookshelf());

                        if (adjacencyGoalPoints > 0) {
                            player.addPoints(adjacencyGoalPoints);

                            for (VirtualView client : clients) {
                                client.notifyPlayerHasScoredPoints(player.getUsername(), adjacencyGoalPoints,
                                    adjacencyGoal.getPointMasks());
                            }
                        }
                    }

                    // TODO: Take into account for tie
                    Optional<Player> winner = Optional.empty();
                    for (Player player : game.getPlayers()) {
                        if (player.getPoints() > winner.flatMap(p -> Optional.of(p.getPoints())).orElse(0)) {
                            winner = Optional.of(player);
                        }
                    }

                    game.setWinner(winner.orElse(game.getStartingPlayer()));

                    for (VirtualView client : clients) {
                        client.notifyGameIsOver(winner.orElse(game.getStartingPlayer()).getUsername());
                    }
                } else {
                    if (game.getBoard().needsRefill()) {
                        while (!game.getBag().isEmpty() && !game.getBoard().isFull(game.getPlayers().size())) {
                            game.getBoard().fillRandomly(game.getBag().getRandomTile(), game.getPlayers().size());
                        }

                        for (VirtualView client : clients) {
                            client.notifyBoardUpdate(game.getBoard());
                        }
                    }

                    game.nextTurn();
                    for (VirtualView client : clients) {
                        client.notifyPlayingPlayer(game.getCurrentPlayer().getUsername());
                    }
                }
            } catch (IllegalFlowException e) {
                throw new IllegalStateException("The game has already started if we got to this point");
            }

            return new Response("You inserted the selected tiles in the library", ResponseStatus.SUCCESS);
        }
    }

    public Response startGame(String username) {
        synchronized (lock) {
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

            for (VirtualView client : clients) {
                client.notifyGameHasStarted();
            }

            return new Response("The game has started", ResponseStatus.SUCCESS);
        }
    }

    public Response disconnect(String username) {
        synchronized (lock) {
            boolean success = false;

            for (int i = 0; i < clients.size(); i++) {
                if (clients.get(i).getUsername().equals(username)) {
                    for (Player player : game.getPlayers()) {
                        if (player.getUsername().equals(username)) {
                            player.setConnectionState(false);
                            success = true;
                            break;
                        }
                    }

                    clients.remove(i);
                    break;
                }
            }

            if (success) {
                for (VirtualView client : clients) {
                    client.notifyPlayerHasDisconnected(username);
                }

                return new Response("You've disconnected from the game", ResponseStatus.SUCCESS);
            }

            return new Response("You're not connected to the game", ResponseStatus.FAILURE);
        }
    }
}
