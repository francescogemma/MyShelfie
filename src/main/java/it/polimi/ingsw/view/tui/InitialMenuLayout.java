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

/**
 * {@link AppLayout} which allows the user to choose between TUI or GUI interface type and
 * TCP or RMI connection type.
 *
 * @author Cristiano Migali
 */
public class InitialMenuLayout extends AppLayout {
    /**
     * Unique name of the initial menu layout required to allow other AppLayouts to tell
     * the {@link App} to switch to this layout.
     *
     * @see AppLayout
     */
    public static final String NAME = "INITIAL_MENU";

    // Layout:

    /**
     * It is the {@link TextBox} displayed on startup as a splash screen.
     */
    private final TextBox splashTextBox = new TextBox().text(
"""
 ┌─┐  ┌─┐            ____   ┌─┐            ┌─┐   __   _          
 │  \\/  │ ┌─┐ ┌─┐   / ___│  │ └───┐   ___  │ │  / _│ (_)   ___   
 │ │\\/│ │ │ │ │ │   \\___ \\  │ ┌─┐ │  / _ \\ │ │ │ │_  │ │  / _ \\  
 │ │  │ │ │ └─┘ │    ___) │ │ │ │ │ │  __/ │ │ │  _│ │ │ │  __/  
 └─┘  └─┘  \\__, │   │____/  └─┘ └─┘  \\___│ └─┘ └─┘   └─┘  \\___│  
           │___/                                                                                 
"""
    ).unfocusable().color(Color.YELLOW).bold();

    /**
     * {@link ValueMenuEntry} which allows the user to choose among TUI and GUI interface.
     */
    private final ValueMenuEntry<String> interfaceEntry = new ValueMenuEntry<>("Interface type",
        new Options("TUI", "GUI"));

    /**
     * {@link ValueMenuEntry} which allows the user to choose among TCP and RMI connection type.
     */
    private final ValueMenuEntry<String> connectionEntry = new ValueMenuEntry<>("Connection type",
        new Options("TCP", "RMI"));

    /**
     * {@link Button} which allows the user to confirm its choice and switch to the {@link ConnectionMenuLayout}.
     */
    private final Button nextButton = new Button("Next");

    /**
     * {@link Button} which allows the user to quit from the game.
     */
    private final Button exitButton = new Button("Exit");

    /**
     * {@link AlternativeDrawable} used to switch between the splash screen realized through {@link InitialMenuLayout#splashTextBox}
     * and the actual menu.
     */
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

    // Data:

    /**
     * It is true iff the player has pressed the exit button.
     */
    private boolean wantsToExit = false;

    /**
     * Constructor of the class.
     * It initializes the layout in which all the elements are arranged and sets the {@link Button}s callbacks.
     */
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
        if (!previousLayoutName.equals(App.START_NAME)) {
            throw new IllegalStateException("You can reach InitialMenuLayout only from start");
        }

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                synchronized (getLock()) {
                    alternativeDrawable.second();
                }
            }
        }, 1000);
    }

    @Override
    public void beforeSwitch() {

    }

    @Override
    public String getName() {
        return NAME;
    }
}
