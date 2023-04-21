package it.polimi.ingsw.view.tui.terminal;

import com.sun.jna.*;
import com.sun.jna.Structure.FieldOrder;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Queue;

/* Code inspired from https://github.com/marcobehlerjetbrains/text-editor/blob/episode-4/Viewer.java
 * (relative tutorial series: https://www.youtube.com/playlist?list=PLIRBoI92yManB1eHCupZ6iG61qMTA9hWe).
 */

/**
 * Implements basic functionalities to manage a terminal in Windows.
 */
class WindowsTerminal extends Terminal {
    private static WindowsTerminal INSTANCE = null;
    public static WindowsTerminal getInstance() throws TerminalException {
        if (INSTANCE == null) {
            INSTANCE = new WindowsTerminal();
        }

        return INSTANCE;
    }

    private int originalInputMode;
    private final Pointer stdInputHandle;
    private final Pointer stdOutputHandle;

    private WindowsTerminal() throws TerminalException {
        try {
            stdInputHandle = Kernel32.INSTANCE.GetStdHandle(Kernel32.STD_INPUT_HANDLE);
        } catch (LastErrorException e) {
            throw new TerminalException("Got error code " + e.getErrorCode() +
                    " while trying to retrieve input handle");
        }

        try {
            stdOutputHandle = Kernel32.INSTANCE.GetStdHandle(Kernel32.STD_OUTPUT_HANDLE);
        } catch (LastErrorException e) {
            throw new TerminalException("Got error code " + e.getErrorCode() +
                    " while trying to retrieve output handle");
        }
    }


    @Override
    protected void enableRawMode() throws TerminalException {
        IntByReference inputMode = new IntByReference();
        try {
            Kernel32.INSTANCE.GetConsoleMode(stdInputHandle, inputMode);
        } catch (LastErrorException e) {
            throw new TerminalException("Got error code " + e.getErrorCode() +
                    " while trying to get original console mode before enabling raw mode");
        }

        originalInputMode = inputMode.getValue();

        /* We want to configure the console to raw mode as described in
         * https://learn.microsoft.com/en-us/windows/console/high-level-console-modes.
         * The source suggests to remove:
         * - line input mode
         * - processed input mode
         * - echo input mode
         * - processed output mode
         * together in order to achieve raw mode.
         * We are going to remove all except for processed output mode, since we want to use ANSI control sequences.
         */
        int rawInputMode = originalInputMode & ~(   Kernel32.ENABLE_ECHO_INPUT      |
                                                    Kernel32.ENABLE_LINE_INPUT      |
                                                    Kernel32.ENABLE_PROCESSED_INPUT   );

        try {
            Kernel32.INSTANCE.SetConsoleMode(stdInputHandle, rawInputMode);
        } catch (LastErrorException e) {
            throw new TerminalException("Got error code " + e.getErrorCode() +
                    " while trying to set the console to raw mode");
        }
    }

    @Override
    protected void disableRawMode() throws TerminalException {
        try {
            Kernel32.INSTANCE.SetConsoleMode(stdInputHandle, originalInputMode);
        } catch (LastErrorException e) {
            throw new TerminalException("Got error code " + e.getErrorCode() +
                    " while trying to restore the original console mode");
        }
    }

    @Override
    protected TerminalSize getSize() throws TerminalException {
        Kernel32.CONSOLE_SCREEN_BUFFER_INFO consoleScreenBufferInfo = new Kernel32.CONSOLE_SCREEN_BUFFER_INFO();

        try {
            Kernel32.INSTANCE.GetConsoleScreenBufferInfo(stdOutputHandle, consoleScreenBufferInfo);
        } catch (LastErrorException e) {
            throw new TerminalException("Got error code " + e.getErrorCode() +
                    " while trying to retrieve information about the console screen buffer");
        }

        try {
            return new TerminalSize(
                    consoleScreenBufferInfo.srWindow.Bottom - consoleScreenBufferInfo.srWindow.Top + 1,
                    consoleScreenBufferInfo.srWindow.Right - consoleScreenBufferInfo.srWindow.Left + 1

            );
        } catch (IllegalArgumentException e) {
            throw new TerminalException("Got exception while constructing TerminalSize object: " + e.getMessage());
        }
    }

    /**
     * Name of library Kernel32.
     */
    private static final String KERNEL32_NAME = "kernel32";

