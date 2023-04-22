package it.polimi.ingsw.view.tui;

import it.polimi.ingsw.view.tui.terminal.drawable.Orientation;
import it.polimi.ingsw.view.tui.terminal.drawable.app.AppLayout;
import it.polimi.ingsw.view.tui.terminal.drawable.app.AppLayoutData;
import it.polimi.ingsw.view.tui.terminal.drawable.menu.Button;
import it.polimi.ingsw.view.tui.terminal.drawable.menu.value.Options;
import it.polimi.ingsw.view.tui.terminal.drawable.menu.value.ValueMenuEntry;
import it.polimi.ingsw.view.tui.terminal.drawable.orientedlayout.OrientedLayout;

import java.util.Map;

public class InitialMenuLayout extends AppLayout {
    public static final String NAME = "INITIAL_MENU";

    private final ValueMenuEntry<String> interfaceEntry = new ValueMenuEntry<>("Interface type",
        new Options("TUI", "GUI"));
    private final ValueMenuEntry<String> connectionEntry = new ValueMenuEntry<>("Connection type",
        new Options("TCP", "RMI"));
    private final Button nextButton = new Button("Next");

    public InitialMenuLayout() {
        setLayout(new OrientedLayout(Orientation.VERTICAL,
            interfaceEntry.center().weight(1),
            connectionEntry.center().weight(1),
            nextButton.center().weight(1)
        ).center().scrollable().alignUpLeft().crop());

        setData(new AppLayoutData(
            Map.of(
                "interface", interfaceEntry::getValue,
                "connection", connectionEntry::getValue
            )
        ));

        nextButton.onpress(this::mustExit);
    }

    @Override
    public void setup(String previousLayoutName) {
        // There is only one layout.
    }

    @Override
    public String getName() {
        return NAME;
    }
}
