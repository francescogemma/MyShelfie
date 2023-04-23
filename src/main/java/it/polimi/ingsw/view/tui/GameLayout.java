package it.polimi.ingsw.view.tui;

import it.polimi.ingsw.model.bookshelf.Bookshelf;
import it.polimi.ingsw.model.bookshelf.BookshelfMask;
import it.polimi.ingsw.model.bookshelf.BookshelfMaskSet;
import it.polimi.ingsw.model.bookshelf.Shelf;
import it.polimi.ingsw.model.tile.Tile;
import it.polimi.ingsw.model.tile.TileColor;
import it.polimi.ingsw.model.tile.TileVersion;
import it.polimi.ingsw.view.tui.terminal.drawable.Orientation;
import it.polimi.ingsw.view.tui.terminal.drawable.app.AppLayout;
import it.polimi.ingsw.view.tui.terminal.drawable.app.AppLayoutData;
import it.polimi.ingsw.view.tui.terminal.drawable.orientedlayout.OrientedLayout;

import java.util.List;
import java.util.Map;

public class GameLayout extends AppLayout {
    public static final String NAME = "GAME";

    private final BoardDrawable boardDrawable = new BoardDrawable();
    private final BookshelfDrawable bookshelfDrawable = new BookshelfDrawable();

    public GameLayout() {
        setLayout(new OrientedLayout(Orientation.HORIZONTAL,
                boardDrawable.center().scrollable().weight(7),
                bookshelfDrawable.center().weight(3)
            ).center().crop()
        );

        setData(new AppLayoutData(
            Map.of()
        ));
    }

    // Populate these data through the transceiver
    private String player = "foo";
    private Bookshelf bookshelf = new Bookshelf();

    @Override
    public void setup(String previousLayoutName) {
        if (previousLayoutName.equals(LobbyLayout.NAME)) {
            bookshelf.insertTiles(List.of(Tile.getInstance(TileColor.GREEN, TileVersion.FIRST),
                Tile.getInstance(TileColor.BLUE, TileVersion.SECOND),
                Tile.getInstance(TileColor.MAGENTA, TileVersion.SECOND)), 0);
            bookshelf.insertTiles(List.of(Tile.getInstance(TileColor.GREEN, TileVersion.FIRST),
                Tile.getInstance(TileColor.BLUE, TileVersion.SECOND),
                Tile.getInstance(TileColor.MAGENTA, TileVersion.SECOND)), 1);
            bookshelf.insertTiles(List.of(Tile.getInstance(TileColor.GREEN, TileVersion.FIRST),
                Tile.getInstance(TileColor.BLUE, TileVersion.SECOND),
                Tile.getInstance(TileColor.MAGENTA, TileVersion.SECOND)), 1);

            boardDrawable.getNonFillTileDrawables().forEach(tileDrawable -> {
                tileDrawable.onselect((row, column) ->
                    boardDrawable.getTileDrawableAt(row, column).selected(true))
                    .ondeselect((row, column) ->
                        boardDrawable.getTileDrawableAt(row, column).selected(false));
            });

            // bookshelfDrawable.populate(bookshelf);

            BookshelfMaskSet bookshelfMaskSet = new BookshelfMaskSet();
            BookshelfMask firstMask = new BookshelfMask(bookshelf);

            firstMask.add(Shelf.getInstance(5, 0));
            firstMask.add(Shelf.getInstance(4, 0));

            BookshelfMask secondMask = new BookshelfMask(bookshelf);

            secondMask.add(Shelf.getInstance(5, 1));

            bookshelfMaskSet.add(firstMask);
            bookshelfMaskSet.add(secondMask);

            bookshelfDrawable.mask(bookshelfMaskSet);
        }
    }

    @Override
    public String getName() {
        return NAME;
    }
}
