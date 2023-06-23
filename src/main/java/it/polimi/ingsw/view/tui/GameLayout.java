package it.polimi.ingsw.view.tui;

import it.polimi.ingsw.controller.Response;
import it.polimi.ingsw.event.NetworkEventTransceiver;
import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.data.VoidEventData;
import it.polimi.ingsw.event.data.client.*;
import it.polimi.ingsw.event.data.game.*;
import it.polimi.ingsw.event.data.internal.PlayerDisconnectedInternalEventData;
import it.polimi.ingsw.event.receiver.EventListener;
import it.polimi.ingsw.event.receiver.EventReceiver;
import it.polimi.ingsw.model.board.BoardView;
import it.polimi.ingsw.model.bookshelf.Bookshelf;
import it.polimi.ingsw.model.bookshelf.BookshelfView;
import it.polimi.ingsw.model.game.IllegalFlowException;
import it.polimi.ingsw.model.game.Player;
import it.polimi.ingsw.model.goal.CommonGoal;
import it.polimi.ingsw.model.goal.PersonalGoal;
import it.polimi.ingsw.model.tile.Tile;
import it.polimi.ingsw.model.tile.TileColor;
import it.polimi.ingsw.networking.DisconnectedException;
import it.polimi.ingsw.utils.Coordinate;
import it.polimi.ingsw.view.displayable.DisplayableGoal;
import it.polimi.ingsw.view.displayable.DisplayablePlayer;
import it.polimi.ingsw.view.displayable.DisplayableScoreBoard;
import it.polimi.ingsw.view.popup.PopUp;
import it.polimi.ingsw.view.popup.PopUpQueue;
import it.polimi.ingsw.view.tui.terminal.drawable.*;
import it.polimi.ingsw.view.tui.terminal.drawable.app.AppLayout;
import it.polimi.ingsw.view.tui.terminal.drawable.app.AppLayoutData;
import it.polimi.ingsw.view.tui.terminal.drawable.menu.Button;
import it.polimi.ingsw.view.tui.terminal.drawable.menu.value.TextBox;
import it.polimi.ingsw.view.tui.terminal.drawable.orientedlayout.OrientedLayout;
import it.polimi.ingsw.view.tui.terminal.drawable.orientedlayout.OrientedLayoutElement;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.Color;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.PrimitiveSymbol;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * {@link AppLayout} to display the whole game playground composed of a {@link BoardDrawable},
 * a {@link BookshelfDrawable} plus a scoreboard and the descriptions of personal, common and final goals.
 *
 * @author Cristiano Migali
 */
public class GameLayout extends AppLayout {
    /**
     * Unique name of the game layout required to allow other AppLayouts to tell
     * the {@link it.polimi.ingsw.view.tui.terminal.drawable.app.App} to switch to this layout.
     *
     * @see AppLayout
     */
    public static final String NAME = "GAME";

    // Layout:
    // Goals panel:

    /**
     * It is the number of lines of the {@link TextBox} which contains the description of the goals (common,
     * personal and final).
     */
    private static final int GOALS_DESCRIPTION_TEXT_BOX_LINES = 10;

    /**
     * It is the number of columns of the {@link TextBox} which contains the description of the goals (common,
     * personal and final).
     */
    private static final int GOALS_DESCRIPTION_TEXT_BOX_COLUMNS = 30;

    /**
     * It is the {@link TextBox} which contains the description of the first common goal.
     */
    private final TextBox firstCommonGoalDescriptionTextBox = new TextBox().hideCursor();

    /**
     * It is the {@link GoalDrawable} which allows to display the first common goal.
     */
    private final GoalDrawable firstCommonGoalDrawable = new GoalDrawable();

    /**
     * It is the {@link TextBox} which contains the available points which can be obtained by completing the first
     * common goal.
     */
    private final TextBox firstCommonGoalPointsTextBox = new TextBox().unfocusable();

    /**
     * It is the {@link BlurrableDrawable} which arranges in a layout the first common goal description, display
     * and available points.
     * It can be blurred when the completed goal is not the first common goal.
     */
    private final BlurrableDrawable blurrableFirstCommonGoalDrawable = new OrientedLayout(Orientation.VERTICAL,
                        new TextBox().text("First common goal").unfocusable().center().weight(1),
                        new OrientedLayout(Orientation.HORIZONTAL,
                            firstCommonGoalDrawable.center().weight(1),
                            firstCommonGoalDescriptionTextBox.center()
                                .crop()
                                .fixSize(
                                    new DrawableSize(
                                        GOALS_DESCRIPTION_TEXT_BOX_LINES,
                                        GOALS_DESCRIPTION_TEXT_BOX_COLUMNS
                                )).weight(3)).weight(4),
                        firstCommonGoalPointsTextBox.center().weight(1)
                    ).addBorderBox().blurrable();

    /**
     * It is the {@link TextBox} which contains the description of the second common goal.
     */
    private final TextBox secondCommonGoalDescriptionTextBox = new TextBox().hideCursor();

    /**
     * It is the {@link GoalDrawable} which allows to display the second common goal.
     */
    private final GoalDrawable secondCommonGoalDrawable = new GoalDrawable();

    /**
     * It is the {@link TextBox} which contains the available points which can be obtained by completing the second
     * common goal.
     */
    private final TextBox secondCommonGoalPointsTextBox = new TextBox().unfocusable();

    /**
     * It is the {@link BlurrableDrawable} which arranges in a layout the second common goal description, display
     * and available points.
     * It can be blurred when the completed goal is not the second common goal.
     */
    private final BlurrableDrawable blurrableSecondCommonGoalDrawable = new OrientedLayout(Orientation.VERTICAL,
                        new TextBox().text("Second common goal").unfocusable().center().weight(1),
                        new OrientedLayout(Orientation.HORIZONTAL,
                            secondCommonGoalDrawable.center().weight(1),
                            secondCommonGoalDescriptionTextBox.center()
                                .crop()
                                .fixSize(
                                    new DrawableSize(
                                        GOALS_DESCRIPTION_TEXT_BOX_LINES,
                                        GOALS_DESCRIPTION_TEXT_BOX_COLUMNS
                                )).weight(3)).weight(4),
                        secondCommonGoalPointsTextBox.center().weight(1)
                    ).addBorderBox().blurrable();

    /**
     * It is the {@link GoalDrawable} which allows to display the personal goal.
     */
    private final GoalDrawable personalGoalDrawable = new GoalDrawable();

