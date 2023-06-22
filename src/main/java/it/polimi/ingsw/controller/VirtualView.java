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
import it.polimi.ingsw.model.game.IllegalFlowException;
import it.polimi.ingsw.utils.Coordinate;
import it.polimi.ingsw.utils.Logger;
import it.polimi.ingsw.utils.Pair;

import java.util.Objects;

/**
 * A virtual view is a view that is not directly connected to a client.
 * It contains a {@link GameController game controller} and a String that is the username of the player.
 *
 * @see EventTransmitter
 * @see GameController
 * @see MenuController
 * @author Giacomo Groppi
 */
public class VirtualView implements EventTransmitter {
    /**
     * The {@link GameController} of the game where the virtual view can be either in the lobby or in the game.
     * Initially, it is set to null and its value will only change when the virtual view enters the lobby or the game.
     * It is possible that the variable may be non-null but the virtual view is no longer connected to the game.
     * This can occur in situations where the {@link VirtualView} is removed from the game without causing
     * the disconnection itself.
     * In such cases, it is not possible to change the value of the variable because the execution happens in a
     * different thread than the one handling the requests.
     */
    private GameController gameController;

    /**
     * The username is set if and only if the {@link VirtualView} authenticates itself with the {@link MenuController}.
     */
    private String username;

    /**
     * The {@link EventTransceiver} of the connection with the client.
     */
    private final EventTransceiver transceiver;

    /**
     * Default message for client already in game
     */
    private static final String DEFAULT_MESSAGE_ALREADY_IN_GAME = "You are in a game...";

    /**
     * Default message for client not in game
     */
    private static final String DEFAULT_MESSAGE_NOT_IN_GAME = "You are not in a game...";

    /**
     * Default message for client not in lobby
     */
    private static final String DEFAULT_NOT_IN_LOBBY = "You are not in lobby...";

    /**
     * Creates a new {@link VirtualView} object with the provided transceiver connection.
     * The object will be destroyed if the connection is lost.
     *
     * @param transceiver the {@link EventTransceiver} of the connected client.
     */
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
        PlayerExitGameEventData .responder(transceiver, transceiver, this::exitGame);
        PauseGameEventData      .responder(transceiver, transceiver, this::pauseGame);
        LogoutEventData         .responder(transceiver, transceiver, this::logout);
        ExitLobbyEventData      .responder(transceiver, transceiver, this::exitLobby);

        // internal signals
        PlayerDisconnectedInternalEventData.castEventReceiver(transceiver).registerListener(event -> disconnect());

