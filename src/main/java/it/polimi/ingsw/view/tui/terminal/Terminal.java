package it.polimi.ingsw.view.tui.terminal;

import it.polimi.ingsw.view.tui.terminal.drawable.Coordinate;
import it.polimi.ingsw.view.tui.terminal.drawable.app.App;
import it.polimi.ingsw.view.tui.terminal.drawable.app.AppLayout;
import it.polimi.ingsw.view.tui.terminal.drawable.app.AppLayoutData;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.PrimitiveSymbol;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.Symbol;
import com.sun.jna.Platform;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Defines basic functionalities useful to manage a terminal window.
 */
public abstract class Terminal {
    private static Terminal INSTANCE = null;

    public static Terminal getInstance() throws TerminalException {
        if (INSTANCE == null) {
            INSTANCE = Platform.isWindows() ? WindowsTerminal.getInstance() : UnixTerminal.getInstance();
        }

        return INSTANCE;
    }

    // https://learn.microsoft.com/en-us/windows/console/console-virtual-terminal-sequences#cursor-keys
    public static final String UP_ARROW = "\033[A";
    public static final String DOWN_ARROW = "\033[B";
    public static final String RIGHT_ARROW = "\033[C";
    public static final String LEFT_ARROW = "\033[D";

    public static final Set<String> NUMBERS = Set.of(
        "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"
    );

    public static final Set<String> TEXT = Set.of(
        "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q",
        "r", "s", "t", "u", "v", "w", "x", "y", "z", "-", "_",
        "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U",
        "V", "W", "X", "Y", "Z", "."
    );

    private static final Set<String> ALLOWED_INPUT_KEYS = new HashSet<>();

    static {
        ALLOWED_INPUT_KEYS.addAll(NUMBERS);
        ALLOWED_INPUT_KEYS.addAll(TEXT);
        ALLOWED_INPUT_KEYS.addAll(Set.of(
            UP_ARROW, DOWN_ARROW, RIGHT_ARROW, LEFT_ARROW,
                "\t", "\r",
                "\b", " "
        ));
    }

    protected abstract void enableRawMode() throws TerminalException;

    protected abstract void disableRawMode() throws TerminalException;

    private void clear() {
        /* We are using ANSI escape codes to clear the terminal.
         * Take a look at https://gist.github.com/fnky/458719343aabd01cfb17a3a4f7296797.
         * "\033[H" moves the cursor to (1, 1), i.e. the top left position in the terminal window.
         * "\033[2J" clears the lines visible on screen.
         * "\033[3J" clears the scrollback buffer.
         */
        System.out.print("\033[H\033[2J\033[3J");
        System.out.flush();
    }

    private void hideCursor() {
        /* We are using ANSI escape codes to hide the cursor.
         * Take a look at https://gist.github.com/fnky/458719343aabd01cfb17a3a4f7296797.
         */
        System.out.print("\033[?251");
        System.out.flush();
    }

    private void showCursor() {
        /* We are using ANSI escape codes to make the cursor visible again.
         * Take a look at https://gist.github.com/fnky/458719343aabd01cfb17a3a4f7296797.
         */
        System.out.print("\033[?25h");
        System.out.flush();
    }

    protected abstract TerminalSize getSize() throws TerminalException;

    private static final int MAX_LINES = 1000;
    private static final int MAX_COLUMNS = 1000;
    private final Symbol[][] oldScreenBuffer = new Symbol[MAX_LINES][MAX_COLUMNS];
    private final Symbol[][] screenBuffer = new Symbol[MAX_LINES][MAX_COLUMNS];

    private static final long DRAWING_TIMEOUT = 33;

    private boolean hasToDraw = false;
    private boolean drawing = false;

    private final Object startLock = new Object();
    private final Object drawingLock = new Object();

    private TerminalSize size;
    private AppLayout appLayout;

    public abstract int read() throws IOException;