    /**
     * It is a {@link TextBox} which explains the points that can be obtained by matching a certain number of tiles
     * of the personal goal.
     */
    private final TextBox personalGoalPointsTextBox = new TextBox()
        .text(Stream.of(1, 2, 3, 4, 5, 6)
            .map(String::valueOf)
            .collect(Collectors.joining(PrimitiveSymbol.VERTICAL_BOX_BORDER.asString())) + "\n" +
                Collections.nCopies(6, PrimitiveSymbol.HORIZONTAL_BOX_BORDER.asString())
                    .stream().map(String::valueOf)
                    .collect(Collectors.joining(PrimitiveSymbol.CROSS.asString())) +
                        PrimitiveSymbol.HORIZONTAL_BOX_BORDER.asString() + "\n" +
                        Stream.of(1, 2, 4, 6, 9, 12)
                            .map(String::valueOf)
                            .collect(Collectors.joining(PrimitiveSymbol.VERTICAL_BOX_BORDER.asString())))
        .hideCursor();

    /**
     * It is the {@link BlurrableDrawable} which arranges in a layout the personal goal display and available points.
     * It can be blurred when the completed goal is not the personal goal.
     */
    private final BlurrableDrawable blurrablePersonalGoalDrawable = new OrientedLayout(Orientation.VERTICAL,
                        new TextBox().text("Personal goal").unfocusable().center().weight(1),
                        new OrientedLayout(Orientation.HORIZONTAL,
                            personalGoalDrawable.center().weight(1),
                            personalGoalPointsTextBox.center()
                                .crop()
                                .fixSize(
                                    new DrawableSize(
                                        GOALS_DESCRIPTION_TEXT_BOX_LINES,
                                        GOALS_DESCRIPTION_TEXT_BOX_COLUMNS
                                )).weight(3)).weight(4)
                    ).addBorderBox().blurrable();

    /**
     * It is a {@link TextBox} which explains the points that can be obtained from the final goal by
     * putting a certain number of tiles of the same color in adjacent cells.
     */
    private final TextBox adjacencyGoalDescriptionTextBox = new TextBox()
        .text("Points from adjacent tiles in the bookshelf:\n3 adjacent tiles -> 2 points\n" +
            "4 adjacent tiles -> 3 points\n5 adjacent tiles -> 5 points\n6+ adjacent tiles -> 8 points")
        .hideCursor();

    /**
     * It is the {@link BlurrableDrawable} which puts the final goal description inside a border box.
     * It can be blurred when the completed goal is not the final one.
     */
    private final BlurrableDrawable blurrableAdjacencyGoalDrawable = new OrientedLayout(Orientation.VERTICAL,
                        new TextBox().text("Final goal").unfocusable().center().weight(1),
                        adjacencyGoalDescriptionTextBox.center().weight(4)
                    ).addBorderBox().blurrable();

    /**
     * Populates the points text-boxes of both the first and second common goal according to the values
     * on top of the points stacks in {@link GameLayout#commonGoals}.
     */
    private void populateCommonGoalsPointsTextBoxes() {
        firstCommonGoalPointsTextBox.text("Points: " + (commonGoals[0].getPointStack().isEmpty() ? 0 :
            commonGoals[0].getPointStack().get(commonGoals[0].getPointStack().size() - 1)));

        secondCommonGoalPointsTextBox.text("Points: " + (commonGoals[1].getPointStack().isEmpty() ? 0 :
            commonGoals[1].getPointStack().get(commonGoals[1].getPointStack().size() - 1)));
    }

    /**
     * Populates the description of the first and second common goal according to the descriptions
     * in {@link GameLayout#commonGoals}.
     * Furthermore it populates the common goals points text boxes and the personal goal display.
     */
    private void populateGoalsPanel() {
        firstCommonGoalDescriptionTextBox.text(commonGoals[0].getDescription());
        firstCommonGoalDrawable.populate(commonGoals[0].getDisplay());

        secondCommonGoalDescriptionTextBox.text(commonGoals[1].getDescription());
        secondCommonGoalDrawable.populate(commonGoals[1].getDisplay());

        populateCommonGoalsPointsTextBoxes();

        personalGoalDrawable.populate(personalGoal.getTilesColorMask());
    }

    /**
     * Displays the completion of a goal to the user.
     * In particular it blurs goal displays of all goal kinds different from the one of the goal that has just been
     * completed; it masks the {@link BookshelfDrawable} of the player which has completed the goal according to the
     * {@link it.polimi.ingsw.model.bookshelf.BookshelfMaskSet} of the
     * {@link it.polimi.ingsw.model.bookshelf.BookshelfMask}s that made them score points.
     * Furthermore it updates the {@link GameLayout#scoreBoard}, adding the scored points to the user which has completed
     * the goal. The newly scored points will be displayed in green with a "+", beside the previous points.
     * All other players in the {@link GameLayout#scoreBoard} will be blurred.
     *
     * @param displayableGoal is a {@link DisplayableGoal} which represents the goal that has been completed.
     */
    private void displayGoal(DisplayableGoal displayableGoal) {
        isDisplayingGoal = true;

        boolean blurFirstCommonGoalDrawable = displayableGoal.getType() != DisplayableGoal.GoalType.COMMON ||
            !displayableGoal.isFirstCommonGoal();
        boolean blurSecondCommonGoalDrawable = displayableGoal.getType() != DisplayableGoal.GoalType.COMMON ||
            displayableGoal.isFirstCommonGoal();
        boolean blurPersonalGoalDrawable = displayableGoal.getType() != DisplayableGoal.GoalType.PERSONAL;
        boolean blurAdjacencyGoalDrawable = displayableGoal.getType() != DisplayableGoal.GoalType.ADJACENCY;

        blurrableFirstCommonGoalDrawable.blur(blurFirstCommonGoalDrawable);
        blurrableSecondCommonGoalDrawable.blur(blurSecondCommonGoalDrawable);
        blurrablePersonalGoalDrawable.blur(blurPersonalGoalDrawable);
        blurrableAdjacencyGoalDrawable.blur(blurAdjacencyGoalDrawable);

        for (DisplayablePlayer displayablePlayer : scoreBoard.getDisplayablePlayers()) {
            if (!displayablePlayer.getName().equals(displayableGoal.getPlayerName())) {
                scoreBoard.blur(displayablePlayer.getName());
            }
        }
        scoreBoard.addAdditionalPoints(displayableGoal.getPlayerName(), displayableGoal.getPoints());
        scoreBoardRecyclerDrawable.populate(scoreBoard.getDisplayablePlayers());

        playerNameTextBox.text(displayableGoal.getPlayerName());
        selectedBookshelfIndex = scoreBoard.getDisplayablePlayer(displayableGoal.getPlayerName()).getOriginalIndex();
        previousBookshelfButton.focusable(false);
        bookshelfDrawable.mask(displayableGoal.getMaskSet());
        nextBookshelfButton.focusable(false);
    }

