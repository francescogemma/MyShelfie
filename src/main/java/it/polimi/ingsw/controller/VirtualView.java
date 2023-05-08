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

import java.util.Arrays;
import java.util.List;

public class VirtualView implements EventTransmitter {
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

    private Response joinLobby (JoinLobbyEventData event) {
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

    private Response exitLobby(ExitLobbyEventData event) {
        Logger.writeMessage("%s ask to leave lobby".formatted(username));

        if (!isInLobby()) return DEFAULT_NOT_IN_LOBBY;

        Response response = MenuController.getInstance().exitLobby(gameController, username);

        if (response.isOk())
            gameController = null;

        return response;
    }

    private Response logout (LogoutEventData eventData) {
        Logger.writeMessage("%s logout".formatted(username));
        if (isInGame() || isInLobby()) {
            Logger.writeWarning("The client has asked to log out but is in game");
            return new Response("You are in a game or lobby...", ResponseStatus.FAILURE);
        }

        if (!isAuthenticated())
            return new Response("You are not login", ResponseStatus.SUCCESS);

        Response response = MenuController.getInstance().logout(this, username);

        if (response.isOk()) {
            this.username = null;
            this.gameController = null;
        }

        return response;
    }

    private void disconnect() {
        Logger.writeMessage("user %s disconnected".formatted(username));

        MenuController.getInstance().forceDisconnect(
                this,
                gameController,
                username
        );

        this.gameController = null;
        this.username = null;
    }

    private Response pauseGame (PauseGameEventData eventData) {
        Logger.writeMessage("Call for username: %s".formatted(username));

        if (!isInGame()) return DEFAULT_MESSAGE_NOT_IN_GAME;

        Response response = gameController.stopGame(username);

        if (response.isOk()) {
            gameController = null;
        }
        return response;
    }

    private Response exitGame (PlayerExitGame exitGame) {
        Logger.writeMessage("Call for username %s".formatted(username));

        if (!isInGame()) return new Response("You are not in a game", ResponseStatus.FAILURE);

        Response response = MenuController.getInstance().exitGame(gameController, username);

        if (response.isOk())
            gameController = null;

        return response;
    }

    private void playerHasJoinMenu () {
        if (!isAuthenticated())
            Logger.writeCritical("View send join menu but he is not authenticated");

        MenuController.getInstance().playerHasJoinMenu(this, username);
    }

    private Response createNewGame (CreateNewGameEventData eventData) {
        if (this.isAuthenticated()) {
            if (isInGame() || isInLobby())
                return DEFAULT_MESSAGE_ALREADY_IN_GAME;
            return MenuController.getInstance().createNewGame(eventData.gameName(), username);
        } else {
            return DEFAULT_MESSAGE_NOT_AUTHENTICATED;
        }
    }

    private Response restartGame (RestartGameEventData event) {
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

    private Response joinGame (JoinGameEventData eventData) {
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

    private Response login(LoginEventData event) {
        if (isAuthenticated())
            return new Response("You are already login", ResponseStatus.FAILURE);
        Response response = MenuController.getInstance().authenticated(
                this,
                event.getUsername(),
                event.getPassword()
        );

        if (response.isOk()) {
            this.username = event.getUsername();
        }
        return response;
    }

    private Response deselectTile (DeselectTileEventData eventData) {
        if (isInGame()) {
            return gameController.deselectTile(username, eventData.coordinate());
        } else {
            return DEFAULT_MESSAGE_NOT_AUTHENTICATED;
        }
    }

    private Response insertTile (InsertTileEventData eventData) {
        if (isInGame()) {
            return gameController.insertSelectedTilesInBookshelf(username, eventData.column());
        } else {
            return DEFAULT_MESSAGE_NOT_AUTHENTICATED;
        }
    }

    private Response selectTile(SelectTileEventData eventData) {
        if (isInGame()) {
            return this.gameController.selectTile(username, eventData.coordinate());
        } else {
            return DEFAULT_MESSAGE_NOT_AUTHENTICATED;
        }
    }

    private Response startGame (StartGameEventData ignore) {
        if (this.isInLobby()) {
            return MenuController.getInstance().startGame(gameController, username);
        } else {
            return DEFAULT_NOT_IN_LOBBY;
        }
    }

    private void setGameController(GameController gameController) {
        assert this.gameController == null;
        assert gameController != null;
        assert isAuthenticated();

        this.gameController = gameController;
    }

    private boolean isAuthenticated () {
        assert username != null || (gameController == null);

        return username != null;
    }

    private boolean isInGame() {
        final boolean res = gameController != null && gameController.isInGame(username);

        // res ==> isAuthenticated
        assert !res || isAuthenticated();

        return res;
    }

    private boolean isInLobby() {
        final boolean res = gameController != null && this.gameController.isInLobby(username);

        // res ==> isAuthenticated
        assert !res || isAuthenticated();

        return res;
    }

    @Override
    public void broadcast(EventData data) {
        Logger.writeMessage("%s player receive %s".formatted(username, data.getId()));

        this.transceiver.broadcast(data);
    }
}
