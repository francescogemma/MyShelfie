package it.polimi.ingsw.view.tui;

import it.polimi.ingsw.controller.Response;
import it.polimi.ingsw.event.NetworkEventTransceiver;
import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.data.client.DeselectTileEventData;
import it.polimi.ingsw.event.data.client.InsertTileEventData;
import it.polimi.ingsw.event.data.client.JoinStartedGameEventData;
import it.polimi.ingsw.event.data.client.SelectTileEventData;
import it.polimi.ingsw.event.data.game.BoardChangedEventData;
import it.polimi.ingsw.event.data.game.BookshelfHasChanged;
import it.polimi.ingsw.event.data.game.CurrentPlayerChangedEventData;
import it.polimi.ingsw.event.data.game.GoalEventData;
import it.polimi.ingsw.model.bookshelf.Bookshelf;
import it.polimi.ingsw.model.bookshelf.BookshelfMaskSet;
import it.polimi.ingsw.model.goal.CommonGoal;
import it.polimi.ingsw.model.goal.PersonalGoal;
import it.polimi.ingsw.model.tile.Tile;
import it.polimi.ingsw.utils.Coordinate;
import it.polimi.ingsw.view.tui.terminal.drawable.*;
import it.polimi.ingsw.view.tui.terminal.drawable.app.AppLayout;
import it.polimi.ingsw.view.tui.terminal.drawable.app.AppLayoutData;
import it.polimi.ingsw.view.tui.terminal.drawable.menu.Button;
import it.polimi.ingsw.view.tui.terminal.drawable.menu.value.TextBox;
import it.polimi.ingsw.view.tui.terminal.drawable.orientedlayout.OrientedLayout;
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

    private final BoardDrawable boardDrawable = new BoardDrawable();
    private final BlurrableDrawable blurrableBoard = boardDrawable.center().scrollable().blurrable();
    private final TextBox completedCommonGoalPopUpTextBox = new TextBox().hideCursor();
    private final TwoLayersDrawable twoLayersBoard = new TwoLayersDrawable(
        blurrableBoard,
        completedCommonGoalPopUpTextBox.center().crop()
            .fixSize(new DrawableSize(10, 25)).addBorderBox().center()
    );
    private final TextBox playerNameTextBox = new TextBox().unfocusable();
    private final BookshelfDrawable bookshelfDrawable = new BookshelfDrawable();
    private final Button nextBookshelfButton = new Button(">");
    private final Button previousBookshelfButton = new Button("<");
    private final TextBox gameNameTextBox = new TextBox().unfocusable();
    private static class PlayerDisplay {
        public PlayerDisplay(String name, int points, int position, boolean isPlayingPlayer,
                             boolean isClientPlayer,
                             boolean blurred) {
            this.name = name;
            this.points = points;
            this.position = position;
            this.isClientPlayer = isClientPlayer;
            this.isPlayingPlayer = isPlayingPlayer;
            this.blurred = blurred;
        }

        private String name;
        private int points;
        private int position;
        private boolean isPlayingPlayer;
        private boolean isClientPlayer;
        private boolean blurred;
    }

    private static class PlayerDisplayDrawable extends FixedLayoutDrawable<BlurrableDrawable> {
        private final TextBox positionTextBox = new TextBox().unfocusable();
        private final TextBox playerNameTextBox = new TextBox().hideCursor();
        private final TextBox playerPointsTextBox = new TextBox().hideCursor();

        public PlayerDisplayDrawable() {
            setLayout(new OrientedLayout(Orientation.HORIZONTAL,
                positionTextBox.center().weight(1),
                playerNameTextBox.center().weight(1),
                playerPointsTextBox.center().weight(1)).center().crop()
                .fixSize(new DrawableSize(5, 40)).addBorderBox().blurrable()
            );
        }
    }

    private final RecyclerDrawable<PlayerDisplayDrawable, PlayerDisplay> playerDisplayRecyclerDrawable =
        new RecyclerDrawable<>(Orientation.VERTICAL, PlayerDisplayDrawable::new,
            (playerDisplayDrawable, playerDisplay) -> {
                playerDisplayDrawable.positionTextBox.text("# " + String.valueOf(playerDisplay.position) + " ");
                playerDisplayDrawable.playerNameTextBox.text(playerDisplay.name + " " +
                        (playerDisplay.isPlayingPlayer ? "(playing)" : ""))
                    .color(playerDisplay.isClientPlayer ? Color.FOCUS : Color.WHITE);
                playerDisplayDrawable.playerPointsTextBox.text("Points: " + playerDisplay.points);
                playerDisplayDrawable.getLayout().blur(playerDisplay.blurred);
            });

    private final static int GOALS_TEXT_BOX_LINES = 10;
    private final static int GOALS_TEXT_BOX_COLUMNS = 30;

    public GameLayout() {
        setLayout(new OrientedLayout(Orientation.HORIZONTAL,
                new OrientedLayout(Orientation.VERTICAL,
                    blurrableFirstCommonGoal.weight(1),
                    blurrableSecondCommonGoal.weight(1),
                    blurrablePersonalGoal.weight(1),
                    blurrableAdjacencyGoal.weight(1)
                ).alignUpLeft().scrollable().weight(3),
                twoLayersBoard.weight(2),
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
            Map.of()
        ));

        nextBookshelfButton.onpress(() -> {
            selectedBookshelfIndex++;

            populateBookshelfMenu();
        });

        previousBookshelfButton.onpress(() -> {
           selectedBookshelfIndex--;

           populateBookshelfMenu();
        });
    }

    private void populateBookshelfMenu() {
        previousBookshelfButton.focusable(selectedBookshelfIndex > 0);
        nextBookshelfButton.focusable(selectedBookshelfIndex < bookshelves.size() - 1);
        bookshelfDrawable.focusable(selectedBookshelfIndex == clientPlayerIndex);
        bookshelfDrawable.populate(bookshelves.get(selectedBookshelfIndex));
        playerNameTextBox.text(playerNames.get(selectedBookshelfIndex))
            .color(selectedBookshelfIndex == clientPlayerIndex ? Color.FOCUS : Color.WHITE);
    }

    private void populateGoalsMenu() {
        firstCommonGoalDescriptionTextBox.text(commonGoals[0].getDescription());
        firstCommonGoalDrawable.populate(commonGoals[0].getDisplay());
        firstCommonGoalPointsTextBox.text("Points: " + commonGoals[0].getPointStack()
            .get(commonGoals[0].getPointStack().size() - 1));

        secondCommonGoalDescriptionTextBox.text(commonGoals[1].getDescription());
        secondCommonGoalDrawable.populate(commonGoals[1].getDisplay());
        secondCommonGoalPointsTextBox.text("Points: " + commonGoals[1].getPointStack()
            .get(commonGoals[1].getPointStack().size() - 1));

        personalGoalDrawable.populate(personalGoal.getTilesColorMask());
    }

    private void displayCommonGoalCompleted(int playerIndex, boolean completedFirstCommonGoal,
                                            BookshelfMaskSet bookshelfMaskSet) {
        if (completedFirstCommonGoal) {
            blurrableSecondCommonGoal.blur(true);
        } else {
            blurrableFirstCommonGoal.blur(true);
        }

        blurrablePersonalGoal.blur(true);
        blurrableAdjacencyGoal.blur(true);

        completedCommonGoalPopUpTextBox.text(playerNames.get(playerIndex) + " completed the\n" +
            (completedFirstCommonGoal ? "first" : "second") + " common goal");
        blurrableBoard.blur(true);
        twoLayersBoard.showForeground();

        playerDisplayRecyclerDrawable.populate(craftPlayerDisplayList(true, playerIndex));

        playerNameTextBox.text(playerNames.get(playerIndex));

        selectedBookshelfIndex = playerIndex;

        previousBookshelfButton.focusable(false);
        bookshelfDrawable.mask(bookshelfMaskSet);
        nextBookshelfButton.focusable(false);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                synchronized (getLock()) {
                    blurrableFirstCommonGoal.blur(false);
                    blurrableSecondCommonGoal.blur(false);
                    blurrablePersonalGoal.blur(false);
                    blurrableAdjacencyGoal.blur(false);
                    blurrableBoard.blur(false);
                    twoLayersBoard.hideForeground();
                    playerDisplayRecyclerDrawable.populate(craftPlayerDisplayList(false, 0));

                    populateBookshelfMenu();
                }
            }
        }, 10000);
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

    private int playerNameToIndex(String name) {
        for (int i = 0; i < playerNames.size(); i++) {
            if (playerNames.get(i).equals(name)) {
                return i;
            }
        }

        throw new IllegalArgumentException("There is no player named " + name);
    }

    private List<PlayerDisplay> craftPlayerDisplayList(boolean blurred, int playerToExcludeFromBlurIndex) {
        List<PlayerDisplay> playerDisplays = new ArrayList<>();

        for (int i = 0; i < playerNames.size(); i++) {
            int j = 0;
            while (j < playerDisplays.size() && playerDisplays.get(j).points >
                    playerPoints.get(i)) {
                j++;
            }

            playerDisplays.add(j, new PlayerDisplay(playerNames.get(i), playerPoints.get(i),j + 1,
                i == playingPlayerIndex,
                i == clientPlayerIndex,
                i != playerToExcludeFromBlurIndex && blurred));

            for (j++; j < playerDisplays.size(); j++) {
                if (playerDisplays.get(i).points < playerPoints.get(i)) {
                    playerDisplays.get(i).position++;
                }
            }
        }

        return playerDisplays;
    }

    private NetworkEventTransceiver transceiver;
    private Requester<Response, SelectTileEventData> selectTileRequester;
    private Requester<Response, DeselectTileEventData> deselectTileRequester;
    private Requester<Response, InsertTileEventData> insertTileRequester;

    @Override
    public void setup(String previousLayoutName) {
        if (previousLayoutName.equals(LobbyLayout.NAME)) {
            selectedBookshelfIndex = 0;
            playerNames = (List<String>) appDataProvider.get(LobbyLayout.NAME, "playernames");
            playerPoints = new ArrayList<>(playerNames.size());
            bookshelves = new ArrayList<>(playerNames.size());
            for (int i = 0; i < playerNames.size(); i++) {
                playerPoints.add(0);
                bookshelves.add(new Bookshelf());

                if (playerNames.get(i).equals(appDataProvider.get(LoginMenuLayout.NAME, "username"))) {
                    clientPlayerIndex = i;
                }
            }

            gameName = appDataProvider.getString(AvailableGamesMenuLayout.NAME, "selectedgame");

            gameNameTextBox.text("Game: " + gameName);

            transceiver = (NetworkEventTransceiver) appDataProvider.get(ConnectionMenuLayout.NAME, "transceiver");
            selectTileRequester = Response.requester(transceiver, transceiver, getLock());
            deselectTileRequester = Response.requester(transceiver, transceiver, getLock());
            insertTileRequester = Response.requester(transceiver, transceiver, getLock());

            populateBookshelfMenu();
            playerDisplayRecyclerDrawable.populate(craftPlayerDisplayList(false, 0));

            boardDrawable.getNonFillTileDrawables().forEach(tileDrawable -> tileDrawable.onselect(
                (rowInBoard, columnInBoard) -> displayServerResponse(selectTileRequester.request(new SelectTileEventData(
                    new Coordinate(rowInBoard, columnInBoard)
                )))
            ));

            boardDrawable.getNonFillTileDrawables().forEach(tileDrawable -> tileDrawable.ondeselect(
                (rowInBoard, columnInBoard) -> displayServerResponse(deselectTileRequester.request(new DeselectTileEventData(
                    new Coordinate(rowInBoard, columnInBoard)
                )))
            ));

            for (int column = 0; column < Bookshelf.COLUMNS; column++) {
                bookshelfDrawable.getColumn(column).onselect(c -> {
                   displayServerResponse(insertTileRequester.request(new InsertTileEventData(c)));
                });
            }

            GoalEventData.castEventReceiver(transceiver).registerListener(data -> {
                synchronized (getLock()) {
                    commonGoals = new CommonGoal[2];

                    // TODO: Disconnected and then reconnected player finds always full points stack
                    // Remember to re add type adapters to network event transceiver.
                    commonGoals[0] = CommonGoal.fromIndex(data.getCommonGoal().get(0), playerNames.size());
                    commonGoals[1] = CommonGoal.fromIndex(data.getCommonGoal().get(1), playerNames.size());
                    personalGoal = PersonalGoal.fromIndex(data.getPersonalGoal());


                    populateGoalsMenu();
                }
            });

            // Retrieving current player index
            playingPlayerIndex = 0;

            CurrentPlayerChangedEventData.castEventReceiver(transceiver).registerListener(data -> {
                synchronized (getLock()) {
                    playingPlayerIndex = playerNameToIndex(data.getUsername());
                }
            });

            BoardChangedEventData.castEventReceiver(transceiver).registerListener(data -> {
                synchronized (getLock()) {
                    boardDrawable.getNonFillTileDrawables().forEach(tileDrawable -> {
                        tileDrawable.selected(data.board().getSelectedCoordinates().contains(
                            new Coordinate(tileDrawable.getRowInBoard(), tileDrawable.getColumnInBoard())
                        ));

                        tileDrawable.selectable(data.board().getSelectableCoordinate().contains(
                            new Coordinate(tileDrawable.getRowInBoard(), tileDrawable.getColumnInBoard())
                        ) || data.board().getSelectedCoordinates().contains(
                            new Coordinate(tileDrawable.getRowInBoard(), tileDrawable.getColumnInBoard())
                        ));

                        Tile tile = data.board().tileAt(
                            new Coordinate(tileDrawable.getRowInBoard(), tileDrawable.getColumnInBoard())
                        );

                        if (tile != null) {
                            tileDrawable.color(tile.getColor());
                        }
                    });
                }
            });

            BookshelfHasChanged.castEventReceiver(transceiver).registerListener(data -> {
                synchronized (getLock()) {
                    bookshelves.set(playerNameToIndex(data.getUsername()), data.getBookshelf());

                    populateBookshelfMenu();
                }
            });

            transceiver.broadcast(new JoinStartedGameEventData());
        }
    }

    @Override
    public String getName() {
        return NAME;
    }
}