    /**
     * Resets the layout to the original state after a goal completion has been displayed.
     * Furthermore it sums the newly scored points of the player which had completed the goal to its previous points.
     *
     * @see GameLayout#displayGoal(DisplayableGoal)
     */
    private void resetLayoutAfterDisplayingGoal() {
        isDisplayingGoal = false;

        blurrableFirstCommonGoalDrawable.blur(false);
        blurrableSecondCommonGoalDrawable.blur(false);
        blurrablePersonalGoalDrawable.blur(false);
        blurrableAdjacencyGoalDrawable.blur(false);

        for (DisplayablePlayer displayablePlayer : scoreBoard.getDisplayablePlayers()) {
            scoreBoard.unblur(displayablePlayer.getName());
            scoreBoard.sumPoints(displayablePlayer.getName());
        }
        scoreBoardRecyclerDrawable.populate(scoreBoard.getDisplayablePlayers());

        populateBookshelfPanel();
    }

    // Board panel:

    /**
     * It is a {@link TextBox} which displays the username of the player whose turn is the current one.
     */
    private final TextBox turnTextBox = new TextBox().unfocusable();

    /**
     * Populates the turn text-box according to the current player.
     */
    private void populateTurnTextBox() {
        turnTextBox.text("It's " + (scoreBoard.isClientPlaying() ? "your" :
            scoreBoard.getPlayingPlayer().getName() + "'s") + " turn")
            .color(scoreBoard.isClientPlaying() ? Color.GREEN : Color.WHITE);
    }

    /**
     * {@link BoardDrawable} used to display the game board.
     */
    private final BoardDrawable boardDrawable = new BoardDrawable();

    /**
     * {@link PopUpDrawable} used to notify the user when a player has disconnected, the game gets stopped, etc. .
     */
    private final PopUpDrawable boardPopUpDrawable = new PopUpDrawable(boardDrawable.center());

    /**
     * {@link Button} which allows the user to exit from the game.
     */
    private final Button exitButton = new Button("Exit");

    /**
     * {@link Button} which allows the current owner to stop the game.
     */
    private final Button stopGameButton = new Button("Stop game");

    /**
     * Layout element associated to the stop game button, it allows to hide the button if the user
     * is not the current owner of the game by setting its weight to 0.
     */
    private final OrientedLayoutElement stopGameButtonLayoutElement = stopGameButton.center().weight(0);

    /**
     * Populates the {@link GameLayout#boardDrawable} according to the data in {@link BoardView}.
     * If it is not the user's turn, all the tiles on the board will be unselectable,
     * otherwise selectable tiles will depend only on game rules.
     */
    private void populateBoard() {
        boardDrawable.getNonFillTileDrawables().forEach(tileDrawable -> {
            tileDrawable.selected(board.getSelectedCoordinates().contains(
                new Coordinate(tileDrawable.getRowInBoard(), tileDrawable.getColumnInBoard())
            ));

            tileDrawable.selectable(scoreBoard.isClientPlaying() && (board.getSelectableCoordinate().contains(
                new Coordinate(tileDrawable.getRowInBoard(), tileDrawable.getColumnInBoard())
            ) || board.getSelectedCoordinates().contains(
                new Coordinate(tileDrawable.getRowInBoard(), tileDrawable.getColumnInBoard())
            )));

            Tile tile = board.tileAt(
                new Coordinate(tileDrawable.getRowInBoard(), tileDrawable.getColumnInBoard())
            );

            TileColor tileColor = (tile == null) ? TileColor.EMPTY : tile.getColor();

            tileDrawable.color(tileColor);
        });
    }

    // Scoreboard:

    /**
     * It is a Drawable which allows to display a player in the scoreboard.
     * Every player has a position, a name, some points and an "additional points" text-box used to display
     * the newly scored points in green when the player completes a goal.
     */
    private static class DisplayablePlayerDrawable extends FixedLayoutDrawable<BlurrableDrawable> {
        /**
         * It is the {@link TextBox} which displays the current position of the player in the scoreboard.
         */
        private final TextBox positionTextBox = new TextBox().unfocusable();

        /**
         * It is the {@link TextBox} which displays the player name.
         */
        private final TextBox playerNameTextBox = new TextBox().hideCursor();

        /**
         * It is the {@link TextBox} which displays the player points.
         */
        private final TextBox playerPointsTextBox = new TextBox().hideCursor();

        /**
         * It is the {@link TextBox} used to display newly scored points when the player completes a goal.
         * These points are highlighted in green and shown beside the previous points with a "+".
         */
        private final TextBox playerAdditionalPointsTextBox = new TextBox().unfocusable().color(Color.GREEN);

        /**
         * Layout element associated with the "additional points" text-box. It allows to hide it from the drawable
         * by setting its weight to 0. In fact that text-box should be visible only while we are displaying a goal.
         */
        private final OrientedLayoutElement playerAdditionalPointsLayoutElement = playerAdditionalPointsTextBox
            .alignUpLeft().weight(0);

        /**
         * Constructor of the class.
         * It initializes the layout in which all the text-boxes are arranged.
         */
        public DisplayablePlayerDrawable() {
            setLayout(new OrientedLayout(Orientation.HORIZONTAL,
                positionTextBox.center().weight(1),
                playerNameTextBox.center().weight(1),
                new OrientedLayout(Orientation.HORIZONTAL,
                    playerPointsTextBox.alignUpLeft().weight(1),
                    playerAdditionalPointsLayoutElement)
                .center().crop().fixSize(new DrawableSize(1, 15)).center().weight(1))
                .center().crop()
                .fixSize(new DrawableSize(5, 45)).addBorderBox().blurrable()
            );
        }
    }

