package it.polimi.ingsw.view.tui;

import it.polimi.ingsw.controller.Response;
import it.polimi.ingsw.controller.ResponseStatus;
import it.polimi.ingsw.event.NetworkEventTransceiver;
import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.data.client.*;
import it.polimi.ingsw.event.data.game.*;
import it.polimi.ingsw.event.data.internal.PlayerDisconnectedInternalEventData;
import it.polimi.ingsw.model.board.BoardView;
import it.polimi.ingsw.model.bookshelf.Bookshelf;
import it.polimi.ingsw.model.bookshelf.BookshelfMaskSet;
import it.polimi.ingsw.model.game.IllegalFlowException;
import it.polimi.ingsw.model.game.Player;
import it.polimi.ingsw.model.goal.CommonGoal;
import it.polimi.ingsw.model.goal.PersonalGoal;
import it.polimi.ingsw.model.tile.Tile;
import it.polimi.ingsw.model.tile.TileColor;
import it.polimi.ingsw.networking.DisconnectedException;
import it.polimi.ingsw.utils.Coordinate;
import it.polimi.ingsw.view.tui.terminal.Terminal;
import it.polimi.ingsw.view.tui.terminal.drawable.*;
import it.polimi.ingsw.view.tui.terminal.drawable.app.AppLayout;
import it.polimi.ingsw.view.tui.terminal.drawable.app.AppLayoutData;
import it.polimi.ingsw.view.tui.terminal.drawable.menu.Button;
import it.polimi.ingsw.view.tui.terminal.drawable.menu.value.TextBox;
import it.polimi.ingsw.view.tui.terminal.drawable.orientedlayout.OrientedLayout;
import it.polimi.ingsw.view.tui.terminal.drawable.orientedlayout.OrientedLayoutElement;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.Color;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.PrimitiveSymbol;
import it.polimi.ingsw.view.tui.terminal.drawable.twolayers.TwoLayersDrawable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GameLayout extends AppLayout {
    public static final String NAME = "GAME";

    private final TextBox firstCommonGoalDescriptionTextBox = new TextBox().hideCursor();
    private final GoalDrawable firstCommonGoalDrawable = new GoalDrawable();
    private final TextBox firstCommonGoalPointsTextBox = new TextBox().unfocusable();
    private final BlurrableDrawable blurrableFirstCommonGoal = new OrientedLayout(Orientation.VERTICAL,
                        new TextBox().text("First common goal").unfocusable().center().weight(1),
                        new OrientedLayout(Orientation.HORIZONTAL,
                            firstCommonGoalDrawable.center().weight(1),
                            firstCommonGoalDescriptionTextBox.center()
                                .crop().fixSize(new DrawableSize(GOALS_TEXT_BOX_LINES, GOALS_TEXT_BOX_COLUMNS))
                                .weight(3)).weight(4),
                        firstCommonGoalPointsTextBox.center().weight(1)
                    ).addBorderBox().blurrable();
    private final TextBox secondCommonGoalDescriptionTextBox = new TextBox().hideCursor();
    private final GoalDrawable secondCommonGoalDrawable = new GoalDrawable();
    private final TextBox secondCommonGoalPointsTextBox = new TextBox().unfocusable();
    private final BlurrableDrawable blurrableSecondCommonGoal = new OrientedLayout(Orientation.VERTICAL,
                        new TextBox().text("Second common goal").unfocusable().center().weight(1),
                        new OrientedLayout(Orientation.HORIZONTAL,
                            secondCommonGoalDrawable.center().weight(1),
                            secondCommonGoalDescriptionTextBox.center()
                                .crop().fixSize(new DrawableSize(GOALS_TEXT_BOX_LINES, GOALS_TEXT_BOX_COLUMNS))
                                .weight(3)).weight(4),
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
    private final BlurrableDrawable blurrablePersonalGoal = new OrientedLayout(Orientation.VERTICAL,
                        new TextBox().text("Personal goal").unfocusable().center().weight(1),
                        new OrientedLayout(Orientation.HORIZONTAL,
                            personalGoalDrawable.center().weight(1),
                            personalGoalPointsTextBox.center()
                                .crop().fixSize(new DrawableSize(GOALS_TEXT_BOX_LINES, GOALS_TEXT_BOX_COLUMNS))
                                .weight(3)).weight(4)
                    ).addBorderBox().blurrable();
    private final TextBox adjacencyGoalTextBox = new TextBox()
        .text("Points from adjacent tiles in the bookshelf:\n3 adjacent tiles -> 2 points\n" +
            "4 adjacent tiles -> 3 points\n5 adjacent tiles -> 5 points\n6+ adjacent tiles -> 8 points")
        .hideCursor();

    private final BlurrableDrawable blurrableAdjacencyGoal = new OrientedLayout(Orientation.VERTICAL,
                        new TextBox().text("Final goal").unfocusable().center().weight(1),
                        adjacencyGoalTextBox.center().weight(4)
                    ).addBorderBox().blurrable();

    private final TextBox turnTextBox = new TextBox().unfocusable();
    private final BoardDrawable boardDrawable = new BoardDrawable();
    private final BlurrableDrawable blurrableBoard = boardDrawable.center().scrollable().blurrable();
    private final TextBox popUpTextBox = new TextBox().hideCursor();
    private final TwoLayersDrawable twoLayersBoard = new TwoLayersDrawable(
        blurrableBoard,
        popUpTextBox.center().crop()
            .fixSize(new DrawableSize(10, 25)).addBorderBox().center()
    );
    private final Button exitButton = new Button("Exit");
    private final Button stopGameButton = new Button("Stop game");
    private final TextBox playerNameTextBox = new TextBox().unfocusable();
    private final BookshelfDrawable bookshelfDrawable = new BookshelfDrawable();
    private final Button nextBookshelfButton = new Button(">");
    private final Button previousBookshelfButton = new Button("<");
    private final TextBox gameNameTextBox = new TextBox().unfocusable();
    public static class PlayerDisplay {
        public PlayerDisplay(String name, int points, int position,
                             boolean isClientPlayer,
                             boolean blurred) {
            this.position = position;
            this.name = name;
            this.points = points;
            this.isClientPlayer = isClientPlayer;
            this.blurred = blurred;
        }

        public int getPosition() {
            return position;
        }

        public String getName() {
            return name;
        }

        public int getPoints() {
            return points;
        }

        public boolean isClientPlayer() {
            return isClientPlayer;
        }

        private String name;
        private int points;
        private int additionalPoints;
        private int position;
        private boolean isClientPlayer;
        private boolean blurred;
    }

    private static class PlayerDisplayDrawable extends FixedLayoutDrawable<BlurrableDrawable> {
        private final TextBox positionTextBox = new TextBox().unfocusable();
        private final TextBox playerNameTextBox = new TextBox().hideCursor();
        private final TextBox playerPointsTextBox = new TextBox().hideCursor();
        private final TextBox playerAdditionalPointsTextBox = new TextBox().unfocusable().color(Color.GREEN);
        private final OrientedLayoutElement playerAdditionalPointsElement = playerAdditionalPointsTextBox
            .alignUpLeft().weight(0);

        public PlayerDisplayDrawable() {
            setLayout(new OrientedLayout(Orientation.HORIZONTAL,
                positionTextBox.center().weight(1),
                playerNameTextBox.center().weight(1),
                new OrientedLayout(Orientation.HORIZONTAL,
                    playerPointsTextBox.alignUpLeft().weight(1),
                    playerAdditionalPointsElement)
                .center().crop().fixSize(new DrawableSize(1, 15)).center().weight(1))
                .center().crop()
                .fixSize(new DrawableSize(5, 45)).addBorderBox().blurrable()
            );
        }
    }

    private final RecyclerDrawable<PlayerDisplayDrawable, PlayerDisplay> playerDisplayRecyclerDrawable =
        new RecyclerDrawable<>(Orientation.VERTICAL, PlayerDisplayDrawable::new,
            (playerDisplayDrawable, playerDisplay) -> {
                playerDisplayDrawable.positionTextBox.text("# " + String.valueOf(playerDisplay.position));
                playerDisplayDrawable.playerNameTextBox.text(playerDisplay.name)
                    .color(playerDisplay.isClientPlayer ? Color.FOCUS : Color.WHITE);
                playerDisplayDrawable.playerPointsTextBox.text("Points: " + playerDisplay.points);

                if (playerDisplay.additionalPoints > 0) {
                    playerDisplayDrawable.playerAdditionalPointsTextBox.text("+" + playerDisplay.additionalPoints);
                    playerDisplayDrawable.playerAdditionalPointsElement.setWeight(1);
                } else {
                    playerDisplayDrawable.playerAdditionalPointsElement.setWeight(0);
                }

                playerDisplayDrawable.getLayout().blur(playerDisplay.blurred);
            });

    private static final int GOALS_TEXT_BOX_LINES = 10;
    private static final int GOALS_TEXT_BOX_COLUMNS = 30;

    public GameLayout() {
        setLayout(new OrientedLayout(Orientation.HORIZONTAL,
                new OrientedLayout(Orientation.VERTICAL,
                    blurrableFirstCommonGoal.weight(1),
                    blurrableSecondCommonGoal.weight(1),
                    blurrablePersonalGoal.weight(1),
                    blurrableAdjacencyGoal.weight(1)
                ).alignUpLeft().scrollable().weight(3),
                new OrientedLayout(Orientation.VERTICAL,
                    turnTextBox.center().weight(2),
                    twoLayersBoard.weight(15),
                    new OrientedLayout(Orientation.HORIZONTAL,
                        new Fill(PrimitiveSymbol.EMPTY).weight(1),
                        stopGameButton.center().weight(2),
                        exitButton.center().weight(2),
                        new Fill(PrimitiveSymbol.EMPTY).weight(1)
                    ).weight(3)
                ).weight(2),
                new OrientedLayout(Orientation.VERTICAL,
                    new Fill(PrimitiveSymbol.EMPTY).weight(1),
                    gameNameTextBox.center().weight(2),
                    new Fill(PrimitiveSymbol.EMPTY).weight(1),
                    playerDisplayRecyclerDrawable.center().scrollable().weight(12),
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
                "scoreboard", () -> craftPlayerDisplayList()
            )
        ));

        nextBookshelfButton.onpress(() -> {
            selectedBookshelfIndex++;

            populateBookshelfMenu();
        });

        previousBookshelfButton.onpress(() -> {
           selectedBookshelfIndex--;

           populateBookshelfMenu();
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

                switchAppLayout(AvailableGamesMenuLayout.NAME);
            } catch (DisconnectedException e) {
                displayServerResponse(new Response("Disconnected!", ResponseStatus.FAILURE));
            }
        });
    }

    private void populateBookshelfMenu() {
        previousBookshelfButton.focusable(selectedBookshelfIndex > 0);
        nextBookshelfButton.focusable(selectedBookshelfIndex < bookshelves.size() - 1);
        bookshelfDrawable.focusable(playingPlayerIndex == clientPlayerIndex &&
            selectedBookshelfIndex == clientPlayerIndex);
        bookshelfDrawable.populate(bookshelves.get(selectedBookshelfIndex));
        playerNameTextBox.text(playerNames.get(selectedBookshelfIndex))
            .color(selectedBookshelfIndex == clientPlayerIndex ? Color.FOCUS : Color.WHITE);
    }

    private void populateGoalsMenu() {
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

    private void populateTurnTextBox() {
        turnTextBox.text("It's " + (clientPlayerIndex == playingPlayerIndex ? "your" :
            playerNames.get(playingPlayerIndex)) + " turn")
            .color(clientPlayerIndex == playingPlayerIndex ? Color.GREEN : Color.WHITE);
    }

    private void populateBoard(BoardView board) {
        boardDrawable.getNonFillTileDrawables().forEach(tileDrawable -> {
            tileDrawable.selected(board.getSelectedCoordinates().contains(
                new Coordinate(tileDrawable.getRowInBoard(), tileDrawable.getColumnInBoard())
            ));

            tileDrawable.selectable(playingPlayerIndex == clientPlayerIndex && (board.getSelectableCoordinate().contains(
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

    private static class CompletedGoal {
        enum GoalType {
            COMMON,
            PERSONAL,
            ADJACENCY
        }

        public CompletedGoal(GoalType type, int index, String playerName, int points, BookshelfMaskSet maskSet) {
            this.type = type;
            this.index = index;
            this.playerName = playerName;
            this.points = points;
            this.maskSet = maskSet;
        }

        private GoalType type;
        private int index;
        private String playerName;
        private int points;
        private BookshelfMaskSet maskSet;
    }

    private final Queue<CompletedGoal> completedGoals = new ArrayDeque<>();
    private Timer completedGoalTimer = null;

    private boolean isFirstPersonalGoalIndex(int index) {
        return commonGoals[0].getIndex() == index;
    }

    private void displayCompletedGoal(CompletedGoal completedGoal) {
        boolean blurFirstCommonGoalDisplay = completedGoal.type != CompletedGoal.GoalType.COMMON ||
            !isFirstPersonalGoalIndex(completedGoal.index);
        boolean blurSecondCommonGoalDisplay = completedGoal.type != CompletedGoal.GoalType.COMMON ||
            isFirstPersonalGoalIndex(completedGoal.index);
        boolean blurPersonalGoalDisplay = completedGoal.type != CompletedGoal.GoalType.PERSONAL;
        boolean blurAdjacencyGoalDisplay = completedGoal.type != CompletedGoal.GoalType.ADJACENCY;

        blurrableFirstCommonGoal.blur(blurFirstCommonGoalDisplay);
        blurrableSecondCommonGoal.blur(blurSecondCommonGoalDisplay);
        blurrablePersonalGoal.blur(blurPersonalGoalDisplay);
        blurrableAdjacencyGoal.blur(blurAdjacencyGoalDisplay);

        String goalType = switch (completedGoal.type) {
            case COMMON -> "common";
            case PERSONAL -> "personal";
            case ADJACENCY -> "final";
        };
        popUpTextBox.text(completedGoal.playerName + " completed\n" +
            (completedGoal.type == CompletedGoal.GoalType.COMMON ? "a " : "the ") +
            goalType + " goal");
        blurrableBoard.blur(true);
        twoLayersBoard.showForeground();

        playerDisplayRecyclerDrawable.populate(craftPlayerDisplayListWithAdditionalPoints(completedGoal.playerName,
            completedGoal.points));

        playerNameTextBox.text(completedGoal.playerName);
        selectedBookshelfIndex = playerNameToIndex(completedGoal.playerName);
        previousBookshelfButton.focusable(false);
        bookshelfDrawable.mask(completedGoal.maskSet);
        nextBookshelfButton.focusable(false);
    }

    private void restoreLayoutAfterCompletedGoal() {
        blurrableFirstCommonGoal.blur(false);
        blurrableSecondCommonGoal.blur(false);
        blurrablePersonalGoal.blur(false);
        blurrableAdjacencyGoal.blur(false);

        blurrableBoard.blur(false);
        twoLayersBoard.hideForeground();

        playerDisplayRecyclerDrawable.populate(craftPlayerDisplayList());

        populateBookshelfMenu();
    }

    private void displayGoalTask() {
        CompletedGoal completedGoal = completedGoals.poll();
        playerPoints.set(playerNameToIndex(completedGoal.playerName),
            playerPoints.get(playerNameToIndex(completedGoal.playerName)) + completedGoal.points);
        restoreLayoutAfterCompletedGoal();

        if (!completedGoals.isEmpty()) {
            displayCompletedGoal(completedGoals.peek());

            completedGoalTimer = new Timer();
            completedGoalTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    synchronized (getLock()) {
                        displayGoalTask();
                    }
                }
            }, 8000);
        } else {
            completedGoalTimer = null;

            displayingPopUp = false;

            getLock().notifyAll();

            if (gameOver) {
                switchAppLayout(GameOverLayout.NAME);
            }
        }
    }

    private void addCompletedGoalToDisplayQueue(CompletedGoal completedGoal) {
        while (displayingPopUp) {
            try {
                getLock().wait();
            } catch (InterruptedException e) {

            }
        }

        displayingPopUp = true;

        if (completedGoal.type == CompletedGoal.GoalType.COMMON) {
            int i = 1;
            if (isFirstPersonalGoalIndex(completedGoal.index)) {
                i = 0;
            }

            List<Integer> newPointStack = commonGoals[i].getPointStack();
            newPointStack.remove(newPointStack.size() - 1);
            commonGoals[i].setPointStack(newPointStack);

            populateGoalsMenu();
        }

        completedGoals.add(completedGoal);

        if (completedGoalTimer == null) {
            displayCompletedGoal(completedGoal);

            completedGoalTimer = new Timer();
            completedGoalTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    synchronized (getLock()) {
                        displayGoalTask();
                    }
                }
            }, 8000);
        }
    }

    private int selectedBookshelfIndex;

    // Populate these data through the transceiver
    private int playingPlayerIndex;
    private List<String> playerNames;
    private List<Integer> playerPoints;
    private int clientPlayerIndex;
    private List<Bookshelf> bookshelves;
    private PersonalGoal personalGoal = null;
    private CommonGoal[] commonGoals = null;
    private String gameName;
    private BoardView board;
    private boolean gameOver;

    private int playerNameToIndex(String name) {
        for (int i = 0; i < playerNames.size(); i++) {
            if (playerNames.get(i).equals(name)) {
                return i;
            }
        }

        throw new IllegalArgumentException("There is no player named " + name);
    }

    private List<PlayerDisplay> craftPlayerDisplayList() {
        List<PlayerDisplay> playerDisplays = new ArrayList<>();

        for (int i = 0; i < playerNames.size(); i++) {
            int j = 0;
            while (j < playerDisplays.size() && playerDisplays.get(j).points >
                    playerPoints.get(i)) {
                j++;
            }

            playerDisplays.add(j, new PlayerDisplay(playerNames.get(i), playerPoints.get(i), j + 1,
                i == clientPlayerIndex, false));

            for (j++; j < playerDisplays.size(); j++) {
                if (playerDisplays.get(i).points < playerPoints.get(i)) {
                    playerDisplays.get(i).position++;
                }
            }
        }

        return playerDisplays;
    }

    private List<PlayerDisplay> craftPlayerDisplayListWithAdditionalPoints(String playerName, int additionalPoints) {
        List<PlayerDisplay> playerDisplays = craftPlayerDisplayList();

        int playerIndex = 0;
        for (int i = 0; i < playerDisplays.size(); i++) {
            playerDisplays.get(i).blurred = !playerDisplays.get(i).name.equals(playerName);

            if (playerDisplays.get(i).name.equals(playerName)) {
                playerIndex = i;
            }
        }

        for (int j = playerIndex - 1; j >= 0; j--) {
            if (playerDisplays.get(j).points < playerDisplays.get(j + 1).points + additionalPoints) {
                if (j == 0) {
                    playerDisplays.get(j + 1).position = 1;
                } else {
                    playerDisplays.get(j + 1).position = playerDisplays.get(j - 1).position;
                    if (playerDisplays.get(j - 1).points > playerDisplays.get(j + 1).points + additionalPoints) {
                        playerDisplays.get(j + 1).position++;
                    }
                }

                playerDisplays.get(j).position = playerDisplays.get(j + 1).position + 1;

                Collections.swap(playerDisplays, j, j + 1);
                playerIndex = j;
            } else {
                break;
            }
        }

        playerDisplays.get(playerIndex).additionalPoints = additionalPoints;

        return playerDisplays;
    }

    private NetworkEventTransceiver transceiver = null;
    private Requester<Response, SelectTileEventData> selectTileRequester = null;
    private Requester<Response, DeselectTileEventData> deselectTileRequester = null;
    private Requester<Response, InsertTileEventData> insertTileRequester = null;
    private Requester<Response, PauseGameEventData> pauseGameRequester = null;
    private Requester<Response, PlayerExitGame> playerExitGameRequester = null;

    private boolean displayingPopUp = false;

    @Override
    public void setup(String previousLayoutName) {
        if (previousLayoutName.equals(LobbyLayout.NAME)) {
            if (transceiver == null) {
                transceiver = (NetworkEventTransceiver) appDataProvider.get(ConnectionMenuLayout.NAME, "transceiver");
                selectTileRequester = Response.requester(transceiver, transceiver, getLock());
                deselectTileRequester = Response.requester(transceiver, transceiver, getLock());
                insertTileRequester = Response.requester(transceiver, transceiver, getLock());
                pauseGameRequester = Response.requester(transceiver, transceiver, getLock());
                playerExitGameRequester = Response.requester(transceiver, transceiver, getLock());

                InitialGameEventData.castEventReceiver(transceiver).registerListener(data -> {
                    playerNames = data.gameView().getPlayers().stream().map(Player::getUsername)
                        .toList();

                    try {
                        playingPlayerIndex = playerNameToIndex(data.gameView().getCurrentPlayer().getUsername());
                    } catch (IllegalFlowException e) {
                        throw new IllegalStateException("It should be always possible to retrieve playing player");
                    }

                    clientPlayerIndex = playerNameToIndex(appDataProvider.getString(
                        LoginMenuLayout.NAME,
                        "username"
                    ));

                    selectedBookshelfIndex = clientPlayerIndex;

                    playerPoints = new ArrayList<>(data.gameView().getPlayers().stream().map(Player::getPoints)
                        .toList());

                    bookshelves = new ArrayList<>(data.gameView().getPlayers().stream().map(Player::getBookshelf)
                        .toList());

                    commonGoals = data.gameView().getCommonGoals();

                    gameName = data.gameView().getName();

                    gameNameTextBox.text("Game: " + gameName);

                    populateTurnTextBox();

                    populateBookshelfMenu();
                    playerDisplayRecyclerDrawable.populate(craftPlayerDisplayList());
                    populateBoard(data.gameView().getBoard());
                });

                CurrentPlayerChangedEventData.castEventReceiver(transceiver).registerListener(data -> {
                    playingPlayerIndex = playerNameToIndex(data.getUsername());
                    populateBoard(board);

                    populateTurnTextBox();

                    if (completedGoalTimer == null) {
                        populateBookshelfMenu();
                    }
                });

                PersonalGoalSetEventData.castEventReceiver(transceiver).registerListener(data -> {
                    personalGoal = PersonalGoal.fromIndex(data.personalGoal());
                    populateGoalsMenu();
                });

                BoardChangedEventData.castEventReceiver(transceiver).registerListener(data -> {
                    board = data.board();
                    populateBoard(board);
                });

                BookshelfHasChangedEventData.castEventReceiver(transceiver).registerListener(data -> {
                    bookshelves.set(playerNameToIndex(data.getUsername()), data.getBookshelf());

                    if (completedGoalTimer == null) {
                        populateBookshelfMenu();
                    }
                });

                CommonGoalCompletedEventData.castEventReceiver(transceiver).registerListener(data -> {
                    CompletedGoal completedGoal = new CompletedGoal(CompletedGoal.GoalType.COMMON,
                        data.getCommonGoalCompleted(), data.getPlayer().getUsername(),
                        data.getPoints(), data.getBookshelfMaskSet());
                    addCompletedGoalToDisplayQueue(completedGoal);
                });

                GameOverEventData.castEventReceiver(transceiver).registerListener(data -> {
                    // Add final goal
                    for (int i = 0; i < playerNames.size(); i++) {
                        if (data.personalGoal().get(i).getKey() > 0) {
                            addCompletedGoalToDisplayQueue(new CompletedGoal(CompletedGoal.GoalType.PERSONAL,
                                0, playerNames.get(i), data.personalGoal().get(i).getKey(),
                                data.personalGoal().get(i).getValue()));
                        }

                        if (data.adjacencyGoal().get(i).getKey() > 0) {
                            addCompletedGoalToDisplayQueue(new CompletedGoal(CompletedGoal.GoalType.ADJACENCY,
                                0, playerNames.get(i), data.adjacencyGoal().get(i).getKey(),
                                data.adjacencyGoal().get(i).getValue()));
                        }
                    }

                    gameOver = true;
                });

                FirstFullBookshelfEventData.castEventReceiver(transceiver).registerListener(data -> {
                    while (displayingPopUp) {
                        try {
                            getLock().wait();
                        } catch (InterruptedException e) {

                        }
                    }

                    displayingPopUp = true;

                    playerDisplayRecyclerDrawable.populate(craftPlayerDisplayListWithAdditionalPoints(
                        data.username(),
                        1
                    ));

                    blurrableBoard.blur(true);
                    popUpTextBox.text(data.username() + " filled\nthe bookshelf");
                    twoLayersBoard.showForeground();

                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            synchronized (getLock()) {
                                playerPoints.set(playerNameToIndex(data.username()),
                                    playerPoints.get(playerNameToIndex(data.username())) + 1);

                                playerDisplayRecyclerDrawable.populate(craftPlayerDisplayList());

                                blurrableBoard.blur(false);
                                twoLayersBoard.hideForeground();

                                displayingPopUp = false;

                                getLock().notifyAll();
                            }
                        }
                    }, 2000);
                });

                PlayerHasDisconnectedEventData.castEventReceiver(transceiver).registerListener(data -> {
                    while (displayingPopUp) {
                        try {
                            getLock().wait();
                        } catch (InterruptedException e) {

                        }
                    }

                    displayingPopUp = true;

                    blurrableBoard.blur(true);
                    popUpTextBox.text(data.username() + " has\ndisconnected");
                    twoLayersBoard.showForeground();

                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            synchronized (getLock()) {
                                blurrableBoard.blur(false);
                                twoLayersBoard.hideForeground();

                                displayingPopUp = false;

                                getLock().notifyAll();
                            }
                        }
                    }, 2000);
                });

                GameHasBeenStoppedEventData.castEventReceiver(transceiver).registerListener(data -> {
                    while (displayingPopUp) {
                        try {
                            getLock().wait();
                        } catch (InterruptedException e) {

                        }
                    }

                    displayingPopUp = true;

                    blurrableBoard.blur(true);
                    popUpTextBox.text("Game has been\nstopped");
                    twoLayersBoard.showForeground();

                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            synchronized (getLock()) {
                                blurrableBoard.blur(false);
                                twoLayersBoard.hideForeground();

                                displayingPopUp = false;

                                getLock().notifyAll();

                                switchAppLayout(AvailableGamesMenuLayout.NAME);
                            }
                        }
                    }, 2000);
                });

                PlayerDisconnectedInternalEventData.castEventReceiver(transceiver).registerListener(data -> {
                    transceiver = null;
                    selectTileRequester = null;
                    deselectTileRequester = null;
                    insertTileRequester = null;
                    pauseGameRequester = null;
                    playerExitGameRequester = null;

                    if (isCurrentLayout()) {
                        switchAppLayout(ConnectionMenuLayout.NAME);
                    }
                });
            }

            gameOver = false;

            transceiver.broadcast(new JoinStartedGameEventData());
        }
    }

    @Override
    public String getName() {
        return NAME;
    }
}
