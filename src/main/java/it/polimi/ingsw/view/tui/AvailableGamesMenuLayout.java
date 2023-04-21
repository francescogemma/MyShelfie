package it.polimi.ingsw.view.tui;

import it.polimi.ingsw.view.tui.terminal.drawable.Fill;
import it.polimi.ingsw.view.tui.terminal.drawable.app.AppLayout;
import it.polimi.ingsw.view.tui.terminal.drawable.app.AppLayoutData;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.Color;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.PrimitiveSymbol;

import java.util.Map;

public class AvailableGamesMenuLayout extends AppLayout {
    public static final String NAME = "AVAILABLE_GAMES_MENU";

    public AvailableGamesMenuLayout() {
        setLayout(new Fill(PrimitiveSymbol.EMPTY.colorBackground(Color.CYAN)));

        setData(new AppLayoutData(Map.of(

        )));
    }

    @Override
    public void setup(String previousLayoutName) {
    }

    @Override
    public String getName() {
        return NAME;
    }
}
