package it.polimi.ingsw.view.tui;

import it.polimi.ingsw.view.tui.terminal.drawable.app.AppLayout;
import it.polimi.ingsw.view.tui.terminal.drawable.app.AppLayoutData;

import java.util.Map;

public class GameLayout extends AppLayout {
    public static final String NAME = "GAME";

    private final BoardDrawable boardDrawable = new BoardDrawable();

    public GameLayout() {
        setLayout(boardDrawable.center().scrollable().alignUpLeft().crop());

        setData(new AppLayoutData(
            Map.of()
        ));
    }

    @Override
    public void setup(String previousLayoutName) {
        if (previousLayoutName.equals(LobbyLayout.NAME)) {
            boardDrawable.getNonFillTileDrawables().forEach(tileDrawable -> {
                tileDrawable.onselect((row, column) ->
                    boardDrawable.getTileDrawableAt(row, column).selected(true))
                    .ondeselect((row, column) ->
                        boardDrawable.getTileDrawableAt(row, column).selected(false));
            });
        }
    }

    @Override
    public String getName() {
        return NAME;
    }
}
