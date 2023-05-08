package it.polimi.ingsw.view.tui;

import it.polimi.ingsw.controller.Response;
import it.polimi.ingsw.controller.ResponseStatus;
import it.polimi.ingsw.event.NetworkEventTransceiver;
import it.polimi.ingsw.event.Requester;
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

public class GameLayout extends AppLayout {
    public static final String NAME = "GAME";

    // Layout:
    // Goals panel:
    private static final int GOALS_DESCRIPTION_TEXT_BOX_LINES = 10;
    private static final int GOALS_DESCRIPTION_TEXT_BOX_COLUMNS = 30;

    private final TextBox firstCommonGoalDescriptionTextBox = new TextBox().hideCursor();
    private final GoalDrawable firstCommonGoalDrawable = new GoalDrawable();
    private final TextBox firstCommonGoalPointsTextBox = new TextBox().unfocusable();
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

    private final TextBox secondCommonGoalDescriptionTextBox = new TextBox().hideCursor();
    private final GoalDrawable secondCommonGoalDrawable = new GoalDrawable();
    private final TextBox secondCommonGoalPointsTextBox = new TextBox().unfocusable();
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

    private final GoalDrawable personalGoalDrawable = new GoalDrawable();
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

    private final TextBox adjacencyGoalDescriptionTextBox = new TextBox()
        .text("Points from adjacent tiles in the bookshelf:\n3 adjacent tiles -> 2 points\n" +
            "4 adjacent tiles -> 3 points\n5 adjacent tiles -> 5 points\n6+ adjacent tiles -> 8 points")
        .hideCursor();
    private final BlurrableDrawable blurrableAdjacencyGoalDrawable = new OrientedLayout(Orientation.VERTICAL,
                        new TextBox().text("Final goal").unfocusable().center().weight(1),
                        adjacencyGoalDescriptionTextBox.center().weight(4)
                    ).addBorderBox().blurrable();

    private void populateGoalsPanel() {
        firstCommonGoalDescriptionTextBox.text(commonGoals[0].getDescription());
        firstCommonGoalDrawable.populate(commonGoals[0].getDisplay());
        firstCommonGoalPointsTextBox.text("Points: " + (commonGoals[0].getPointStack().size() == 0 ? 0 :
            commonGoals[0].getPointStack().get(commonGoals[0].getPointStack().size() - 1)));

        secondCommonGoalDescriptionTextBox.text(commonGoals[1].getDescription());
        secondCommonGoalDrawable.populate(commonGoals[1].getDisplay());
        secondCommonGoalPointsTextBox.text("Points: " + (commonGoals[1].getPointStack().size() == 0 ? 0 :
            commonGoals[1].getPointStack().get(commonGoals[1].getPointStack().size() - 1)));

        personalGoalDrawable.populate(personalGoal.getTilesColorMask());
    }

