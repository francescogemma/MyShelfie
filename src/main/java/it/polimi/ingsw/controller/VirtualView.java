package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.servercontroller.GameController;
import it.polimi.ingsw.controller.servercontroller.MenuController;
import it.polimi.ingsw.event.EventTransceiver;
import it.polimi.ingsw.event.data.EventData;
import it.polimi.ingsw.event.data.client.LoginEventData;
import it.polimi.ingsw.event.data.client.*;
import it.polimi.ingsw.event.data.game.GameHasBeenStoppedEventData;
import it.polimi.ingsw.event.data.game.GameOverEventData;
import it.polimi.ingsw.event.data.internal.PlayerDisconnectedInternalEventData;
import it.polimi.ingsw.event.transmitter.EventTransmitter;
import it.polimi.ingsw.utils.Logger;
import it.polimi.ingsw.utils.Pair;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class VirtualView implements EventTransmitter{
    private GameController gameController;
    private String username;
    private final EventTransceiver transceiver;

    private static final Response DEFAULT_MESSAGE_NOT_AUTHENTICATED = new Response("You are not login", ResponseStatus.FAILURE);
    private static final Response DEFAULT_MESSAGE_ALREADY_IN_GAME = new Response("You are in a game...", ResponseStatus.FAILURE);
    private static final Response DEFAULT_MESSAGE_NOT_IN_GAME = new Response("You are not in a game...", ResponseStatus.FAILURE);
    private static final Response DEFAULT_IN_LOBBY = new Response("You are in lobby...", ResponseStatus.FAILURE);
    private static final Response DEFAULT_NOT_IN_LOBBY = new Response("You are not in lobby...", ResponseStatus.FAILURE);

    private static final List<String> idForceExit = Arrays.asList(
            GameOverEventData.ID,
            GameHasBeenStoppedEventData.ID
    );

    public VirtualView(EventTransceiver transceiver) {
        if (transceiver == null)
            throw new NullPointerException();

        this.transceiver = transceiver;
        gameController = null;

        LoginEventData          .responder(transceiver, transceiver, this::login);
        StartGameEventData      .responder(transceiver, transceiver, this::startGame);
        InsertTileEventData     .responder(transceiver, transceiver, this::insertTile);
        SelectTileEventData     .responder(transceiver, transceiver, this::selectTile);
        DeselectTileEventData   .responder(transceiver, transceiver, this::deselectTile);
        JoinGameEventData       .responder(transceiver, transceiver, this::joinGame);
        RestartGameEventData    .responder(transceiver, transceiver, this::restartGame);
        JoinLobbyEventData      .responder(transceiver, transceiver, this::joinLobby);

        CreateNewGameEventData  .responder(transceiver, transceiver, this::createNewGame);
        PlayerExitGame          .responder(transceiver, transceiver, this::exitGame);
        PauseGameEventData      .responder(transceiver, transceiver, this::pauseGame);
        LogoutEventData         .responder(transceiver, transceiver, this::logout);
        ExitLobbyEventData      .responder(transceiver, transceiver, this::exitLobby);

        PlayerDisconnectedInternalEventData.castEventReceiver(transceiver).registerListener(event -> disconnect());

        PlayerHasJoinMenu       .castEventReceiver(transceiver).registerListener(event -> this.playerHasJoinMenu());
    }

    private synchronized Response joinLobby (JoinLobbyEventData event) {
        Logger.writeMessage("%s ask to join lobby".formatted(username));
        if (isAuthenticated() && !isInGame() && !isInLobby()) {
            Pair<Response, GameController> res = MenuController
                    .getInstance()
                    .joinLobby(this, username, event.gameName());

            if (res.getKey().isOk()) {
                this.gameController = res.getValue();
            }

            return res.getKey();
        } else {
            return DEFAULT_IN_LOBBY;
        }
    }

    private synchronized Response exitLobby(ExitLobbyEventData event) {
        Logger.writeMessage("%s ask to leave lobby".formatted(username));
        if (isInLobby()) {
            Response res = this.gameController.exitLobby(username);

            if (res.isOk()) {
                gameController = null;
            }

            return res;
        } else {
            return DEFAULT_NOT_IN_LOBBY;
        }
    }

    private synchronized Response logout (LogoutEventData eventData) {
        Logger.writeMessage("%s logout".formatted(username));
        if (isInGame() || isInLobby()) {
            Logger.writeWarning("The client has asked to log out but is in game");
            return new Response("You are in a game or lobby...", ResponseStatus.FAILURE);
        }

        if (isAuthenticated()) {
            Response response = MenuController.getInstance().logout(this);

            if (response.isOk()) {
                this.username = null;
                this.gameController = null;
            }

            return response;
        } else {
            Logger.writeWarning("The client has asked to log out but is not authenticated");
            return new Response("You are not login", ResponseStatus.SUCCESS);
        }
    }

    private synchronized void disconnect() {
        Logger.writeMessage("user %s disconnected".formatted(username));
        MenuController.getInstance().forceDisconnect(
                this,
                gameController,
                username
        );

        this.gameController = null;
    }

    private synchronized Response pauseGame (PauseGameEventData eventData) {
        Logger.writeMessage("Call for username: %s".formatted(username));

        if (isInGame()) {
            Response response = gameController.stopGame(username);

            if (response.isOk()) {
                this.gameController = null;
            }

            return response;
        } else {
            return DEFAULT_MESSAGE_NOT_IN_GAME;
        }
    }

    private synchronized Response exitGame (PlayerExitGame exitGame) {
        Logger.writeMessage("Call for username %s".formatted(username));
        if (isInGame()) {
            Response response = MenuController.getInstance().exitGame(gameController, this);

            if (response.isOk()) {
                this.gameController = null;
            }
            return response;
        } else {
            return new Response("You are not in a game", ResponseStatus.FAILURE);
        }
    }

    private synchronized void playerHasJoinMenu () {
        if (this.isAuthenticated()) {
            MenuController.getInstance().playerHasJoinMenu(this, username);
        } else {
            Logger.writeCritical("View send join menu but he is not authenticated");
        }
    }

    private synchronized Response createNewGame (CreateNewGameEventData eventData) {
        if (this.isAuthenticated()) {
            if (!isInGame() && !isInLobby()) {
                return MenuController.getInstance().createNewGame(eventData.gameName(), username);
            } else {
                return DEFAULT_MESSAGE_ALREADY_IN_GAME;
            }
        } else {
            return DEFAULT_MESSAGE_NOT_AUTHENTICATED;
        }
    }

    private synchronized Response restartGame (RestartGameEventData event) {
        Logger.writeMessage("%s ask for restart game".formatted(username));
        if (isInLobby()) {
            Response res = gameController.restartGame(username);

            if (res.isOk()) {
                Logger.writeMessage("registerListener");
            }

            return res;
        } else {
            return DEFAULT_NOT_IN_LOBBY;
        }
    }

    private synchronized Response joinGame (JoinGameEventData eventData) {
        Logger.writeMessage("%s ask for join game".formatted(username));
        if (this.isAuthenticated()) {
            if (isInGame()) {
                return DEFAULT_MESSAGE_ALREADY_IN_GAME;
            }

            Response response;

            if (isInLobby()) {
                response = gameController.joinGame(username);
            } else {
                Pair<Response, GameController> pair = MenuController
                    .getInstance().joinGame(this, username, eventData.getGameName());

                response = pair.getKey();

                if (pair.getValue() != null) {
                    assert pair.getKey().isOk();
                    setGameController(pair.getValue());
                }
            }

            return response;
        } else
            return DEFAULT_MESSAGE_NOT_AUTHENTICATED;
    }

    private synchronized Response login(LoginEventData event) {
        if (!isAuthenticated()) {
            Response response = MenuController.getInstance().authenticated(
                    this,
                    event.getUsername(),
                    event.getPassword()
            );

            if (response.isOk()) {
                this.username = event.getUsername();
            }

            return response;
        } else {
            return new Response("You are already login", ResponseStatus.FAILURE);
        }
    }

    private synchronized Response deselectTile (DeselectTileEventData eventData) {
        if (isInGame()) {
            return gameController.deselectTile(username, eventData.coordinate());
        } else {
            return DEFAULT_MESSAGE_NOT_AUTHENTICATED;
        }
    }

    private synchronized Response insertTile (InsertTileEventData eventData) {
        if (isInGame()) {
            return gameController.insertSelectedTilesInBookshelf(username, eventData.column());
        } else {
            return DEFAULT_MESSAGE_NOT_AUTHENTICATED;
        }
    }

    private synchronized Response selectTile(SelectTileEventData eventData) {
        if (isInGame()) {
            return this.gameController.selectTile(username, eventData.coordinate());
        } else {
            return DEFAULT_MESSAGE_NOT_AUTHENTICATED;
        }
    }

    private synchronized Response startGame (StartGameEventData ignore) {
        if (this.isAuthenticated()) {
            if (this.isInLobby()) {
                return MenuController.getInstance().startGame(gameController, username);
            } else {
                return DEFAULT_NOT_IN_LOBBY;
            }
        } else {
            return DEFAULT_MESSAGE_NOT_AUTHENTICATED;
        }
    }

    public synchronized String getUsername () {
        if (username == null)
            throw new IllegalStateException("You can't call this function if virtualview is not authenticated");

        return username;
    }

    public synchronized void setGameController(GameController gameController) {
        assert this.gameController == null;
        assert gameController != null;
        assert isAuthenticated();

        this.gameController = gameController;
    }

    public synchronized boolean isAuthenticated () {
        assert username != null || (gameController == null);

        return username != null;
    }

    public synchronized boolean isInGame() {
        final boolean res = gameController != null && gameController.isInGame(username);

        // res ==> isAuthenticated
        assert !res || isAuthenticated();

        return res;
    }

    public synchronized boolean isInLobby() {
        final boolean res = gameController != null && this.gameController.isInLobby(username);

        // res ==> isAuthenticated
        assert !res || isAuthenticated();

        return res;
    }

    @Override
    public void broadcast(EventData data) {
        if (idForceExit.contains(data.getId()) && isInGame())
            gameController = null;

        Logger.writeMessage("%s player receive %s".formatted(username, data.getId()));

        this.transceiver.broadcast(data);
    }
}