    public AppLayoutData start(App app) throws TerminalException {
        app.setLock(drawingLock);

        synchronized (startLock) {
            enableRawMode();

            clear();

            clear(oldScreenBuffer);
            clear(screenBuffer);

            hideCursor();

            try {
                size = getSize();
            } catch (IllegalArgumentException e) {
                throw new TerminalException("Got exception while trying to retrieve terminal size before starting"
                    + "to draw: " + e.getMessage());
            }

            appLayout =  app.getNextAppLayout();

            hasToDraw = true;
            drawing = true;

            new Thread(() -> {
                StringBuilder input = new StringBuilder(ALLOWED_INPUT_KEYS.stream().mapToInt(String::length)
                    .max().orElse(4));

                while (true) {
                    synchronized (drawingLock) {
                        if (app.mustExit()) {
                            hasToDraw = false;
                            drawingLock.notifyAll();
                            return;
                        }
                    }

                    if (ALLOWED_INPUT_KEYS.stream().noneMatch(s -> s.startsWith(input.toString()) &&
                        !s.equals(input.toString()))) {
                        input.setLength(0);
                    }

                    /* Due to locks inside methods that access standard input, enableRawMode and disableRawMode
                     * can't finish if a call to the read method is in execution. So, if the read method gets invoked
                     * before enableRawMode; enableRawMode won't finish until the user inputs "\n" (remember that,
                     * since enableRawMode is still in execution, the terminal is still in cooked mode, and hence reads
                     * only returns when "\n" is entered). By the way start is written, we call enableRawMode always
                     * before this thread has been started and we call disableRawMode always when this thread will
                     * not call read anymore.
                     */
                    try {
                        final int readByte = read();
                        input.append((char) readByte);
                    } catch (IOException e) {

                    }

                    synchronized (drawingLock) {
                        if (ALLOWED_INPUT_KEYS.contains(input.toString())) {
                            appLayout.handleInput(input.toString());

                            appLayout = app.getNextAppLayout();
                        }
                    }
                }
            }).start();

            new Thread(() -> {
                synchronized (drawingLock) {
                    while (hasToDraw) {
                        appLayout = app.getNextAppLayout();

                        TerminalSize newSize = getSize();

                        // Calling newSize.equals instead of size.equals, since size could be null.
                        if (!newSize.equals(size)) {
                            clear();

                            clear(oldScreenBuffer);
                            size = newSize;
                        }

                        appLayout.askForSize(size.toDrawableSize());

                        for (int line = 1; line <= size.lines; line++) {
                            for (int column = 1; column <= size.columns; column++) {
                                screenBuffer[line-1][column-1] = appLayout.getSymbolAt(new Coordinate(line, column));
                            }
                        }

                        System.out.print(diffString());
                        System.out.flush();

                        for (int line = 1; line <= size.lines; line++) {
                            for (int column = 1; column <= size.columns; column++) {
                                oldScreenBuffer[line-1][column-1] = screenBuffer[line-1][column-1];
                            }
                        }

                        try {
                            drawingLock.wait(DRAWING_TIMEOUT);
                        } catch (InterruptedException e) {

                        }
                    }

                    drawing = false;
                    drawingLock.notifyAll();
                }
            }).start();

            synchronized (drawingLock) {
                while (drawing) {
                    try {
                        drawingLock.wait();
                    } catch (InterruptedException e) {

                    }
                }
            }

            showCursor();

            clear();

            disableRawMode();

            return appLayout.getData();
        }
    }

    private StringBuilder diffString() {
        StringBuilder diff = new StringBuilder();

        boolean lastWasDifferent;

        for (int line = 1; line <= size.lines; line++) {
            lastWasDifferent = false;
            for (int column = 1; column <= size.columns; column++) {
                if (screenBuffer[line-1][column-1] != oldScreenBuffer[line-1][column-1]) {
                    if (!lastWasDifferent) {
                        lastWasDifferent = true;
                        // Setting cursor position
                        diff.append("\033[").append(line).append(";").append(column).append("H");
                    }
                    diff.append(screenBuffer[line-1][column-1].asString());
                } else {
                    lastWasDifferent = false;
                }
            }
        }

        // Moves cursor to the lower right corner after redrawing the screen
        return diff.append("\033[").append(size.lines).append(";").append(size.columns).append("H");
    }

    private void clear(Symbol[][] screenBuffer) {
        for (Symbol[] line : screenBuffer) {
            Arrays.fill(line, PrimitiveSymbol.EMPTY);
        }
    }

    /* // TUI debug utilities
    private static final Long debugLock = System.currentTimeMillis();
    private static int count = 0;
    private static final File debugLog = new File("debug-" + debugLock + ".txt");

    static {
        try {
            debugLog.createNewFile();
        } catch (IOException e) {

        }
    }

    public static void debug(String toPrint) {
        synchronized (debugLock) {
            try {
                PrintWriter pW = new PrintWriter(new FileWriter(debugLog, true));
                pW.println("[" + count++ + "] " + toPrint);
                pW.close();
            } catch (IOException e) {

            }
        }
    } */
}
