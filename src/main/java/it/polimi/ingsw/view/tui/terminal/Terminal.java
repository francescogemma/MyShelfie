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
 *
 * @author Cristiano Migali
 */
public abstract class Terminal {
    /**
     * It is the instance of the Terminal used to implement a singleton pattern.
     */
    private static Terminal INSTANCE = null;

    /**
     * This is the only way to retrieve a Terminal instance. Indeed this class implements a singleton pattern.
     *
     * @return the Terminal instance according to the OS on which the program is running.
     * @throws TerminalException if the terminal emulator which is running the program doesn't support raw mode.
     */
    public static Terminal getInstance() throws TerminalException {
        if (INSTANCE == null) {
            INSTANCE = Platform.isWindows() ? WindowsTerminal.getInstance() : UnixTerminal.getInstance();
        }

        return INSTANCE;
    }

    // https://learn.microsoft.com/en-us/windows/console/console-virtual-terminal-sequences#cursor-keys

    /**
     * ANSI escape code representing up arrow key.
     */
    public static final String UP_ARROW = "\033[A";

    /**
     * ANSI escape code representing down arrow key.
     */
    public static final String DOWN_ARROW = "\033[B";

    /**
     * ANSI escape code representing right arrow key.
     */
    public static final String RIGHT_ARROW = "\033[C";

    /**
     * ANSI escape code representing left arrow key.
     */
    public static final String LEFT_ARROW = "\033[D";

    /**
     * Set of strings each containing a single character which is a decimal digit.
     */
    public static final Set<String> NUMBERS = Set.of(
        "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"
    );

    /**
     * Set of strings each containing a single character which can be an upper or lower case letter or other punctuation
     * symbols.
     */
    public static final Set<String> TEXT = Set.of(
        "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q",
        "r", "s", "t", "u", "v", "w", "x", "y", "z", "-", "_",
        "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U",
        "V", "W", "X", "Y", "Z", "."
    );

    /**
     * It is the set of input keys that the Terminal processes when they are read from the input stream.
     */
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

    /**
     * Sets the Terminal to raw mode. In raw mode input echo is disabled, furthermore there is no need to wait
     * for the user to press enter to be able to read characters.
     *
     * @throws TerminalException if the terminal emulator on which the program is being executed doesn't support
     * raw mode.
     */
    protected abstract void enableRawMode() throws TerminalException;

    /**
     * Restores the terminal to its original configuration.
     *
     * @throws TerminalException if the system call which tries to restore the original configuration fails for some
     * reason.
     */
    protected abstract void disableRawMode() throws TerminalException;

    /**
     * Clears the terminal screen through an ANSI escape sequence.
     */
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

    /**
     * Hides the cursor of the terminal through an ANSI escape sequence.
     */
    private void hideCursor() {
        /* We are using ANSI escape codes to hide the cursor.
         * Take a look at https://gist.github.com/fnky/458719343aabd01cfb17a3a4f7296797.
         */
        System.out.print("\033[?251");
        System.out.flush();
    }

    /**
     * Shows the cursor of the terminal through an ANSI escape sequence.
     */
    private void showCursor() {
        /* We are using ANSI escape codes to make the cursor visible again.
         * Take a look at https://gist.github.com/fnky/458719343aabd01cfb17a3a4f7296797.
         */
        System.out.print("\033[?25h");
        System.out.flush();
    }

    /**
     * @return the current {@link TerminalSize}.
     * @throws TerminalException if the system call which tries to retrieve the current size fails for some reason.
     */
    protected abstract TerminalSize getSize() throws TerminalException;

    /**
     * It is the maximum number of lines allowed for a terminal screen.
     */
    private static final int MAX_LINES = 1000;

    /**
     * It is the maximum number of columns allowed for a terminal screen.
     */
    private static final int MAX_COLUMNS = 1000;

    /**
     * Matrix which for every line and column number of the cells on the terminal stores the Symbol which is currently
     * being displayed.
     */
    private final Symbol[][] oldScreenBuffer = new Symbol[MAX_LINES][MAX_COLUMNS];

    /**
     * Matrix which for every line and column number of the cells on the terminal stores the next Symbol that has to be
     * shown.
     */
    private final Symbol[][] screenBuffer = new Symbol[MAX_LINES][MAX_COLUMNS];

    /**
     * It is the number of milliseconds after which the terminal screen should be redrawn.
     */
    private static final long DRAWING_TIMEOUT = 33;

    /**
     * It is true iff there is an {@link App} ran by the terminal which needs to be constantly redrawn.
     */
    private boolean hasToDraw = false;

    /**
     * It is true iff the drawing thread which constantly redraws the {@link App} on screen is still running.
     */
    private boolean drawing = false;

    /**
     * Lock object used to synchronize the start of {@link App}s on a Terminal instance. In particular a Terminal
     * can run at most one {@link App} at the time, if a thread asks to start an {@link App} before the one in execution
     * has terminated, the thread will wait on this lock.
     */
    private final Object startLock = new Object();

    /**
     * Lock object held by the drawing thread while it is redrawing the screen according to the {@link App} Drawable.
     * Every thread which modifies data that will be drawn on screen has to synchronize on this lock.
     */
    private final Object drawingLock = new Object();

    /**
     * It is the current {@link TerminalSize}.
     */
    private TerminalSize size;

    /**
     * It is the {@link AppLayout} which is currently being displayed on screen.
     */
    private AppLayout appLayout;

    /**
     * Reads a character from standard input. It is capable of reading also special characters like keyboard arrows.
     *
     * @return the ASCII code or part of the ANSI escape sequence of the character in input.
     * @throws IOException if some problem has occurred while reading from standard input.
     */

    public abstract int read() throws IOException;

    /**
     * Starts the execution of an {@link App} on the Terminal. A Terminal is capable of running at most one {@link App}
     * at the time, so if a thread invokes this method while another App is running, it will wait on a lock object
     * for the other App to quit.
     * This method starts two threads: one waits for input on standard input, the other constantly redraws the screen
     * according to the current {@link AppLayout}.
     * Furthermore it puts the Terminal in raw mode and restore it to the original configuration when the App terminates.
     * When a terminal is in raw mode input echo is disabled and there is no need to wait for the user to press enter
     * in order to read input.
     *
     * @param app is the {@link App} that will be executed on the Terminal.
     * @return the {@link AppLayoutData} produced by the last layout in the {@link App} before it had terminated.
     * @throws TerminalException if the terminal emulator on which the program is running doesn't support raw mode.
     */
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

    /**
     * @return a string which can be printed to bring a terminal screen displaying symbols according to
     * {@link Terminal#oldScreenBuffer} to one displaying symbols according to {@link Terminal#screenBuffer}.
     * In particular, if no resize happened, the terminal screen isn't fully redrawn. In the string there are
     * ANSI escape sequences which place the terminal cursor in specific cells in order to update only the
     * areas of the screen that actually changed with respect to {@link Terminal#oldScreenBuffer}.
     */
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

    /**
     * Clears the screen buffer in input, that is it sets all its entries to {@link PrimitiveSymbol#EMPTY}.
     *
     * @param screenBuffer is the screen buffer that will be cleared.
     */
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
