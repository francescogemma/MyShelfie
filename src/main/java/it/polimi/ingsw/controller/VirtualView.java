package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.servercontroller.GameController;
import it.polimi.ingsw.controller.servercontroller.MenuController;
import it.polimi.ingsw.event.EventTransceiver;
import it.polimi.ingsw.event.data.EventData;
import it.polimi.ingsw.event.data.VoidEventData;
import it.polimi.ingsw.event.data.client.LoginEventData;
import it.polimi.ingsw.event.data.client.*;
import it.polimi.ingsw.event.data.internal.PlayerDisconnectedInternalEventData;
import it.polimi.ingsw.event.transmitter.EventTransmitter;
import it.polimi.ingsw.utils.Logger;
import it.polimi.ingsw.utils.Pair;

import java.util.Objects;

public class VirtualView implements EventTransmitter {
    private GameController gameController;
    private String username;
    private final EventTransceiver transceiver;

    private static final String DEFAULT_MESSAGE_ALREADY_IN_GAME = "You are in a game...";
    private static final String DEFAULT_MESSAGE_NOT_IN_GAME = "You are not in a game...";
    private static final String DEFAULT_NOT_IN_LOBBY = "You are not in lobby...";

    public VirtualView(EventTransceiver transceiver) {
        Objects.requireNonNull(transceiver);

        this.transceiver = transceiver;
        this.gameController = null;

        // user request
        LoginEventData          .responder(transceiver, transceiver, this::login);
        StartGameEventData      .responder(transceiver, transceiver, this::startGame);
        InsertTileEventData     .responder(transceiver, transceiver, this::insertTile);
        SelectTileEventData     .responder(transceiver, transceiver, this::selectTile);
        DeselectTileEventData   .responder(transceiver, transceiver, this::deselectTile);
        JoinGameEventData       .responder(transceiver, transceiver, this::joinGame);
        RestartGameEventData    .responder(transceiver, transceiver, this::restartGame);
        JoinLobbyEventData      .responder(transceiver, transceiver, this::joinLobby);
        CreateNewGameEventData  .responder(transceiver, transceiver, this::createNewGame);
        PlayerExitGameEventData.responder(transceiver, transceiver, this::exitGame);
        PauseGameEventData      .responder(transceiver, transceiver, this::pauseGame);
        LogoutEventData         .responder(transceiver, transceiver, this::logout);
        ExitLobbyEventData      .responder(transceiver, transceiver, this::exitLobby);

        // internal signals
        PlayerDisconnectedInternalEventData.castEventReceiver(transceiver).registerListener(event -> disconnect());

        // user event
        PlayerHasJoinMenuEventData.castEventReceiver(transceiver).registerListener(event -> this.playerHasJoinMenu());
    }

    private Response<VoidEventData> joinLobby (JoinLobbyEventData event) {
        Logger.writeMessage("%s ask to join lobby".formatted(username));

        if (!isAuthenticated()) return Response.failure(Response.notAuthenticated);

        Pair<Response<VoidEventData>, GameController> res;

        if (gameController != null) {
            synchronized (gameController.getLock()) {
                if (gameController.isInLobby(username) || gameController.isInGame(username))
                    return Response.failure("You are not in menu");
            }
        }

        res = MenuController
                .getInstance()
                .joinLobby(this, username, event.gameName());

        if (res.getKey().isOk()) {
            this.gameController = res.getValue();
        }

        Logger.writeMessage("[%s] ask to join lobby response %s is ok: %s".formatted(username, res.getKey().message(), res.getKey().isOk()));

        return res.getKey();
    }

    private Response exitLobby(ExitLobbyEventData event) {
        Logger.writeMessage("%s ask to leave lobby".formatted(username));

        if (!isAuthenticated()) return Response.failure(Response.notAuthenticated);

        return MenuController.getInstance().exitLobby(gameController, username);
    }

    private Response<VoidEventData> logout (LogoutEventData eventData) {
        Logger.writeMessage("%s logout".formatted(username));

        Response<VoidEventData> response;

        if(!isAuthenticated()) return Response.failure(Response.notAuthenticated);

        if (gameController != null) {
            synchronized (gameController.getLock()) {
                if (gameController.isInGame(username) || gameController.isInLobby(username)) {
                    Logger.writeWarning("The client has asked to log out but is in game");
                    return Response.failure("You are in a game or lobby...");
                }

                response = MenuController.getInstance().logout(this, username);
            }
        } else {
            response = MenuController.getInstance().logout(this, username);
        }

        if (response.isOk()) {
            username = null;
            gameController = null;
        }

        return response;
    }

    private void disconnect() {
        Logger.writeMessage("[%s] disconnected".formatted(username));

        MenuController.getInstance().forceDisconnect(
                this,
                gameController,
                username
        );

        this.gameController = null;
        this.username = null;
    }

