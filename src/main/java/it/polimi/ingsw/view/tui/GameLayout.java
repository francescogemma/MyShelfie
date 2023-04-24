package it.polimi.ingsw.view.tui;

import it.polimi.ingsw.model.bookshelf.Bookshelf;
import it.polimi.ingsw.model.goal.CommonGoal;
import it.polimi.ingsw.model.goal.PersonalGoal;
import it.polimi.ingsw.model.tile.Tile;
import it.polimi.ingsw.model.tile.TileColor;
import it.polimi.ingsw.model.tile.TileVersion;
import it.polimi.ingsw.view.tui.terminal.drawable.*;
import it.polimi.ingsw.view.tui.terminal.drawable.app.AppLayout;
import it.polimi.ingsw.view.tui.terminal.drawable.app.AppLayoutData;
import it.polimi.ingsw.view.tui.terminal.drawable.menu.Button;
import it.polimi.ingsw.view.tui.terminal.drawable.menu.value.TextBox;
import it.polimi.ingsw.view.tui.terminal.drawable.orientedlayout.OrientedLayout;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.Color;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.PrimitiveSymbol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GameLayout extends AppLayout {
    public static final String NAME = "GAME";

    private final TextBox firstCommonGoalDescriptionTextBox = new TextBox().hideCursor();
    private final GoalDrawable firstCommonGoalDrawable = new GoalDrawable();
    private final TextBox firstCommonGoalPointsTextBox = new TextBox().unfocusable();
    private final TextBox secondCommonGoalDescriptionTextBox = new TextBox().hideCursor();
    private final GoalDrawable secondCommonGoalDrawable = new GoalDrawable();
    private final TextBox secondCommonGoalPointsTextBox = new TextBox().unfocusable();
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
    private final TextBox adjacencyGoalTextBox = new TextBox()
        .text("Points from adjacent tiles in the bookshelf:\n3 adjacent tiles -> 2 points\n" +
            "4 adjacent tiles -> 3 points\n5 adjacent tiles -> 5 points\n6+ adjacent tiles -> 8 points")
        .hideCursor();
    private final BoardDrawable boardDrawable = new BoardDrawable();
    private final TextBox playerNameTextBox = new TextBox().unfocusable();
    private final BookshelfDrawable bookshelfDrawable = new BookshelfDrawable();
    private final Button nextBookshelfButton = new Button(">");
    private final Button previousBookshelfButton = new Button("<");
    private final TextBox gameNameTextBox = new TextBox().unfocusable();
    private static class PlayerDisplay {
        public PlayerDisplay(String name, int points, int position, boolean isPlayer) {
            this.name = name;
            this.points = points;
            this.position = position;
            this.isPlayer = isPlayer;
        }

        private String name;
        private int points;
        private int position;
        private boolean isPlayer;
    }

    private static class PlayerDisplayDrawable extends FixedLayoutDrawable<Drawable> {
        private final TextBox positionTextBox = new TextBox().unfocusable();
        private final TextBox playerNameTextBox = new TextBox().hideCursor();
        private final TextBox playerPointsTextBox = new TextBox().hideCursor();

        public PlayerDisplayDrawable() {
            setLayout(new OrientedLayout(Orientation.HORIZONTAL,
                positionTextBox.center().weight(1),
                playerNameTextBox.center().weight(1),
                playerPointsTextBox.center().weight(1)).center().crop()
                .fixSize(new DrawableSize(5, 40)).addBorderBox()
            );
        }
    }

    private final RecyclerDrawable<PlayerDisplayDrawable, PlayerDisplay> playerDisplayRecyclerDrawable =
        new RecyclerDrawable<>(Orientation.VERTICAL, PlayerDisplayDrawable::new);

    private final static int GOALS_TEXT_BOX_LINES = 10;
    private final static int GOALS_TEXT_BOX_COLUMNS = 30;

    public GameLayout() {
        setLayout(new OrientedLayout(Orientation.HORIZONTAL,
                new OrientedLayout(Orientation.VERTICAL,
                    new OrientedLayout(Orientation.VERTICAL,
                        new TextBox().text("First common goal").unfocusable().center().weight(1),
                        new OrientedLayout(Orientation.HORIZONTAL,
                            firstCommonGoalDrawable.center().weight(1),
                            firstCommonGoalDescriptionTextBox.center()
                                .crop().fixSize(new DrawableSize(GOALS_TEXT_BOX_LINES, GOALS_TEXT_BOX_COLUMNS))
                                .weight(3)).weight(4),
                        firstCommonGoalPointsTextBox.center().weight(1)
                    ).addBorderBox().weight(1),
                    new OrientedLayout(Orientation.VERTICAL,
                        new TextBox().text("Second common goal").unfocusable().center().weight(1),
                        new OrientedLayout(Orientation.HORIZONTAL,
                            secondCommonGoalDrawable.center().weight(1),
                            secondCommonGoalDescriptionTextBox.center()
                                .crop().fixSize(new DrawableSize(GOALS_TEXT_BOX_LINES, GOALS_TEXT_BOX_COLUMNS))
                                .weight(3)).weight(4),
                        secondCommonGoalPointsTextBox.center().weight(1)
                    ).addBorderBox().weight(1),
                    new OrientedLayout(Orientation.VERTICAL,
                        new TextBox().text("Personal goal").unfocusable().center().weight(1),
                        new OrientedLayout(Orientation.HORIZONTAL,
                            personalGoalDrawable.center().weight(1),
                            personalGoalPointsTextBox.center()
                                .crop().fixSize(new DrawableSize(GOALS_TEXT_BOX_LINES, GOALS_TEXT_BOX_COLUMNS))
                                .weight(3)).weight(4)
                    ).addBorderBox().weight(1),
                    new OrientedLayout(Orientation.VERTICAL,
                        new TextBox().text("Final goal").unfocusable().center().weight(1),
                        adjacencyGoalTextBox.center().weight(4)
                    ).addBorderBox().weight(1)
                ).alignUpLeft().scrollable().weight(3),
                boardDrawable.center().scrollable().weight(2),
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
        bookshelfDrawable.focusable(selectedBookshelfIndex == playerIndex);
        bookshelfDrawable.populate(bookshelves.get(selectedBookshelfIndex));
        playerNameTextBox.text(players.get(selectedBookshelfIndex).name)
            .color(selectedBookshelfIndex == playerIndex ? Color.FOCUS : Color.WHITE);
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

    private int selectedBookshelfIndex;

    // Populate these data through the transceiver
    private List<PlayerDisplay> players = List.of(
        new PlayerDisplay("tizio", 8, 1, false),
        new PlayerDisplay("bar", 5, 2, false),
        new PlayerDisplay("foo", 3, 3, true),
        new PlayerDisplay("caio", 2, 4, false)
    );

    private int playerIndex = 2;
    private List<Bookshelf> bookshelves = new ArrayList<>();
    private PersonalGoal personalGoal = PersonalGoal.fromIndex(0);
    private CommonGoal[] commonGoals = new CommonGoal[]{CommonGoal.fromIndex(4, 4),
        CommonGoal.fromIndex(1, 4)};
    private String gameName = "ourgame";

    @Override
    public void setup(String previousLayoutName) {
        if (previousLayoutName.equals(LobbyLayout.NAME)) {
            boardDrawable.getNonFillTileDrawables().forEach(tileDrawable -> {
                tileDrawable.onselect((row, column) ->
                    boardDrawable.getTileDrawableAt(row, column).selected(true))
                    .ondeselect((row, column) ->
                        boardDrawable.getTileDrawableAt(row, column).selected(false));
            });

            Bookshelf firstBookshelf = new Bookshelf();
            firstBookshelf.insertTiles(List.of(
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST),
                Tile.getInstance(TileColor.BLUE, TileVersion.FIRST)
            ), 0);
            firstBookshelf.insertTiles(List.of(
                Tile.getInstance(TileColor.WHITE, TileVersion.FIRST),
                Tile.getInstance(TileColor.YELLOW, TileVersion.FIRST),
                Tile.getInstance(TileColor.YELLOW, TileVersion.FIRST)
            ), 0);
            firstBookshelf.insertTiles(List.of(
                Tile.getInstance(TileColor.CYAN, TileVersion.FIRST),
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST)
            ), 1);
            firstBookshelf.insertTiles(List.of(
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST),
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST),
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST)
            ), 1);
            firstBookshelf.insertTiles(List.of(
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST),
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST),
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST)
            ), 2);
            firstBookshelf.insertTiles(List.of(
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST),
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST),
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST)
            ), 3);
            firstBookshelf.insertTiles(List.of(
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST),
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST),
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST)
            ), 3);
            firstBookshelf.insertTiles(List.of(
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST),
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST),
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST)
            ), 4);
            firstBookshelf.insertTiles(List.of(
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST),
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST),
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST)
            ), 4);

            Bookshelf secondBookshelf = new Bookshelf();
            secondBookshelf.insertTiles(List.of(
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST),
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST),
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST)
            ), 0);
            secondBookshelf.insertTiles(List.of(
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST),
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST),
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST)
            ), 0);
            secondBookshelf.insertTiles(List.of(
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST),
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST),
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST)
            ), 1);
            secondBookshelf.insertTiles(List.of(
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST),
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST),
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST)
            ), 1);
            secondBookshelf.insertTiles(List.of(
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST),
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST),
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST)
            ), 2);
            secondBookshelf.insertTiles(List.of(
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST),
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST),
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST)
            ), 2);
            secondBookshelf.insertTiles(List.of(
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST),
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST),
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST)
            ), 3);
            secondBookshelf.insertTiles(List.of(
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST),
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST),
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST)
            ), 4);
            secondBookshelf.insertTiles(List.of(
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST),
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST),
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST)
            ), 4);

            Bookshelf thirdBookshelf = new Bookshelf();
            thirdBookshelf.insertTiles(List.of(
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST),
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST),
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST)
            ), 0);
            thirdBookshelf.insertTiles(List.of(
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST),
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST),
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST)
            ), 0);
            thirdBookshelf.insertTiles(List.of(
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST),
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST),
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST)
            ), 1);
            thirdBookshelf.insertTiles(List.of(
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST),
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST),
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST)
            ), 1);
            thirdBookshelf.insertTiles(List.of(
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST),
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST),
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST)
            ), 2);
            thirdBookshelf.insertTiles(List.of(
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST),
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST),
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST)
            ), 2);
            thirdBookshelf.insertTiles(List.of(
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST),
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST),
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST)
            ), 3);
            thirdBookshelf.insertTiles(List.of(
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST),
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST),
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST)
            ), 3);
            thirdBookshelf.insertTiles(List.of(
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST),
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST),
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST)
            ), 4);

            Bookshelf fourthBookshelf = new Bookshelf();
            fourthBookshelf.insertTiles(List.of(
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST),
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST),
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST)
            ), 0);
            fourthBookshelf.insertTiles(List.of(
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST),
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST),
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST)
            ), 0);
            fourthBookshelf.insertTiles(List.of(
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST),
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST),
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST)
            ), 1);
            fourthBookshelf.insertTiles(List.of(
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST),
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST),
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST)
            ), 2);
            fourthBookshelf.insertTiles(List.of(
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST),
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST),
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST)
            ), 2);
            fourthBookshelf.insertTiles(List.of(
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST),
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST),
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST)
            ), 3);
            fourthBookshelf.insertTiles(List.of(
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST),
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST),
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST)
            ), 3);
            fourthBookshelf.insertTiles(List.of(
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST),
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST),
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST)
            ), 4);
            fourthBookshelf.insertTiles(List.of(
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST),
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST),
                Tile.getInstance(TileColor.GREEN, TileVersion.FIRST)
            ), 4);

            bookshelves.add(firstBookshelf);
            bookshelves.add(secondBookshelf);
            bookshelves.add(thirdBookshelf);
            bookshelves.add(fourthBookshelf);

            selectedBookshelfIndex = 0;

            populateBookshelfMenu();
            populateGoalsMenu();
            playerDisplayRecyclerDrawable.populate(players, ((playerDisplayDrawable, playerDisplay) -> {
                playerDisplayDrawable.positionTextBox.text("# " + String.valueOf(playerDisplay.position) + " ");
                playerDisplayDrawable.playerNameTextBox.text(playerDisplay.name + " ")
                    .color(playerDisplay.isPlayer ? Color.FOCUS : Color.WHITE);
                playerDisplayDrawable.playerPointsTextBox.text("Points: " + playerDisplay.points);
            }));
            gameNameTextBox.text("Game: " + gameName);
        }
    }

    @Override
    public String getName() {
        return NAME;
    }
}
