package it.polimi.ingsw.view.tui;

import it.polimi.ingsw.view.tui.terminal.drawable.*;
import it.polimi.ingsw.view.tui.terminal.drawable.app.AppLayout;
import it.polimi.ingsw.view.tui.terminal.drawable.app.AppLayoutData;
import it.polimi.ingsw.view.tui.terminal.drawable.menu.Button;
import it.polimi.ingsw.view.tui.terminal.drawable.menu.value.TextBox;
import it.polimi.ingsw.view.tui.terminal.drawable.orientedlayout.OrientedLayout;
import it.polimi.ingsw.view.tui.terminal.drawable.orientedlayout.OrientedLayoutElement;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.PrimitiveSymbol;

import java.util.List;
import java.util.Map;

public class LobbyLayout extends AppLayout {
    public static final String NAME = "LOBBY";

    private final TextBox gameTextBox = new TextBox().unfocusable();
    private static class PlayerDrawable extends FixedLayoutDrawable<Drawable> {
        private final TextBox textBox = new TextBox().hideCursor();

        public PlayerDrawable() {
            setLayout(new OrientedLayout(Orientation.HORIZONTAL,
                    new TextBox().text("Player: ").unfocusable().center().weight(1),
                    textBox.center().weight(1)
                ).center().crop().fixSize(new DrawableSize(6, 50))
                .addBorderBox()
            );
        }
    }
    private final RecyclerDrawable<PlayerDrawable, String> recyclerPlayersList = new RecyclerDrawable<>(Orientation.VERTICAL,
        PlayerDrawable::new);
    private final Button startButton = new Button("Start game");
    private final OrientedLayoutElement startButtonLayoutElement = startButton.center().weight(1);
    private final Button backButton = new Button("Back");

    public LobbyLayout() {
        setLayout(new OrientedLayout(Orientation.VERTICAL,
            new OrientedLayout(Orientation.HORIZONTAL,
                new TextBox().text("Game: ").unfocusable().weight(1),
                gameTextBox.weight(1)
            ).center().crop().fixSize(new DrawableSize(3, 40)).center().weight(1),
            recyclerPlayersList.center().scrollable().weight(4),
            new OrientedLayout(Orientation.HORIZONTAL,
                new Fill(PrimitiveSymbol.EMPTY).weight(1),
                startButtonLayoutElement,
                backButton.center().weight(1),
                new Fill(PrimitiveSymbol.EMPTY).weight(1)
            ).weight(1)
        ).center().crop());

        setData(new AppLayoutData(
            Map.of(

            )
        ));

        startButton.onpress(() -> switchAppLayout(GameLayout.NAME));

        backButton.onpress(() -> switchAppLayout(AvailableGamesMenuLayout.NAME));
    }

    @Override
    public void setup(String previousLayoutName) {
        if (previousLayoutName.equals(AvailableGamesMenuLayout.NAME)) {
            gameTextBox.text(appDataProvider
                .getString(AvailableGamesMenuLayout.NAME, "selectedgame"));

            // Populating players data with mock data
            List<String> players = List.of("mario", "foo", "luca", "gianni");

            recyclerPlayersList.populate(players, ((playerDrawable, player) -> playerDrawable.textBox.text(player)));

            boolean isOwner = true;

            if (isOwner) {
                startButtonLayoutElement.setWeight(1);

                if (players.size() > 2) {
                    startButton.focusable(true);
                } else {
                    startButton.focusable(false);
                }
            } else {
                startButtonLayoutElement.setWeight(0);
            }
        }
    }

    @Override
    public String getName() {
        return NAME;
    }
}