    private Response<VoidEventData> pauseGame (PauseGameEventData eventData) {
        Logger.writeMessage("[%s] ask to pause game".formatted(username));

        if (gameController == null) return Response.failure(DEFAULT_MESSAGE_NOT_IN_GAME);

        Response<VoidEventData> response = gameController.stopGame(username);

        if (response.isOk()) {
            gameController = null;
        }
        return response;
    }

    private Response<VoidEventData> exitGame (PlayerExitGameEventData exitGame) {
        Logger.writeMessage("Call for username %s".formatted(username));

        if (gameController == null) return Response.failure("You are not in a game");

        Response<VoidEventData> response = MenuController.getInstance().exitGame(gameController, username);

        if (response.isOk())
            gameController = null;

        return response;
    }

    private void playerHasJoinMenu () {
        Logger.writeMessage("[%s] join menu".formatted(username));
        if (!isAuthenticated()) {
            Logger.writeCritical("View send join menu but he is not authenticated");
            return;
        }

        MenuController.getInstance().playerHasJoinMenu(this, username);
    }

    private Response<VoidEventData> createNewGame (CreateNewGameEventData eventData) {
        Logger.writeMessage("[%s] ask to create game".formatted(username));
        if (!this.isAuthenticated()) return Response.failure(DEFAULT_MESSAGE_ALREADY_IN_GAME);

        if (gameController != null && (gameController.isInLobby(username) || gameController.isInGame(username)))
            return Response.failure(DEFAULT_MESSAGE_ALREADY_IN_GAME);

        return MenuController.getInstance().createNewGame(eventData.gameName(), username);
    }

    private Response<VoidEventData> restartGame (RestartGameEventData event) {
        Logger.writeMessage("%s ask for restart game".formatted(username));
        if (gameController == null) return Response.failure(DEFAULT_NOT_IN_LOBBY);

        return MenuController.getInstance().restartGame(gameController, username);
    }

    private Response<VoidEventData> askToJoinGame(String gameName) {
        Response<VoidEventData> response;
        Pair<Response<VoidEventData>, GameController> pair = MenuController
                .getInstance().joinGame(this, username, gameName);

        response = pair.getKey();

        if (response.isOk()) {
            assert pair.getKey() != null;
            setGameController(pair.getValue());
        }

        return response;
    }

    private Response<VoidEventData> joinGame (JoinGameEventData eventData) {
        Logger.writeMessage("%s ask for join game".formatted(username));
        if (!isAuthenticated()) return Response.failure(Response.notAuthenticated);

        Response<VoidEventData> response = null;

        if (gameController != null) {
            synchronized (gameController.getLock()) {
                if (gameController.isInLobby(username)) {
                    response = gameController.joinGame(username);
                }
            }

            if (response == null)
                response = askToJoinGame(eventData.getGameName());
        } else {
            response = askToJoinGame(eventData.getGameName());
        }

        Logger.writeMessage("%s receiver %s".formatted(username, response.message()));

        return response;
    }

    private Response<UsernameEventData> login(LoginEventData event) {
        if (isAuthenticated()) return new Response<>("Already login", ResponseStatus.FAILURE, new UsernameEventData(""));

        Response<UsernameEventData> response = MenuController.getInstance().login(
                this,
                event.getUsername(),
                event.getPassword()
        );

        if (response.isOk()) {
            this.username = response.getWrappedData().getUsername();
        }
        return response;
    }

    private Response<VoidEventData> deselectTile (DeselectTileEventData eventData) {
        if (gameController == null) return Response.failure(Response.notInGame);

        assert isAuthenticated();

        return gameController.deselectTile(username, eventData.coordinate());
    }

    private Response<VoidEventData> insertTile (InsertTileEventData eventData) {
        if (gameController == null) return Response.failure(Response.notInGame);

        assert isAuthenticated();

        return gameController.insertSelectedTilesInBookshelf(username, eventData.column());
    }

    private Response<VoidEventData> selectTile(SelectTileEventData eventData) {
        if (gameController == null) return Response.failure(Response.notInGame);

        assert isAuthenticated();

        return this.gameController.selectTile(username, eventData.coordinate());
    }

    private Response<VoidEventData> startGame (StartGameEventData ignore) {
        if (gameController == null) return Response.failure(Response.notInGame);

        assert isAuthenticated();

        return MenuController.getInstance().startGame(gameController, username);
    }

    private void setGameController(GameController gameController) {
        assert gameController != null;
        assert isAuthenticated();

        this.gameController = gameController;
    }

    private boolean isAuthenticated () {
        assert username != null || (gameController == null);

        return username != null;
    }

    @Override
    public void broadcast(EventData data) {
        Logger.writeMessage("[%s] player receive %s".formatted(username, data.getId()));

        this.transceiver.broadcast(data);
    }
}