        // user event
        PlayerHasJoinMenuEventData.castEventReceiver(transceiver).registerListener(event -> this.playerHasJoinMenu());
    }

    /**
     * This method allows entering the lobby of a game.
     * The method fails if the {@link VirtualView} is not authenticated or if it is already in a lobby or game.
     *
     * @param event The event sent by the client.
     * @return Failure if already in a game or lobby, otherwise returns the response from the {@link MenuController}.
     * */
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

        Logger
                .writeMessage("[%s] ask to join lobby response %s is ok: %s"
                                .formatted(
                                        username,
                                        res.getKey().message(),
                                        res.getKey().isOk()
                                )
                );

        return res.getKey();
    }

    /**
     * This method allows exiting the lobby.
     * It fails if the {@link VirtualView} is not authenticated or if it is already in a game or lobby.
     *
     * @param event The event sent by the client.
     * @return Failure if the {@link VirtualView} is not authenticated,
     *  otherwise returns the response message forwarded by {@link MenuController}
     *  indicating the result of the operation.
     * */
    private Response<VoidEventData> exitLobby(ExitLobbyEventData event) {
        Logger.writeMessage("%s ask to leave lobby".formatted(username));

        if (!isAuthenticated()) return Response.failure(Response.notAuthenticated);

        return MenuController.getInstance().exitLobby(gameController, username);
    }

    /**
     * This method allows logging out. It fails if the user is in a game or lobby, or if the user is not authenticated.
     *
     * @param eventData The event sent by the client.
     * @return Failure if the user is in a game, lobby, or not authenticated.
     *  Otherwise, it returns the result of the operation executed on {@link MenuController}.
     * */
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

    /**
     * This method notifies the {@link MenuController} that the connection has dropped.
     */
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

    /**
     * This method pauses the game.
     *
     * @param eventData The event sent by the client.
     * @return Failure if:
     * <ul>
     *     <li>The user is not authenticated</li>
     *     <li>The user is not in any game</li>
     *     <li>The game stop operation fails</li>
     * </ul>
     *
     * @see GameController#stopGame(String)
     */
    private Response<VoidEventData> pauseGame (PauseGameEventData eventData) {
        Logger.writeMessage("[%s] ask to pause game".formatted(username));

        if (gameController == null) return Response.failure(DEFAULT_MESSAGE_NOT_IN_GAME);

        Response<VoidEventData> response = gameController.stopGame(username);

        if (response.isOk()) {
            gameController = null;
        }
        return response;
    }

    /**
     * This method allows the user to exit a game.
     *
     * @param exitGame The event sent by the client.
     * @return Failure if:
     * <ul>
     *     <li>The user is not in any game</li>
     *     <li>The method {@link MenuController#exitGame(GameController, String)} fails to execute the operation</li>
     * </ul>
     *
     * @see MenuController#exitGame(GameController, String)
     * */
    private Response<VoidEventData> exitGame (PlayerExitGameEventData exitGame) {
        Logger.writeMessage("Call for username %s".formatted(username));

        if (gameController == null) return Response.failure("You are not in a game");

        Response<VoidEventData> response = MenuController.getInstance().exitGame(gameController, username);

        if (response.isOk())
            gameController = null;

        return response;
    }

    /**
     * This method notifies the {@link MenuController} that the user is
     * ready to receive information about available games.
     */
    private void playerHasJoinMenu () {
        Logger.writeMessage("[%s] join menu".formatted(username));
        if (!isAuthenticated()) {
            Logger.writeCritical("View send join menu but he is not authenticated");
            return;
        }

        MenuController.getInstance().playerHasJoinMenu(this, username);
    }

    /**
     * This method allows creating a new game.
     *
     * @param eventData The event sent by the client.
     * @return Failure if:
     * <ul>
     *     <li> The user is not authenticated. </li>
     *     <li> The user is already in a game. </li>
     *     <li> The user is already in a lobby. </li>
     *     <li> {@link MenuController#createNewGame(String, String)} fails to create a new game. </li>
     * </ul>
     *  Otherwise, it returns the response message indicating the result of the operation,
     *  forwarded by the {@link MenuController}.
     */
    private Response<VoidEventData> createNewGame (CreateNewGameEventData eventData) {
        Logger.writeMessage("[%s] ask to create game".formatted(username));
        if (!this.isAuthenticated()) return Response.failure(DEFAULT_MESSAGE_ALREADY_IN_GAME);

        if (gameController != null) {
            synchronized (gameController.getLock()) {
                if (gameController.isInGame(username) || gameController.isInLobby(username))
                    return Response.failure(DEFAULT_MESSAGE_ALREADY_IN_GAME);
            }
        }

        return MenuController.getInstance().createNewGame(eventData.gameName(), username);
    }

    /**
     * This method restarts a game.
     *
     * @param event The event sent by the client.
     * @return Failure if:
     *  <ul>
     *      <li> The player is not in any lobby. </li>
     *      <li> {@link MenuController#restartGame(GameController, String)} fails. </li>
     *  </ul>
     *  Otherwise, it returns the response message indicating the result of the operation.
     */
    private Response<VoidEventData> restartGame (RestartGameEventData event) {
        Logger.writeMessage("%s ask for restart game".formatted(username));
        if (gameController == null) return Response.failure(DEFAULT_NOT_IN_LOBBY);

        return MenuController.getInstance().restartGame(gameController, username);
    }

    /**
     * This method asks the {@link MenuController} to enter the game.
     * If the {@link MenuController} returns a positive status, the function sets the internal reference
     * to the gameController.
     *
     * @param gameName The name of the game in question
     * @throws NullPointerException iff gameName is null
     * @throws IllegalStateException iff the user is not authenticated
     * @return FAILURE if the {@link MenuController} returns a failure status.
     * */
    private Response<VoidEventData> askToJoinGame(String gameName) {
        Objects.requireNonNull(gameName);

        if (!isAuthenticated())
            throw new IllegalStateException();

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

    /**
     * This method allows the virtual view to enter a game.
     * The method will ask to join the game directly if the virtual view is already in the lobby.
     * If the virtual view is only in the menu, it will ask the
     * {@link MenuController#joinGame(EventTransmitter, String, String)} to join the game.
     * The response from the {@link MenuController} will include the request status and the pointer to the
     * corresponding {@link GameController} object.
     *
     * @param eventData The request sent by the user via network.
     * @return FAILURE if the user is not authenticated or unable to join the game.
     *          SUCCESS otherwise.
     *
     * @see JoinGameEventData
     * */
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

    /**
     * This method asks the {@link MenuController} to authenticate the {@link VirtualView}.
     * The method fails if the {@link VirtualView} is already authenticated.
     *
     * @param event The event sent by the client.
     * @return The status of the login operation. If successful, the user's username is added as wrapped data.
     * */
    private Response<UsernameEventData> login(LoginEventData event) {
        if (isAuthenticated())
            return new Response<>(
                    "Already login",
                    ResponseStatus.FAILURE,
                    new UsernameEventData("")
            );

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

    /**
     * This method performs the operation of deselecting a previously selected tile.
     *
     * @param eventData The event sent by the client.
     * @return If the {@link VirtualView} is not authenticated, the method fails.
     *  Otherwise, it returns the response from the {@link GameController}.
     * */
    private Response<VoidEventData> deselectTile (DeselectTileEventData eventData) {
        if (gameController == null) return Response.failure(Response.notInGame);

        assert isAuthenticated();

        return gameController.deselectTile(username, eventData.coordinate());
    }

    /**
     * This method allows you to insert the selected tiles in the {@link it.polimi.ingsw.model.bookshelf.Bookshelf}.
     *
     * @param eventData The event sent by the client for the insertion of the {@link it.polimi.ingsw.model.tile.Tile}.
     * @return Failure if:
     *  <ul>
     *     <li>The {@link VirtualView} is not authenticated.</li>
     *     <li>The {@link VirtualView} is not in a game.</li>
     *     <li>The {@link GameController} returns a failure status.</li>
     *  </ul>
     *  Otherwise, it returns the response from the {@link GameController}.
     *
     * @see GameController#insertSelectedTilesInBookshelf(String, int)
     * @see InsertTileEventData
     */
    private Response<VoidEventData> insertTile (InsertTileEventData eventData) {
        if (gameController == null) return Response.failure(Response.notInGame);

        assert isAuthenticated();

        return gameController.insertSelectedTilesInBookshelf(username, eventData.column());
    }

    /**
     * This method allows you to insert the selected tiles in the {@link it.polimi.ingsw.model.board.Board}.
     * @param eventData The event sent by the client for the selection.
     * @return Failure if:
     * <ul>
     *     <li>The {@link VirtualView} is not authenticated.</li>
     *     <li>The {@link VirtualView} is not in a game.</li>
     *     <li>The {@link GameController} returns a failure status.</li>
     * </ul>
     * Otherwise, it returns the response from the {@link GameController}.
     * @see GameController#selectTile(String, Coordinate)
     */
    private Response<VoidEventData> selectTile(SelectTileEventData eventData) {
        if (gameController == null) return Response.failure(Response.notInGame);

        assert isAuthenticated();

        return this.gameController.selectTile(username, eventData.coordinate());
    }

    /**
     * This method allows you to start the game.
     * @param ignore The event sent by the client.
     * @return Failure if:
     * <ul>
     *     <li>The {@link VirtualView} is not authenticated.</li>
     *     <li>The {@link GameController} returns a failure status.</li>
     * </ul>
     * Otherwise, it returns the response from the {@link MenuController}.
     *
     * @see StartGameEventData
     * @see MenuController#startGame(GameController, String)
     */
    private Response<VoidEventData> startGame (StartGameEventData ignore) {
        if (gameController == null) return Response.failure(Response.notInGame);

        assert isAuthenticated();

        return MenuController.getInstance().startGame(gameController, username);
    }

    /**
     * This method sets the internal reference to the {@link GameController}.
     *
     * @param gameController The {@link GameController} to be set.
     */
    private void setGameController(GameController gameController) {
        assert gameController != null;
        assert isAuthenticated();

        this.gameController = gameController;
    }

    /**
     * @return true if the virtual view is authenticated
     */
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