    private void displayGoal(DisplayableGoal displayableGoal) {
        isDisplayingCommonGoal = true;

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

    private void resetLayoutAfterDisplayingGoal() {
        isDisplayingCommonGoal = false;

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
    private final TextBox turnTextBox = new TextBox().unfocusable();

    private void populateTurnTextBox() {
        turnTextBox.text("It's " + (scoreBoard.isClientPlaying() ? "your" :
            scoreBoard.getPlayingPlayer().getName() + "'s") + " turn")
            .color(scoreBoard.isClientPlaying() ? Color.GREEN : Color.WHITE);
    }

    private final BoardDrawable boardDrawable = new BoardDrawable();
    private final PopUpDrawable boardPopUpDrawable = new PopUpDrawable(boardDrawable.center());

    private final Button exitButton = new Button("Exit");
    private final Button stopGameButton = new Button("Stop game");
    private final OrientedLayoutElement stopGameButtonLayoutElement = stopGameButton.center().weight(0);

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
    private static class DisplayablePlayerDrawable extends FixedLayoutDrawable<BlurrableDrawable> {
        private final TextBox positionTextBox = new TextBox().unfocusable();
        private final TextBox playerNameTextBox = new TextBox().hideCursor();
        private final TextBox playerPointsTextBox = new TextBox().hideCursor();
        private final TextBox playerAdditionalPointsTextBox = new TextBox().unfocusable().color(Color.GREEN);
        private final OrientedLayoutElement playerAdditionalPointsLayoutElement = playerAdditionalPointsTextBox
            .alignUpLeft().weight(0);

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
    private final TextBox playerNameTextBox = new TextBox().unfocusable();
    private final BookshelfDrawable bookshelfDrawable = new BookshelfDrawable();
    private final Button nextBookshelfButton = new Button(">");
    private final Button previousBookshelfButton = new Button("<");
    private final TextBox gameNameTextBox = new TextBox().unfocusable();

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
    private NetworkEventTransceiver transceiver = null;
    private PersonalGoal personalGoal;
    private CommonGoal[] commonGoals;
    private BoardView board;
    private DisplayableScoreBoard scoreBoard;
    private String gameName;
    private int selectedBookshelfIndex;
    private List<BookshelfView> bookshelves;

    private boolean isDisplayingCommonGoal;

    private PopUp waitingForReconnectionsPopUp;

    // Utilities:
    private Requester<Response, JoinGameEventData> joinGameRequester = null;
    private Requester<Response, SelectTileEventData> selectTileRequester = null;
    private Requester<Response, DeselectTileEventData> deselectTileRequester = null;
    private Requester<Response, InsertTileEventData> insertTileRequester = null;
    private Requester<Response, PauseGameEventData> pauseGameRequester = null;
    private Requester<Response, PlayerExitGame> playerExitGameRequester = null;

    private EventReceiver<InitialGameEventData> initialGameReceiver = null;
    private EventReceiver<PersonalGoalSetEventData> personalGoalSetReceiver = null;
    private EventReceiver<PlayerHasJoinGameEventData> playerHasJoinGameReceiver = null;
    private EventReceiver<BoardChangedEventData> boardChangedReceiver = null;
    private EventReceiver<BookshelfHasChangedEventData> bookshelfHasChangedReceiver = null;
    private EventReceiver<CurrentPlayerChangedEventData> currentPlayerChangedReceiver = null;
    private EventReceiver<CommonGoalCompletedEventData> commonGoalCompletedReceiver = null;
    private EventReceiver<FirstFullBookshelfEventData> firstFullBookshelfReceiver = null;
    private EventReceiver<GameOverEventData> gameOverReceiver = null;
    private EventReceiver<PlayerHasDisconnectedEventData> playerHasDisconnectedReceiver = null;
    private EventReceiver<GameHasBeenPauseEventData> gameHasBeenPauseReceiver = null;
    private EventReceiver<GameHasBeenStoppedEventData> gameHasBeenStoppedReceiver = null;

    private final PopUpQueue boardPopUpQueue = new PopUpQueue(
        text -> {
            synchronized (getLock()) {
                boardPopUpDrawable.displayPopUp(text);
            }
        },
        () -> {
            synchronized (getLock()) {
                boardPopUpDrawable.hidePopUp();
            }
        });

    // Listeners:
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

    private final EventListener<PersonalGoalSetEventData> personalGoalSetListener = data -> {
        personalGoal = PersonalGoal.fromIndex(data.personalGoal());

        populateGoalsPanel();
    };

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

    private final EventListener<BoardChangedEventData> boardChangedListener = data -> {
        board = data.board();

        populateBoard();
    };

    private final EventListener<BookshelfHasChangedEventData> bookshelfHasChangedListener = data -> {
        bookshelves.set(scoreBoard.getDisplayablePlayer(data.username()).getOriginalIndex(), data.bookshelf());

        if (!isDisplayingCommonGoal) {
            populateBookshelfPanel();
        }
    };

    private final EventListener<CurrentPlayerChangedEventData> currentPlayerChangedListener = data -> {
        scoreBoard.setPlayingPlayerOriginalIndex(data.getUsername());

        populateTurnTextBox();

        populateBoard();

        if (!isDisplayingCommonGoal) {
            populateBookshelfPanel();
        }
    };

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

    private final EventListener<PlayerHasDisconnectedEventData> playerHasDisconnectedListener = data -> {
        boardPopUpQueue.add(data.username() + " has disconnected", data.username().toUpperCase() +
            "_HAS_DISCONNECTED",
            popUp -> {
                synchronized (getLock()) {
                    scoreBoard.setConnectionState(data.username(), false);
                    scoreBoardRecyclerDrawable.populate(scoreBoard.getDisplayablePlayers());

                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            popUp.askToHide();
                        }
                    }, 2000);
                }
            },
            popUp -> {}
        );

        if (data.newOwner().equals(scoreBoard.getClientPlayer().getName())) {
            stopGameButtonLayoutElement.setWeight(2);
        } else {
            stopGameButtonLayoutElement.setWeight(0);
        }
    };

    private final EventListener<GameHasBeenPauseEventData> gameHasBeenPauseListener = data -> {
        waitForReconnections();
    };

    private final EventListener<GameHasBeenStoppedEventData> gameHasBeenStoppedListener = data -> {
        stopGame();
    };

    public void waitForReconnections() {
        boardPopUpQueue.add("Waiting for reconnection of other players",
            popUp -> {
                synchronized (getLock()) {
                    waitingForReconnectionsPopUp = popUp;
                }
            },
            popUp -> {});
    }

    public void stopGame() {
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
                    displayServerResponse(new Response("Disconnected!", ResponseStatus.FAILURE));
                }
            }));

        boardDrawable.getNonFillTileDrawables().forEach(tileDrawable -> tileDrawable.ondeselect(
            (rowInBoard, columnInBoard) -> {
                try {
                    displayServerResponse(deselectTileRequester.request(new DeselectTileEventData(
                        new Coordinate(rowInBoard, columnInBoard)
                    )));
                } catch (DisconnectedException e) {
                    displayServerResponse(new Response("Disconnected!", ResponseStatus.FAILURE));
                }
            }));

        for (int column = 0; column < Bookshelf.COLUMNS; column++) {
            bookshelfDrawable.getColumn(column).onselect(c -> {
                try {
                    displayServerResponse(insertTileRequester.request(new InsertTileEventData(c)));
                } catch (DisconnectedException e) {
                    displayServerResponse(new Response("Disconnected!", ResponseStatus.FAILURE));
                }
            });
        }

        stopGameButton.onpress(() -> {
            try {
                displayServerResponse(pauseGameRequester.request(new PauseGameEventData()));
            } catch (DisconnectedException e) {
                displayServerResponse(new Response("Disconnected!", ResponseStatus.FAILURE));
            }
        });

        exitButton.onpress(() -> {
            try {
                Response response = playerExitGameRequester.request(new PlayerExitGame());

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
                displayServerResponse(new Response("Disconnected!", ResponseStatus.FAILURE));
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

        isDisplayingCommonGoal = false;
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

        boardPopUpQueue.enable();

        Response response;

        try {
            response = joinGameRequester.request(new JoinGameEventData(
                appDataProvider.getString(AvailableGamesMenuLayout.NAME, "selectedgame")));

            displayServerResponse(response);

            if (!response.isOk()) {
                switchAppLayout(AvailableGamesMenuLayout.NAME);
            }
        } catch (DisconnectedException e) {
            displayServerResponse(new Response("Disconnected!", ResponseStatus.FAILURE));
        }
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
    }

    @Override
    public String getName() {
        return NAME;
    }
}
