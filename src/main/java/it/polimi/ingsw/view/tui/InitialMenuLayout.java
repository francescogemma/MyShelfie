package it.polimi.ingsw.view.tui;

import it.polimi.ingsw.view.tui.terminal.drawable.AlternativeDrawable;
import it.polimi.ingsw.view.tui.terminal.drawable.Fill;
import it.polimi.ingsw.view.tui.terminal.drawable.Orientation;
import it.polimi.ingsw.view.tui.terminal.drawable.app.App;
import it.polimi.ingsw.view.tui.terminal.drawable.app.AppLayout;
import it.polimi.ingsw.view.tui.terminal.drawable.app.AppLayoutData;
import it.polimi.ingsw.view.tui.terminal.drawable.menu.Button;
import it.polimi.ingsw.view.tui.terminal.drawable.menu.value.Options;
import it.polimi.ingsw.view.tui.terminal.drawable.menu.value.TextBox;
import it.polimi.ingsw.view.tui.terminal.drawable.menu.value.ValueMenuEntry;
import it.polimi.ingsw.view.tui.terminal.drawable.orientedlayout.OrientedLayout;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.Color;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.PrimitiveSymbol;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class InitialMenuLayout extends AppLayout {
    public static final String NAME = "INITIAL_MENU";

    private final TextBox splashTextBox = new TextBox().text(
"""
 ┌─┐  ┌─┐            ____   ┌─┐            ┌─┐   __   _          
 │  \\/  │ ┌─┐ ┌─┐   / ___│  │ └───┐   ___  │ │  / _│ (_)   ___   
 │ │\\/│ │ │ │ │ │   \\___ \\  │ ┌─┐ │  / _ \\ │ │ │ │_  │ │  / _ \\  
 │ │  │ │ │ └─┘ │    ___) │ │ │ │ │ │  __/ │ │ │  _│ │ │ │  __/  
 └─┘  └─┘  \\__, │   │____/  └─┘ └─┘  \\___│ └─┘ └─┘   └─┘  \\___│  
           │___/                                                                                 
"""
    ).hideCursor().color(Color.YELLOW).bold();

    private final ValueMenuEntry<String> interfaceEntry = new ValueMenuEntry<>("Interface type",
        new Options("TUI", "GUI"));
    private final ValueMenuEntry<String> connectionEntry = new ValueMenuEntry<>("Connection type",
        new Options("TCP", "RMI"));
    private final Button nextButton = new Button("Next");
    private final Button exitButton = new Button("Exit");

    private boolean wantsToExit = false;

    private final AlternativeDrawable alternativeDrawable = new AlternativeDrawable(
            splashTextBox.center(),
            new OrientedLayout(Orientation.VERTICAL,
                interfaceEntry.center().weight(1),
                connectionEntry.center().weight(1),
                new OrientedLayout(Orientation.HORIZONTAL,
                    new Fill(PrimitiveSymbol.EMPTY).weight(2),
                    nextButton.center().weight(1),
                    exitButton.center().weight(1),
                    new Fill(PrimitiveSymbol.EMPTY).weight(2)
                ).weight(1)
            ).center().scrollable());

    public InitialMenuLayout() {
        setLayout(alternativeDrawable.alignUpLeft().crop());

        setData(new AppLayoutData(
            Map.of(
                "interface", interfaceEntry::getValue,
                "connection", connectionEntry::getValue,
                "exit", () -> wantsToExit
            )
        ));

        nextButton.onpress(this::mustExit);
        exitButton.onpress(() -> {
            wantsToExit = true;
            mustExit();
        });
    }

    @Override
    public void setup(String previousLayoutName) {
        if (previousLayoutName.equals(App.START_NAME)) {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    synchronized (getLock()) {
                        alternativeDrawable.second();
                    }
                }
            }, 1000);
        }
    }

    @Override
    public void beforeSwitch() {

    }

    @Override
    public String getName() {
        return NAME;
    }
}