    /**
     * {@link RecyclerDrawable} used to display the score board which is repopulated dynamically every time a player
     * scores points, disconnects or reconnects.
     */
    private final RecyclerDrawable<DisplayablePlayerDrawable, DisplayablePlayer> scoreBoardRecyclerDrawable =
        new RecyclerDrawable<>(Orientation.VERTICAL, DisplayablePlayerDrawable::new,
            (displayablePlayerDrawable, displayablePlayer) -> {
                displayablePlayerDrawable.positionTextBox.text("# " + displayablePlayer.getPosition());

                displayablePlayerDrawable.playerNameTextBox.text(displayablePlayer.getName())
                    .color(displayablePlayer.isClientPlayer() ? Color.FOCUS
                        : (displayablePlayer.isConnected() ? Color.WHITE : Color.GREY));

                displayablePlayerDrawable.playerPointsTextBox.text("Points: " + displayablePlayer
                    .getPoints());

                if (displayablePlayer.getAdditionalPoints() > 0) {
                    displayablePlayerDrawable.playerAdditionalPointsTextBox.text("+" +
                        displayablePlayer.getAdditionalPoints());
                    displayablePlayerDrawable.playerAdditionalPointsLayoutElement.setWeight(1);
                } else {
                    displayablePlayerDrawable.playerAdditionalPointsLayoutElement.setWeight(0);
                }

                displayablePlayerDrawable.getLayout().blur(displayablePlayer.isBlurred());
            });

    // Bookshelves panel:

    /**
     * It is a {@link TextBox} which displays the username of the player whose bookshelf is being shown.
     */
    private final TextBox playerNameTextBox = new TextBox().unfocusable();

    /**
     * It is a {@link BookshelfDrawable} which allows to display the bookshelf of a player.
     */
    private final BookshelfDrawable bookshelfDrawable = new BookshelfDrawable();

    /**
     * It is a {@link Button} which allows the user to see the bookshelf of the next player (in the original lobby
     * ordering) with respect to the player whose bookshelf is being shown.
     */
    private final Button nextBookshelfButton = new Button(">");

    /**
     * It is a {@link Button} which allows the user to see the bookshelf of the previous player (in the original lobby
     * ordering) with respect to the player whose bookshelf is being shown.
     */
    private final Button previousBookshelfButton = new Button("<");

    /**
     * It is a {@link TextBox} which displays the name of the game.
     */
    private final TextBox gameNameTextBox = new TextBox().unfocusable();

    /**
     * Populates the {@link GameLayout#bookshelfDrawable} according to the data in {@link GameLayout#bookshelves} and
     * the {@link GameLayout#selectedBookshelfIndex}.
     *
     * It also makes the buttons which allows to switch the current bookshelf focusable or not, according to
     * {@link GameLayout#selectedBookshelfIndex}. In fact, if the bookshelf that is being shown is the one of the first
     * player (in the original lobby ordering), there is no previous player whose bookshelf can be shown, hence the
     * previous bookshelf button should be not focusable.
     */
    private void populateBookshelfPanel() {
        previousBookshelfButton.focusable(selectedBookshelfIndex > 0);

        nextBookshelfButton.focusable(selectedBookshelfIndex < bookshelves.size() - 1);

        bookshelfDrawable.focusable(scoreBoard.isClientPlaying() &&
            selectedBookshelfIndex == scoreBoard.getClientPlayer().getOriginalIndex());

        bookshelfDrawable.populate(bookshelves.get(selectedBookshelfIndex));

        playerNameTextBox.text(scoreBoard.getDisplayablePlayer(selectedBookshelfIndex).getName())
            .color(selectedBookshelfIndex == scoreBoard.getClientPlayer().getOriginalIndex() ? Color.FOCUS : Color.WHITE);
    }

    // Data:
    /**
     * {@link NetworkEventTransceiver} which allows to receive events from the server.
     */
    private NetworkEventTransceiver transceiver = null;

    /**
     * It is the {@link PersonalGoal} of the user.
     */
    private PersonalGoal personalGoal;

    /**
     * It is an array with the {@link CommonGoal}s of the current game.
     */
    private CommonGoal[] commonGoals;

    /**
     * It is the {@link BoardView} which represents the current state of the game board.
     */
    private BoardView board;

    /**
     * It is a {@link DisplayableScoreBoard} which represents the current score board of the game.
     */
    private DisplayableScoreBoard scoreBoard;

    /**
     * It is a string with the name of the game.
     */
    private String gameName;

    /**
     * It is the index of the player (in the original lobby ordering) whose bookshelf is being shown.
     */
    private int selectedBookshelfIndex;

    /**
     * It is a list of {@link BookshelfView}s, one for every player in the game.
     * Every bookshelf is constantly updated by the server.
     * The list ordering matches the original lobby ordering.
     */
    private List<BookshelfView> bookshelves;

    /**
     * It is true iff the layout is currently displaying the completion of a goal.
     */
    private boolean isDisplayingGoal;

    /**
     * It is the {@link PopUp} used to notify the user that the game is in pause.
     * In particular it is non-null iff the game is in pause.
     * When a player reconnects this pop-up can be hidden.
     */
    private PopUp waitingForReconnectionsPopUp;

    /**
     * It is true iff the game has been stopped.
     */
    private boolean gameHasBeenStopped;

    // Utilities:
    /**
     * {@link Requester} which allows to perform a synchronous request to join the game.
     * This request is performed on layout setup and ensures that the server doesn't broadcast any update relevant
     * to the game layout before it has set the corresponding receivers.
     */
    private Requester<Response<VoidEventData>, JoinGameEventData> joinGameRequester = null;

    /**
     * {@link Requester} which allows the user to perform a synchronous request to the server in order to select
     * a tile. The request is sent when a non-selected tile is on focus and the user presses enter.
     */
    private Requester<Response<VoidEventData>, SelectTileEventData> selectTileRequester = null;

    /**
     * {@link Requester} which allows the user to perform a synchronous request to the server in order to deselect
     * a tile. The request is sent when a selected tile is on focus and the user presses enter.
     */
    private Requester<Response<VoidEventData>, DeselectTileEventData> deselectTileRequester = null;

    /**
     * {@link Requester} which allows the user to perform a synchronous request to the server in order to insert
     * the selected tiles in the bookshelf. The request is sent when a column in the user bookshelf is on focus and
     * the user presses enter.
     */
    private Requester<Response<VoidEventData>, InsertTileEventData> insertTileRequester = null;

    /**
     * {@link Requester} which allows the current owner of the game to perform a synchronous request to the server
     * to stop it. The request is sent when the stop game button is on focus and the owner presses enter.
     */
    private Requester<Response<VoidEventData>, PauseGameEventData> pauseGameRequester = null;

