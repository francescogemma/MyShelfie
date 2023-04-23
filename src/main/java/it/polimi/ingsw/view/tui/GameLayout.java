package it.polimi.ingsw.view.tui;

import it.polimi.ingsw.model.bookshelf.Bookshelf;
import it.polimi.ingsw.model.tile.Tile;
import it.polimi.ingsw.model.tile.TileColor;
import it.polimi.ingsw.model.tile.TileVersion;
import it.polimi.ingsw.view.tui.terminal.drawable.Fill;
import it.polimi.ingsw.view.tui.terminal.drawable.Orientation;
import it.polimi.ingsw.view.tui.terminal.drawable.app.AppLayout;
import it.polimi.ingsw.view.tui.terminal.drawable.app.AppLayoutData;
import it.polimi.ingsw.view.tui.terminal.drawable.menu.Button;
import it.polimi.ingsw.view.tui.terminal.drawable.menu.value.TextBox;
import it.polimi.ingsw.view.tui.terminal.drawable.orientedlayout.OrientedLayout;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.PrimitiveSymbol;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GameLayout extends AppLayout {
    public static final String NAME = "GAME";

    private final BoardDrawable boardDrawable = new BoardDrawable();
    private final TextBox playerNameTextBox = new TextBox().unfocusable();
    private final BookshelfDrawable bookshelfDrawable = new BookshelfDrawable();
    private final Button nextBookshelfButton = new Button(">");
    private final Button previousBookshelfButton = new Button("<");

    public GameLayout() {
        setLayout(new OrientedLayout(Orientation.HORIZONTAL,
                new OrientedLayout(Orientation.VERTICAL,
                    new TextBox().text("Ciao\nProva\nProva\nFrasemoltolunga\nciao\nProva").center().weight(1),
                    new TextBox().text("Ciao\nProva\nProva\nFrasemoltolunga\nciao\nProva").center().weight(1),
                    new TextBox().text("Ciao\nProva\nProva\nFrasemoltolunga\nciao\nProva").center().weight(1)
                ).weight(2),
                boardDrawable.center().scrollable().weight(3),
                new OrientedLayout(Orientation.VERTICAL,
                    playerNameTextBox.center().weight(1),
                    new OrientedLayout(Orientation.HORIZONTAL,
                        new Fill(PrimitiveSymbol.EMPTY).weight(1),
                        previousBookshelfButton.center().weight(2),
                        bookshelfDrawable.center().weight(12),
                        nextBookshelfButton.center().weight(2),
                        new Fill(PrimitiveSymbol.EMPTY).weight(1)
                    ).center().weight(12)
                ).weight(2)
            ).center().crop()
        );

        setData(new AppLayoutData(
            Map.of()
        ));

        nextBookshelfButton.onpress(() -> {
            selectedBookshelfIndex++;

            updateBookshelfMenu();
        });

        previousBookshelfButton.onpress(() -> {
           selectedBookshelfIndex--;

           updateBookshelfMenu();
        });
    }

    private void updateBookshelfMenu() {
        previousBookshelfButton.focusable(selectedBookshelfIndex > 0);
        nextBookshelfButton.focusable(selectedBookshelfIndex < bookshelves.size() - 1);
        bookshelfDrawable.focusable(selectedBookshelfIndex == playerIndex);
        bookshelfDrawable.populate(bookshelves.get(selectedBookshelfIndex));
        playerNameTextBox.text(players.get(selectedBookshelfIndex));
    }

    private int selectedBookshelfIndex;

    // Populate these data through the transceiver
    private List<String> players = List.of("bar", "tizio", "foo", "caio");
    private int playerIndex = 2;
    private List<Bookshelf> bookshelves = new ArrayList<>();
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
            updateBookshelfMenu();
        }
    }

    @Override
    public String getName() {
        return NAME;
    }
}