    // Loading Kernel32 as illustrated in https://github.com/java-native-access/jna/blob/master/www/GettingStarted.md.
    public interface Kernel32 extends StdCallLibrary {
        Kernel32 INSTANCE = (Kernel32) Native.load(KERNEL32_NAME, Kernel32.class);

        /* We retrieved the value of STD_INPUT_HANDLE at the following link:
         * https://learn.microsoft.com/it-it/windows/console/getstdhandle.
         */
        int STD_INPUT_HANDLE = -10;

        /* We retrieved the value of STD_OUTPUT_HANDLE at the following link:
         * https://learn.microsoft.com/it-it/windows/console/getstdhandle.
         */
        int STD_OUTPUT_HANDLE = -11;

        /* We retrieved the value of ENABLE_ECHO_INPUT at the following link:
         * https://learn.microsoft.com/it-it/windows/console/setconsolemode?source=recommendations.
         */
        int ENABLE_ECHO_INPUT = 0x0004;

        /* We retrieved the value of ENABLE_LINE_INPUT at the following link:
         * https://learn.microsoft.com/it-it/windows/console/setconsolemode?source=recommendations.
         */
        int ENABLE_LINE_INPUT = 0x0002;

        /* We retrieved the value of ENABLE_PROCESSED_INPUT at the following link:
         * https://learn.microsoft.com/it-it/windows/console/setconsolemode?source=recommendations.
         */
        int ENABLE_PROCESSED_INPUT = 0x0001;

        /* Defining GetStdHandle signature as in https://learn.microsoft.com/it-it/windows/console/getstdhandle.
         *
         * By the mappings to primitive types explained in
         * https://github.com/java-native-access/jna/blob/master/www/Mappings.md:
         * DWORD -> int
         */
        Pointer GetStdHandle(int nStdHandle) throws LastErrorException;

        /* Defining GetConsoleMode signature as in https://learn.microsoft.com/it-it/windows/console/getconsolemode.
         *
         * We retrieved the primitive type aliased by HANDLE and LPDWORD in
         * https://learn.microsoft.com/en-us/windows/win32/winprog/windows-data-types.
         *
         * For HANDLE, which is a void pointer which pointed memory is allocated by the library,
         * we are using the Pointer class as explained in
         * https://java-native-access.github.io/jna/4.2.1/com/sun/jna/Pointer.html.
         * Since lpMode is an int* argument, we are using IntByReference as explained in
         * https://github.com/java-native-access/jna/blob/master/www/ByRefArguments.md.
         */
        boolean GetConsoleMode(Pointer hConsoleHandle, IntByReference lpMode) throws LastErrorException;

        /* Defining SetConsoleMode signature as in https://learn.microsoft.com/it-it/windows/console/setconsolemode.
         *
         * We retrieved the primitive type aliased by HANDLE in
         * https://learn.microsoft.com/en-us/windows/win32/winprog/windows-data-types.
         *
         * For HANDLE, we are using the Pointer class, for the reasons explained in the comment above.
         *
         * By the mappings to primitive types explained in
         * https://github.com/java-native-access/jna/blob/master/www/Mappings.md:
         * DWORD -> int
         */
        boolean SetConsoleMode(Pointer hConsoleHandle, int dwMode) throws LastErrorException;

        /* Defining COORD struct as explained in
         * https://github.com/java-native-access/jna/blob/master/www/StructuresAndUnions.md.
         * The original COORD struct definition in https://learn.microsoft.com/en-us/windows/console/coord-str is:
         * typedef struct _COORD {
         *     SHORT X;
         *     SHORT Y;
         * } COORD, *PCOORD;
         *
         * We retrieved the primitive type aliased by SHORT in
         * https://learn.microsoft.com/en-us/windows/win32/winprog/windows-data-types.
         *
         * By the mappings to primitive types explained in
         * https://github.com/java-native-access/jna/blob/master/www/Mappings.md:
         * SHORT = short -> short
         */
        @FieldOrder({ "X", "Y" })
        class COORD extends Structure {
            public short X;
            public short Y;
        }