    /**
     * {@link Requester} which allows the user to perform a synchronous request to the server in order to leave the game.
     * The request is sent when the exit button is on focus and the user presses enter.
     */
    private Requester<Response<VoidEventData>, PlayerExitGameEventData> playerExitGameRequester = null;

    /**
     * {@link EventReceiver} which gets notified when the server broadcasts the initial game state to the player
     * that has just joined.
     */
    private EventReceiver<InitialGameEventData> initialGameReceiver = null;

    /**
     * {@link EventReceiver} which gets notified when the server broadcasts the personal goal of the player
     * that has just joined.
     */
    private EventReceiver<PersonalGoalSetEventData> personalGoalSetReceiver = null;

    /**
     * {@link EventReceiver} which gets notified by the server when another player joins the game.
     */
    private EventReceiver<PlayerHasJoinGameEventData> playerHasJoinGameReceiver = null;

    /**
     * {@link EventReceiver} which gets notified by the server when the game board has been modified.
     */
    private EventReceiver<BoardChangedEventData> boardChangedReceiver = null;

    /**
     * {@link EventReceiver} which gets notified by the server when the bookshelf of one of the players has been
     * modified.
     */
    private EventReceiver<BookshelfHasChangedEventData> bookshelfHasChangedReceiver = null;

    /**
     * {@link EventReceiver} which gets notified by the server when the current player has changed.
     */
    private EventReceiver<CurrentPlayerChangedEventData> currentPlayerChangedReceiver = null;

    /**
     * {@link EventReceiver} which gets notified by the server when one player has completed a common goal.
     */
    private EventReceiver<CommonGoalCompletedEventData> commonGoalCompletedReceiver = null;

    /**
     * {@link EventReceiver} which gets notified by the server when one player is the first who has filled
     * them bookshelf.
     */
    private EventReceiver<FirstFullBookshelfEventData> firstFullBookshelfReceiver = null;

    /**
     * {@link EventReceiver} which gets notified by the server when the game has ended.
     */
    private EventReceiver<GameOverEventData> gameOverReceiver = null;

    /**
     * {@link EventReceiver} which gets notified by the server when a player has disconnected.
     */
    private EventReceiver<PlayerHasDisconnectedEventData> playerHasDisconnectedReceiver = null;

    /**
     * {@link EventReceiver} which gets notified by the server when the game has been put in pause since there is
     * only one playing player.
     */
    private EventReceiver<GameHasBeenPauseEventData> gameHasBeenPauseReceiver = null;

    /**
     * {@link EventReceiver} which gets notified by the server when the game has been stopped by the owner.
     */
    private EventReceiver<GameHasBeenStoppedEventData> gameHasBeenStoppedReceiver = null;

    /**
     * {@link PopUpQueue} used to manage the pop-ups which appear on top of the game board to inform the user tha
     * a player has disconnected, the game has been stopped by the owner, etc. .
     */
    private PopUpQueue boardPopUpQueue;

    // Listeners:

    /**
     * {@link EventListener} invoked when the server broadcasts the initial state of the game to the player
     * that has just joined.
     */
    private final EventListener<InitialGameEventData> initialGameListener = data -> {
        commonGoals = data.gameView().getCommonGoals();

        for (int i = 0; i < data.gameView().getPlayers().size(); i++) {
            scoreBoard.addDisplayablePlayer(data.gameView().getPlayers().get(i), i);
        }
        try {
            scoreBoard.setPlayingPlayerOriginalIndex(data.gameView().getCurrentPlayer().getUsername());
        } catch (IllegalFlowException e) {
            throw new IllegalStateException("It should be always possible to retrieve playing player");
        }
        populateTurnTextBox();
        scoreBoardRecyclerDrawable.populate(scoreBoard.getDisplayablePlayers());

        board = data.gameView().getBoard();
        populateBoard();

        stopGameButton.focusable(true);
        if (data.gameView().getOwner()
            .equals(scoreBoard.getClientPlayer().getName())) {

            stopGameButtonLayoutElement.setWeight(2);
        }
        exitButton.focusable(true);

        gameName = data.gameView().getName();
        gameNameTextBox.text("Game: " + gameName);

        selectedBookshelfIndex = scoreBoard.getClientPlayer().getOriginalIndex();
        bookshelves = new ArrayList<>(data.gameView().getPlayers().stream().map(Player::getBookshelf)
            .toList());
        populateBookshelfPanel();

        if (data.gameView().isWaitingForReconnections()) {
            waitForReconnections();
        }

        if (data.gameView().isStopped()) {
            stopGame();
        }
    };

    /**
     * {@link EventListener} invoked when the server sends the personal goal to the player that has just joined.
     */
    private final EventListener<PersonalGoalSetEventData> personalGoalSetListener = data -> {
        personalGoal = PersonalGoal.fromIndex(data.personalGoal());

        populateGoalsPanel();
    };

    /**
     * {@link EventListener} invoked when the server notifies that another player has just joined the game.
     */
    private final EventListener<PlayerHasJoinGameEventData> playerHasJoinGameListener = data -> {
        scoreBoard.setConnectionState(data.username(), true);
        scoreBoardRecyclerDrawable.populate(scoreBoard.getDisplayablePlayers());

        if (waitingForReconnectionsPopUp != null) {
            waitingForReconnectionsPopUp.askToHide();
            waitingForReconnectionsPopUp = null;
        }

        if (data.owner().equals(scoreBoard.getClientPlayer().getName())) {
            stopGameButtonLayoutElement.setWeight(2);
        } else {
            stopGameButtonLayoutElement.setWeight(0);
        }
    };

    /**
     * {@link EventListener} invoked when the server notifies that the game board has been modified.
     */
    private final EventListener<BoardChangedEventData> boardChangedListener = data -> {
        board = data.board();

        populateBoard();
    };

    /**
     * {@link EventListener} invoked when the server notifies that the bookshelf of one of the players has been
     * modified.
     */
    private final EventListener<BookshelfHasChangedEventData> bookshelfHasChangedListener = data -> {
        bookshelves.set(scoreBoard.getDisplayablePlayer(data.username()).getOriginalIndex(), data.bookshelf());

        if (!isDisplayingGoal) {
            populateBookshelfPanel();
        }
    };

