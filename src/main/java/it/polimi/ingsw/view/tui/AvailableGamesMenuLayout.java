package it.polimi.ingsw.view.tui;

import it.polimi.ingsw.view.tui.terminal.drawable.*;
import it.polimi.ingsw.view.tui.terminal.drawable.app.AppLayout;
import it.polimi.ingsw.view.tui.terminal.drawable.app.AppLayoutData;
import it.polimi.ingsw.view.tui.terminal.drawable.menu.Button;
import it.polimi.ingsw.view.tui.terminal.drawable.menu.value.TextBox;
import it.polimi.ingsw.view.tui.terminal.drawable.menu.value.ValueMenuEntry;
import it.polimi.ingsw.view.tui.terminal.drawable.orientedlayout.FullyResizableOrientedLayout;
import it.polimi.ingsw.view.tui.terminal.drawable.orientedlayout.OrientedLayout;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.Color;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.PrimitiveSymbol;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AvailableGamesMenuLayout extends AppLayout {
    public static final String NAME = "AVAILABLE_GAMES_MENU";

    private final TextBox usernameTextBox = new TextBox().unfocusable();
    private final Drawable displayUsername = new OrientedLayout(Orientation.HORIZONTAL,
        new TextBox().text("Logged in as: ").unfocusable().weight(1),
        usernameTextBox.color(Color.RED).weight(1)
    ).center().crop().fixSize(new DrawableSize(3, 25));
    private final Drawable noAvailableGamesTextBox = new TextBox().text("There aren't available games!")
        .unfocusable().center();

    static class JoinableGameDrawable extends FixedLayoutDrawable<Drawable> {
        private final TextBox textBox = new TextBox();
        private final Button button = new Button("Join");

        public JoinableGameDrawable() {
            setLayout(new FullyResizableOrientedLayout(Orientation.HORIZONTAL,
                textBox.unfocusable().center().crop().weight(1),
                button.center().crop().weight(1)
            ).fixSize(new DrawableSize(5, 80)).addBorderBox());
        }
    }

    private final RecyclerDrawable<JoinableGameDrawable, String> recyclerGamesList = new RecyclerDrawable<>(Orientation.VERTICAL,
        JoinableGameDrawable::new);
    private final AlternativeDrawable alternative = new AlternativeDrawable(noAvailableGamesTextBox, recyclerGamesList.center()
                .scrollable());

    private final ValueMenuEntry<String> gameNameEntry = new ValueMenuEntry<>("New game's name", new TextBox());
    private final Button createNewGameButton = new Button("Create new game");
    private final Button backToLoginButton = new Button("Back to login");

    private List<String> availableGames = new ArrayList<>();

    public AvailableGamesMenuLayout() {
        setLayout(new OrientedLayout(Orientation.VERTICAL,
            displayUsername.alignUpLeft().weight(1),
            alternative.center().weight(7),
            new OrientedLayout(Orientation.HORIZONTAL,
                new Fill(PrimitiveSymbol.EMPTY).weight(1),
                gameNameEntry.center().weight(1),
                createNewGameButton.center().weight(1),
                backToLoginButton.center().weight(1),
                new Fill(PrimitiveSymbol.EMPTY).weight(1)
            ).center().weight(2)
        ).center().crop());

        setData(new AppLayoutData(Map.of(

        )));

        backToLoginButton.onpress(() -> switchAppLayout(LoginMenuLayout.NAME));
    }

    @Override
    public void setup(String previousLayoutName) {
        if (previousLayoutName.equals(LoginMenuLayout.NAME)) {
            usernameTextBox.text(appDataProvider.getString(LoginMenuLayout.NAME, "username"));

            // Populating availableGames with mock data.
            availableGames = List.of( "game1", "game2", "game3", "game4", "game5" );

            if (availableGames.size() > 0) {
                recyclerGamesList.populate(availableGames, (joinableGameDrawable, name) -> {
                    joinableGameDrawable.textBox.text(name);
                });

                alternative.second();
            }
        }
    }

    @Override
    public String getName() {
        return NAME;
    }
}