        /* Defining SMALL_RECT struct as explained in
         * https://github.com/java-native-access/jna/blob/master/www/StructuresAndUnions.md.
         * The original SMALL_RECT struct definition in https://learn.microsoft.com/en-us/windows/console/small-rect-str
         * is:
         * typedef struct _SMALL_RECT {
         *     SHORT Left;
         *     SHORT Top;
         *     SHORT Right;
         *     SHORT Bottom;
         * } SMALL_RECT;
         *
         * We retrieved the primitive type aliased by SHORT in
         * https://learn.microsoft.com/en-us/windows/win32/winprog/windows-data-types.
         *
         * By the mappings to primitive types explained in
         * https://github.com/java-native-access/jna/blob/master/www/Mappings.md:
         * SHORT = short -> short
         */
        @FieldOrder({ "Left", "Top", "Right", "Bottom" })
        class SMALL_RECT extends Structure {
            public short Left;
            public short Top;
            public short Right;
            public short Bottom;
        }

        /* Defining CONSOLE_SCREEN_BUFFER_INFO struct as explained in
         * https://github.com/java-native-access/jna/blob/master/www/StructuresAndUnions.md.
         * The original CONSOLE_SCREEN_BUFFER_INFO struct definition in
         * https://learn.microsoft.com/en-us/windows/console/console-screen-buffer-info-str is:
         * typedef struct _CONSOLE_SCREEN_BUFFER_INFO {
         *     COORD      dwSize;
         *     COORD      dwCursorPosition;
         *     WORD       wAttributes;
         *     SMALL_RECT srWindow;
         *     COORD      dwMaximumWindowSize;
         * } CONSOLE_SCREEN_BUFFER_INFO;
         *
         * We retrieved the primitive type aliased by WORD in
         * https://learn.microsoft.com/en-us/windows/win32/winprog/windows-data-types.
         *
         * By the mappings to primitive types explained in
         * https://github.com/java-native-access/jna/blob/master/www/Mappings.md:
         * WORD = short -> short
         *
         * As explained in https://java-native-access.github.io/jna/4.2.0/com/sun/jna/Structure.html
         * when we use a Structure inside a Structure (like COORD or SMALL_RECT) it corresponds to using struct and
         * not struct*, as in the original struct definition.
         */
        @FieldOrder({ "dwSize", "dwCursorPosition", "wAttributes", "srWindow", "drMaximumWindowSize" })
        class CONSOLE_SCREEN_BUFFER_INFO extends Structure {
            public COORD dwSize;
            public COORD dwCursorPosition;
            public short wAttributes;
            public SMALL_RECT srWindow;
            public COORD drMaximumWindowSize;
        }

        /* Defining GetConsoleScreenBufferInfo as in
         * https://learn.microsoft.com/it-it/windows/console/getconsolescreenbufferinfo.
         */
        boolean GetConsoleScreenBufferInfo(Pointer hConsoleOutput,
                                           CONSOLE_SCREEN_BUFFER_INFO lpConsoleScreenBufferInfo) throws LastErrorException;


        // https://learn.microsoft.com/en-us/windows/console/focus-event-record-str
        @FieldOrder({ "bSetFocus" })
        class FOCUS_EVENT_RECORD extends Structure {
            public boolean bSetFocus;
        }

        // https://learn.microsoft.com/en-us/windows/console/key-event-record-str
        @FieldOrder({ "bKeyDown", "wRepeatCount", "wVirtualKeyCode", "wVirtualScanCode", "uChar", "dwControlKeyState" })
        class KEY_EVENT_RECORD extends Structure {
            public boolean bKeyDown;
            public short wRepeatCount;
            public short wVirtualKeyCode;
            public short wVirtualScanCode;

            // In the actual struct there is a union, I'm taking the max here
            public short uChar;

            public int dwControlKeyState;
        }

        // https://learn.microsoft.com/en-us/windows/console/menu-event-record-str
        @FieldOrder({ "dwCommandId" })
        class MENU_EVENT_RECORD extends Structure {
            public int dwCommandId;
        }

        // https://learn.microsoft.com/en-us/windows/console/mouse-event-record-str
        @FieldOrder({ "dwMousePosition", "dwButtonState", "dwControlKeyState", "dwEventFlags" })
        class MOUSE_EVENT_RECORD extends Structure {
            public COORD dwMousePosition;
            public int dwButtonState;
            public int dwControlKeyState;
            public int dwEventFlags;
        }