    /**
     * {@link EventListener} invoked when the server notifies that the current player has changed.
     */
    private final EventListener<CurrentPlayerChangedEventData> currentPlayerChangedListener = data -> {
        scoreBoard.setPlayingPlayerOriginalIndex(data.getUsername());

        populateTurnTextBox();

        populateBoard();

        if (!isDisplayingGoal) {
            populateBookshelfPanel();
        }
    };

    /**
     * {@link EventListener} invoked when the server notifies that a player has completed a common goal.
     */
    private final EventListener<CommonGoalCompletedEventData> commonGoalCompletedListener = data -> {
        DisplayableGoal displayableGoal = new DisplayableGoal(data.getPlayer().getUsername(),
            DisplayableGoal.GoalType.COMMON, data.getCommonGoalCompleted() == commonGoals[0].getIndex(),
            data.getPoints(), data.getBookshelfMaskSet());

        boardPopUpQueue.add(displayableGoal.toText(),
            popUp -> {
                synchronized (getLock()) {
                    List<Integer> newPointStack = commonGoals[displayableGoal.isFirstCommonGoal() ? 0
                        : 1].getPointStack();
                    newPointStack.remove(newPointStack.size() - 1);
                    commonGoals[displayableGoal.isFirstCommonGoal() ? 0
                        : 1].setPointStack(newPointStack);

                    populateCommonGoalsPointsTextBoxes();

                    displayGoal(displayableGoal);

                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            popUp.askToHide();
                        }
                    }, 10000);
                }
            },
            popUp -> {
                synchronized (getLock()) {
                    resetLayoutAfterDisplayingGoal();
                }
            });
    };

    /**
     * {@link EventListener} invoked when the server notifies that a player is the one who has filled them bookshelf.
     */
    private final EventListener<FirstFullBookshelfEventData> firstFullBookshelfListener = data -> {
        boardPopUpQueue.add(data.username() + " is the first who filled the bookshelf!",
            popUp -> {
                synchronized (getLock()) {
                    scoreBoard.addAdditionalPoints(data.username(), 1);
                    scoreBoardRecyclerDrawable.populate(scoreBoard.getDisplayablePlayers());

                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            popUp.askToHide();
                        }
                    }, 4000);
                }
            },
            popUp -> {
                synchronized (getLock()) {
                    scoreBoard.sumPoints(data.username());
                    scoreBoardRecyclerDrawable.populate(scoreBoard.getDisplayablePlayers());
                }
            });
    };

    /**
     * {@link EventListener} invoked when the server notifies that the game has ended.
     */
    private final EventListener<GameOverEventData> gameOverListener = data -> {
        for (DisplayablePlayer displayablePlayer : scoreBoard.getDisplayablePlayers()) {
            scoreBoard.setIsWinner(displayablePlayer.getName(),
                data.winners().stream().map(Player::getUsername).anyMatch(playerName ->
                    playerName.equals(displayablePlayer.getName())));
        }

        if (waitingForReconnectionsPopUp != null) {
            waitingForReconnectionsPopUp.askToHide();
            waitingForReconnectionsPopUp = null;

            switchAppLayout(GameOverLayout.NAME);
            return;
        }

        List<DisplayableGoal> displayableGoals = new ArrayList<>();

        // Add final goal
        for (int i = 0; i < scoreBoard.getDisplayablePlayers().size(); i++) {

            if (data.personalGoal().get(i).getKey() > 0) {
                displayableGoals.add(new DisplayableGoal(scoreBoard.getDisplayablePlayer(i).getName(),
                    DisplayableGoal.GoalType.PERSONAL, false,
                    data.personalGoal().get(i).getKey(), data.personalGoal().get(i).getValue()));
            }

            if (data.adjacencyGoal().get(i).getKey() > 0) {
                displayableGoals.add(new DisplayableGoal(scoreBoard.getDisplayablePlayer(i).getName(),
                                    DisplayableGoal.GoalType.ADJACENCY, false,
                                    data.adjacencyGoal().get(i).getKey(), data.adjacencyGoal().get(i).getValue()));
            }
        }

        if (displayableGoals.isEmpty()) {
            switchAppLayout(GameOverLayout.NAME);
            return;
        }

        for (int i = 0; i < displayableGoals.size(); i++) {
            boardPopUpQueue.add(displayableGoals.get(i).toText(), displayableGoals.get(i),
                popUp -> {
                    synchronized (getLock()) {
                        displayGoal((DisplayableGoal) popUp.getData().get());

                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                popUp.askToHide();
                            }
                        }, 10000);
                    }
                },
                (i == displayableGoals.size() - 1) ? popUp -> {
                    synchronized (getLock()) {
                        resetLayoutAfterDisplayingGoal();
                        switchAppLayout(GameOverLayout.NAME);
                    }
                } : popUp -> {
                    synchronized (getLock()) {
                        resetLayoutAfterDisplayingGoal();
                    }
                }
            );
        }
    };

    /**
     * {@link EventListener} invoked when the server notifies that a player has disconnected.
     */
    private final EventListener<PlayerHasDisconnectedEventData> playerHasDisconnectedListener = data -> {
        boardPopUpQueue.add(data.username() + " has disconnected", data.username().toUpperCase() +
            "_HAS_DISCONNECTED",
            PopUp.hideAfter(2000),
            popUp -> {}
        );

        scoreBoard.setConnectionState(data.username(), false);
        scoreBoardRecyclerDrawable.populate(scoreBoard.getDisplayablePlayers());

        if (data.newOwner().equals(scoreBoard.getClientPlayer().getName())) {
            stopGameButtonLayoutElement.setWeight(2);
        } else {
            stopGameButtonLayoutElement.setWeight(0);
        }
    };

    /**
     * {@link EventListener} invoked when the server notifies that the game is in pause since there is only
     * one online player.
     */
    private final EventListener<GameHasBeenPauseEventData> gameHasBeenPauseListener = data -> {
        waitForReconnections();
    };

    /**
     * {@link EventListener} invoked when the server notifies that the game has just been stopped by the owner.
     */
    private final EventListener<GameHasBeenStoppedEventData> gameHasBeenStoppedListener = data -> {
        stopGame();
    };

    /**
     * Adds the "waiting for reconnection" pop-up to {@link GameLayout#boardPopUpQueue} to inform the user that the
     * game is on pause. When the pop-up is displayed it checks that the player hasn't already reconnected,
     * if this is not the case the pop-up is hidden.
     */
    public void waitForReconnections() {
        boardPopUpQueue.add("Waiting for reconnection of other players",
            popUp -> {
                synchronized (getLock()) {
                    // The player we are waiting for could reconnect before the pop-up is displayed.
                    if (scoreBoard.getDisplayablePlayers().stream().filter(DisplayablePlayer::isConnected)
                        .count() < 2 && !gameHasBeenStopped) {
                        waitingForReconnectionsPopUp = popUp;
                    } else {
                        popUp.askToHide();
                    }
                }
            },
            popUp -> {});
    }

    /**
     * Adds the "game has been stopped" pop-up to {@link GameLayout#boardPopUpQueue}. After 2 seconds switches the
     * current {@link AppLayout} to {@link AvailableGamesMenuLayout}.
     */
    public void stopGame() {
        gameHasBeenStopped = true;

        if (waitingForReconnectionsPopUp != null) {
            waitingForReconnectionsPopUp.askToHide();
            waitingForReconnectionsPopUp = null;
        }

        boardPopUpQueue.add("Game has been stopped",
            popUp -> {
                synchronized (getLock()) {
                    stopGameButton.focusable(false);
                    exitButton.focusable(false);

                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            popUp.askToHide();
                        }
                    }, 2000);
                }
            },
            popUp -> {
                synchronized (getLock()) {
                    switchAppLayout(AvailableGamesMenuLayout.NAME);
                }
            });
    }

    /**
     * Constructor of the class.
     * It initializes the layout in which all the elements are arranged and sets the required {@link Button}s
     * callbacks.
     */
    public GameLayout() {
        setLayout(new OrientedLayout(Orientation.HORIZONTAL,
                new OrientedLayout(Orientation.VERTICAL,
                    blurrableFirstCommonGoalDrawable.weight(1),
                    blurrableSecondCommonGoalDrawable.weight(1),
                    blurrablePersonalGoalDrawable.weight(1),
                    blurrableAdjacencyGoalDrawable.weight(1)
                ).alignUpLeft().scrollable().weight(3),
                new OrientedLayout(Orientation.VERTICAL,
                    turnTextBox.center().weight(2),
                    boardPopUpDrawable.weight(15),
                    new OrientedLayout(Orientation.HORIZONTAL,
                        new Fill(PrimitiveSymbol.EMPTY).weight(1),
                        stopGameButtonLayoutElement,
                        exitButton.center().weight(2),
                        new Fill(PrimitiveSymbol.EMPTY).weight(1)
                    ).weight(3)
                ).weight(2),
                new OrientedLayout(Orientation.VERTICAL,
                    new Fill(PrimitiveSymbol.EMPTY).weight(1),
                    gameNameTextBox.center().weight(2),
                    new Fill(PrimitiveSymbol.EMPTY).weight(1),
                    scoreBoardRecyclerDrawable.center().scrollable().weight(12),
                    new OrientedLayout(Orientation.VERTICAL,
                        playerNameTextBox.center().weight(1),
                        new OrientedLayout(Orientation.HORIZONTAL,
                            new Fill(PrimitiveSymbol.EMPTY).weight(1),
                            previousBookshelfButton.center().weight(2),
                            bookshelfDrawable.center().weight(12),
                            nextBookshelfButton.center().weight(2),
                            new Fill(PrimitiveSymbol.EMPTY).weight(1)
                        ).weight(12)
                    ).center().addBorderBox().weight(20)
                ).weight(2)
            ).center().crop()
        );

        setData(new AppLayoutData(
            Map.of(
                "scoreboard", () -> scoreBoard
            )
        ));

        nextBookshelfButton.onpress(() -> {
            selectedBookshelfIndex++;

            populateBookshelfPanel();
        });

        previousBookshelfButton.onpress(() -> {
           selectedBookshelfIndex--;

           populateBookshelfPanel();
        });

        boardDrawable.getNonFillTileDrawables().forEach(tileDrawable -> tileDrawable.onselect(
            (rowInBoard, columnInBoard) -> {
                try {
                    displayServerResponse(selectTileRequester.request(new SelectTileEventData(
                        new Coordinate(rowInBoard, columnInBoard))));
                } catch (DisconnectedException e) {
                    displayServerResponse(Response.failure("Disconnected!"));
                }
            }));

        boardDrawable.getNonFillTileDrawables().forEach(tileDrawable -> tileDrawable.ondeselect(
            (rowInBoard, columnInBoard) -> {
                try {
                    displayServerResponse(deselectTileRequester.request(new DeselectTileEventData(
                        new Coordinate(rowInBoard, columnInBoard)
                    )));
                } catch (DisconnectedException e) {
                    displayServerResponse(Response.failure("Disconnected!"));
                }
            }));

        for (int column = 0; column < Bookshelf.COLUMNS; column++) {
            bookshelfDrawable.getColumn(column).onselect(c -> {
                try {
                    displayServerResponse(insertTileRequester.request(new InsertTileEventData(c)));
                } catch (DisconnectedException e) {
                    displayServerResponse(Response.failure("Disconnected!"));
                }
            });
        }

        stopGameButton.onpress(() -> {
            try {
                displayServerResponse(pauseGameRequester.request(new PauseGameEventData()));
            } catch (DisconnectedException e) {
                displayServerResponse(Response.failure("Disconnected!"));
            }
        });

        exitButton.onpress(() -> {
            try {
                Response response = playerExitGameRequester.request(new PlayerExitGameEventData());

                if (!response.isOk()) {
                    displayServerResponse(response);
                    return;
                }

                if (waitingForReconnectionsPopUp != null) {
                    waitingForReconnectionsPopUp.askToHide();
                    waitingForReconnectionsPopUp = null;
                }

                switchAppLayout(AvailableGamesMenuLayout.NAME);
            } catch (DisconnectedException e) {
                displayServerResponse(Response.failure("Disconnected!"));
            }
        });
    }

    @Override
    public void setup(String previousLayoutName) {
        if (!previousLayoutName.equals(LobbyLayout.NAME) &&
            !previousLayoutName.equals(AvailableGamesMenuLayout.NAME)) {
            throw new IllegalStateException("You can reach GameLayout only from LobbyLayout or" +
                " AvailableGamesMenuLayout");
        }

        isDisplayingGoal = false;
        waitingForReconnectionsPopUp = null;

        exitButton.focusable(false);
        stopGameButton.focusable(false);
        previousBookshelfButton.focusable(false);
        nextBookshelfButton.focusable(false);
        bookshelfDrawable.focusable(false);

        if (transceiver == null) {
            transceiver = (NetworkEventTransceiver) appDataProvider.get(ConnectionMenuLayout.NAME, "transceiver");

            joinGameRequester = Response.requester(transceiver, transceiver, getLock());
            selectTileRequester = Response.requester(transceiver, transceiver, getLock());
            deselectTileRequester = Response.requester(transceiver, transceiver, getLock());
            insertTileRequester = Response.requester(transceiver, transceiver, getLock());
            pauseGameRequester = Response.requester(transceiver, transceiver, getLock());
            playerExitGameRequester = Response.requester(transceiver, transceiver, getLock());

            initialGameReceiver = InitialGameEventData.castEventReceiver(transceiver);
            personalGoalSetReceiver = PersonalGoalSetEventData.castEventReceiver(transceiver);
            playerHasJoinGameReceiver = PlayerHasJoinGameEventData.castEventReceiver(transceiver);
            boardChangedReceiver = BoardChangedEventData.castEventReceiver(transceiver);
            bookshelfHasChangedReceiver = BookshelfHasChangedEventData.castEventReceiver(transceiver);
            currentPlayerChangedReceiver = CurrentPlayerChangedEventData.castEventReceiver(transceiver);
            commonGoalCompletedReceiver = CommonGoalCompletedEventData.castEventReceiver(transceiver);
            firstFullBookshelfReceiver = FirstFullBookshelfEventData.castEventReceiver(transceiver);
            gameOverReceiver = GameOverEventData.castEventReceiver(transceiver);
            playerHasDisconnectedReceiver = PlayerHasDisconnectedEventData.castEventReceiver(transceiver);
            gameHasBeenPauseReceiver = GameHasBeenPauseEventData.castEventReceiver(transceiver);
            gameHasBeenStoppedReceiver = GameHasBeenStoppedEventData.castEventReceiver(transceiver);


            PlayerDisconnectedInternalEventData.castEventReceiver(transceiver).registerListener(data -> {
                transceiver = null;

                joinGameRequester = null;
                selectTileRequester = null;
                deselectTileRequester = null;
                insertTileRequester = null;
                pauseGameRequester = null;
                playerExitGameRequester = null;

                initialGameReceiver = null;
                personalGoalSetReceiver = null;
                playerHasJoinGameReceiver = null;
                boardChangedReceiver = null;
                bookshelfHasChangedReceiver = null;
                currentPlayerChangedReceiver = null;
                commonGoalCompletedReceiver = null;
                firstFullBookshelfReceiver = null;
                gameOverReceiver = null;
                playerHasDisconnectedReceiver = null;
                gameHasBeenPauseReceiver = null;
                gameHasBeenStoppedReceiver = null;


                if (isCurrentLayout()) {
                    switchAppLayout(ConnectionMenuLayout.NAME);
                }
            });
        }

        scoreBoard = new DisplayableScoreBoard(appDataProvider.getString(
            LoginMenuLayout.NAME,
            "username"
        ));

        joinGameRequester.registerAllListeners();
        selectTileRequester.registerAllListeners();
        deselectTileRequester.registerAllListeners();
        insertTileRequester.registerAllListeners();
        pauseGameRequester.registerAllListeners();
        playerExitGameRequester.registerAllListeners();

        initialGameReceiver.registerListener(initialGameListener);
        personalGoalSetReceiver.registerListener(personalGoalSetListener);
        playerHasJoinGameReceiver.registerListener(playerHasJoinGameListener);
        boardChangedReceiver.registerListener(boardChangedListener);
        bookshelfHasChangedReceiver.registerListener(bookshelfHasChangedListener);
        currentPlayerChangedReceiver.registerListener(currentPlayerChangedListener);
        commonGoalCompletedReceiver.registerListener(commonGoalCompletedListener);
        firstFullBookshelfReceiver.registerListener(firstFullBookshelfListener);
        gameOverReceiver.registerListener(gameOverListener);
        playerHasDisconnectedReceiver.registerListener(playerHasDisconnectedListener);
        gameHasBeenPauseReceiver.registerListener(gameHasBeenPauseListener);
        gameHasBeenStoppedReceiver.registerListener(gameHasBeenStoppedListener);

        boardPopUpQueue = new PopUpQueue(
            text -> {
                synchronized (getLock()) {
                    boardPopUpDrawable.displayPopUp(text);
                }
            },
            () -> {
                synchronized (getLock()) {
                    boardPopUpDrawable.hidePopUp();
                }
            }, getLock());
        boardPopUpDrawable.hidePopUp();

        Response response;

        try {
            response = joinGameRequester.request(new JoinGameEventData(
                appDataProvider.getString(AvailableGamesMenuLayout.NAME, "selectedgame")));

            displayServerResponse(response);

            if (!response.isOk()) {
                switchAppLayout(AvailableGamesMenuLayout.NAME);
            }
        } catch (DisconnectedException e) {
            displayServerResponse(Response.failure("Disconnected!"));
        }

        gameHasBeenStopped = false;
    }

    @Override
    public void beforeSwitch() {
        if (transceiver != null) {
            joinGameRequester.unregisterAllListeners();
            selectTileRequester.unregisterAllListeners();
            deselectTileRequester.unregisterAllListeners();
            insertTileRequester.unregisterAllListeners();
            pauseGameRequester.unregisterAllListeners();
            playerExitGameRequester.unregisterAllListeners();

            initialGameReceiver.unregisterListener(initialGameListener);
            personalGoalSetReceiver.unregisterListener(personalGoalSetListener);
            boardChangedReceiver.unregisterListener(boardChangedListener);
            bookshelfHasChangedReceiver.unregisterListener(bookshelfHasChangedListener);
            currentPlayerChangedReceiver.unregisterListener(currentPlayerChangedListener);
            commonGoalCompletedReceiver.unregisterListener(commonGoalCompletedListener);
            firstFullBookshelfReceiver.unregisterListener(firstFullBookshelfListener);
            gameOverReceiver.unregisterListener(gameOverListener);
            playerHasDisconnectedReceiver.unregisterListener(playerHasDisconnectedListener);
            gameHasBeenStoppedReceiver.unregisterListener(gameHasBeenStoppedListener);
            playerHasJoinGameReceiver.unregisterListener(playerHasJoinGameListener);
            gameHasBeenPauseReceiver.unregisterListener(gameHasBeenPauseListener);
        }

        boardPopUpQueue.disable();
        boardPopUpQueue = null;
    }

    @Override
    public String getName() {
        return NAME;
    }
}