        // https://learn.microsoft.com/en-us/windows/console/window-buffer-size-record-str
        @FieldOrder({ "dwSize" })
        class WINDOW_BUFFER_SIZE_RECORD extends Structure {
            public COORD dwSize;
        }

        // https://learn.microsoft.com/en-us/windows/console/input-record-str
        short FOCUS_EVENT = 0x0010;
        short KEY_EVENT = 0x0001;
        short MENU_EVENT = 0x0008;
        short MOUSE_EVENT = 0x0002;
        short WINDOW_BUFFER_SIZE_EVENT = 0x0004;

        // https://learn.microsoft.com/en-us/windows/console/input-record-str
        @FieldOrder({ "KeyEvent", "MouseEvent", "WindowBufferSizeEvent", "MenuEvent", "FocusEvent" })
        class EVENT_RECORD_UNION extends Union {
            public KEY_EVENT_RECORD KeyEvent;
            public MOUSE_EVENT_RECORD MouseEvent;
            public WINDOW_BUFFER_SIZE_RECORD WindowBufferSizeEvent;
            public MENU_EVENT_RECORD MenuEvent;
            public FOCUS_EVENT_RECORD FocusEvent;
        }

        // https://learn.microsoft.com/en-us/windows/console/input-record-str
        // https://stackoverflow.com/questions/56043874/how-to-map-a-union-in-a-jna-structure
        @FieldOrder({ "EventType", "Event" })
        class INPUT_RECORD extends Structure {
            public short EventType;
            public EVENT_RECORD_UNION Event;

            @Override
            public void read() {
                super.read();

                switch (EventType) {
                    case FOCUS_EVENT -> Event.setType(FOCUS_EVENT_RECORD.class);
                    case KEY_EVENT -> Event.setType(KEY_EVENT_RECORD.class);
                    case MENU_EVENT -> Event.setType(MENU_EVENT_RECORD.class);
                    case MOUSE_EVENT -> Event.setType(MOUSE_EVENT_RECORD.class);
                    case WINDOW_BUFFER_SIZE_EVENT -> Event.setType(WINDOW_BUFFER_SIZE_RECORD.class);
                }

                Event.read();
            }
        }

        // https://learn.microsoft.com/en-us/windows/console/readconsoleinput
        boolean ReadConsoleInputA(Pointer hConsoleInput, INPUT_RECORD lpBuffer, int nLength,
                                 IntByReference lpNumberOfEventsRead) throws LastErrorException;

        // https://learn.microsoft.com/en-us/windows/win32/inputdev/virtual-key-codes
        short VK_LEFT = 0x25;
        short VK_UP = 0x26;
        short VK_RIGHT = 0x27;
        short VK_DOWN = 0x28;
    }

    private final Queue<Integer> readQueue = new ArrayDeque<>();

    @Override
    public int read() throws IOException {
        if (!readQueue.isEmpty()) {
            return readQueue.poll();
        }

        Kernel32.INPUT_RECORD lpBuffer = new Kernel32.INPUT_RECORD();
        IntByReference lpNumberOfEventsRead = new IntByReference();

        while (true) {
            try {
                Kernel32.INSTANCE.ReadConsoleInputA(stdInputHandle, lpBuffer, 1, lpNumberOfEventsRead);
            } catch (LastErrorException e) {
                throw new IOException("Got error code: " + e.getErrorCode() + " while trying to read from console");
            }

            if (lpBuffer.EventType != Kernel32.KEY_EVENT || !lpBuffer.Event.KeyEvent.bKeyDown) {
                continue;
            }

            switch (lpBuffer.Event.KeyEvent.wVirtualKeyCode) {
                case Kernel32.VK_LEFT, Kernel32.VK_UP, Kernel32.VK_RIGHT,
                        Kernel32.VK_DOWN -> {
                    readQueue.add(91);

                    switch (lpBuffer.Event.KeyEvent.wVirtualKeyCode) {
                        case Kernel32.VK_LEFT -> readQueue.add(68);
                        case Kernel32.VK_UP -> readQueue.add(65);
                        case Kernel32.VK_RIGHT -> readQueue.add(67);
                        case Kernel32.VK_DOWN -> readQueue.add(66);
                    }

                    return 27;
                }
            }

            return lpBuffer.Event.KeyEvent.uChar;
        }
    }
}
