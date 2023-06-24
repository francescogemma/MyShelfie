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
 *
 * @author Cristiano Migali
 */
class WindowsTerminal extends Terminal {
    /**
     * It is the instance of the WindowsTerminal used to implement a singleton pattern.
     */
    private static WindowsTerminal INSTANCE = null;

    /**
     * This is the only way of retrieving a WindowsTerminal instance. Indeed this class implements a singleton pattern.
     *
     * @return the instance of the WindowsTerminal.
     */
    public static WindowsTerminal getInstance() throws TerminalException {
        if (INSTANCE == null) {
            INSTANCE = new WindowsTerminal();
        }

        return INSTANCE;
    }

    /**
     * It is the original input mode of the terminal emulator.
     */
    private int originalInputMode;

    /**
     * JNA pointer to the input handle of the Windows terminal emulator.
     */
    private final Pointer stdInputHandle;

    /**
     * JNA pointer to the output handle of the Windows terminal emulator.
     */
    private final Pointer stdOutputHandle;

    /**
     * Constructor of the class.
     *
     * @throws TerminalException if the terminal emulator doesn't allow to retrieve pointers to its input or output
     * handles.
     */
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

    /**
     * Loading Kernel32 as illustrated in https://github.com/java-native-access/jna/blob/master/www/GettingStarted.md.
     */
    public interface Kernel32 extends StdCallLibrary {
        /**
         * Instance of Kernel32 which allows to invoke system calls through JNA.
         */
        Kernel32 INSTANCE = (Kernel32) Native.load(KERNEL32_NAME, Kernel32.class);

        /**
         * We retrieved the value of STD_INPUT_HANDLE at the following link:
         * https://learn.microsoft.com/it-it/windows/console/getstdhandle.
         */
        int STD_INPUT_HANDLE = -10;

        /**
         * We retrieved the value of STD_OUTPUT_HANDLE at the following link:
         * https://learn.microsoft.com/it-it/windows/console/getstdhandle.
         */
        int STD_OUTPUT_HANDLE = -11;

        /**
         * We retrieved the value of ENABLE_ECHO_INPUT at the following link:
         * https://learn.microsoft.com/it-it/windows/console/setconsolemode?source=recommendations.
         */
        int ENABLE_ECHO_INPUT = 0x0004;

        /**
         * We retrieved the value of ENABLE_LINE_INPUT at the following link:
         * https://learn.microsoft.com/it-it/windows/console/setconsolemode?source=recommendations.
         */
        int ENABLE_LINE_INPUT = 0x0002;

        /**
         * We retrieved the value of ENABLE_PROCESSED_INPUT at the following link:
         * https://learn.microsoft.com/it-it/windows/console/setconsolemode?source=recommendations.
         */
        int ENABLE_PROCESSED_INPUT = 0x0001;

        /**
         * Defining GetStdHandle signature as in https://learn.microsoft.com/it-it/windows/console/getstdhandle.
         * By the mappings to primitive types explained in
         * https://github.com/java-native-access/jna/blob/master/www/Mappings.md:
         * DWORD -> int
         *
         * @param nStdHandle The standard device. This parameter can be one of the following values.
         *
         * @throws LastErrorException if an unexpected errors occurs.
         *
         * @return a handle to the specified standard device.
         */
        Pointer GetStdHandle(int nStdHandle) throws LastErrorException;

        /**
         * Defining GetConsoleMode signature as in https://learn.microsoft.com/it-it/windows/console/getconsolemode.
         *
         * We retrieved the primitive type aliased by HANDLE and LPDWORD in
         * https://learn.microsoft.com/en-us/windows/win32/winprog/windows-data-types.
         *
         * For HANDLE, which is a void pointer which pointed memory is allocated by the library,
         * we are using the Pointer class as explained in
         * https://java-native-access.github.io/jna/4.2.1/com/sun/jna/Pointer.html.
         * Since lpMode is an int* argument, we are using IntByReference as explained in
         * https://github.com/java-native-access/jna/blob/master/www/ByRefArguments.md.
         *
         * @param hConsoleHandle  A handle to the console input buffer or the console screen buffer. The handle must have the GENERIC_READ access right. For more information, see Console Buffer Security and Access Rights.
         * @param lpMode  A pointer to a variable that receives the current mode of the specified buffer.
         *
         * @return true iff the function succeeds.
         *
         * @throws LastErrorException if an unexpected errors occurs.
         */
        boolean GetConsoleMode(Pointer hConsoleHandle, IntByReference lpMode) throws LastErrorException;

        /**
         * Defining SetConsoleMode signature as in https://learn.microsoft.com/it-it/windows/console/setconsolemode.
         *
         * We retrieved the primitive type aliased by HANDLE in
         * https://learn.microsoft.com/en-us/windows/win32/winprog/windows-data-types.
         *
         * For HANDLE, we are using the Pointer class, for the reasons explained in the comment above.
         *
         * By the mappings to primitive types explained in
         * https://github.com/java-native-access/jna/blob/master/www/Mappings.md:
         * DWORD -> int
         *
         * @param hConsoleHandle  A handle to the console input buffer or a console screen buffer. The handle must have the GENERIC_READ access right. For more information, see Console Buffer Security and Access Rights.
         * @param dwMode  The input or output mode to be set.
         *
         * @return true iff the consol mode has been set succesfully.
         *
         * @throws LastErrorException if an unexpected error occurs.
         */
        boolean SetConsoleMode(Pointer hConsoleHandle, int dwMode) throws LastErrorException;

        /**
         * Defining COORD struct as explained in
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
            /**
             *  The horizontal coordinate or column value. The units depend on the function call.
             */
            public short X;

            /**
             *  The vertical coordinate or row value. The units depend on the function call.
             */
            public short Y;
        }

        /**
         * Defining SMALL_RECT struct as explained in
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
            /**
             *  The x-coordinate of the upper left corner of the rectangle.
             */
            public short Left;

            /**
             *  The y-coordinate of the upper left corner of the rectangle.
             */
            public short Top;

            /**
             *  The x-coordinate of the lower right corner of the rectangle.
             */
            public short Right;

            /**
             *  The y-coordinate of the lower right corner of the rectangle.
             */
            public short Bottom;
        }

        /**
         * Defining CONSOLE_SCREEN_BUFFER_INFO struct as explained in
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
            /**
             * A COORD structure that contains the size of the console screen buffer, in character columns and rows.
             */
            public COORD dwSize;

            /**
             *  A COORD structure that contains the column and row coordinates of the cursor in the console screen buffer.
             */
            public COORD dwCursorPosition;

            /**
             *  The attributes of the characters written to a screen buffer by the WriteFile and WriteConsole functions, or echoed to a screen buffer by the ReadFile and ReadConsole functions. For more information, see Character Attributes.
             */
            public short wAttributes;

            /**
             *  A SMALL_RECT structure that contains the console screen buffer coordinates of the upper-left and lower-right corners of the display window.
             */
            public SMALL_RECT srWindow;

            /**
             *  A COORD structure that contains the maximum size of the console window, in character columns and rows, given the current screen buffer size and font and the screen size.
             */
            public COORD drMaximumWindowSize;
        }

        /**
         * Defining GetConsoleScreenBufferInfo as in
         * https://learn.microsoft.com/it-it/windows/console/getconsolescreenbufferinfo.
         *
         * @param hConsoleOutput  A handle to the console screen buffer. The handle must have the GENERIC_READ access right. For more information, see Console Buffer Security and Access Rights.
         * @param lpConsoleScreenBufferInfo  A pointer to a CONSOLE_SCREEN_BUFFER_INFO structure that receives the console screen buffer information.
         *
         * @return true iff the retrieval has been successful.
         *
         * @throws LastErrorException if an unexpected error has occurred.
         */
        boolean GetConsoleScreenBufferInfo(Pointer hConsoleOutput,
                                           CONSOLE_SCREEN_BUFFER_INFO lpConsoleScreenBufferInfo) throws LastErrorException;


        /**
         * Defining FOCUS_EVENT_RECORD struct as explained in https://learn.microsoft.com/en-us/windows/console/focus-event-record-str .
         */
        @FieldOrder({ "bSetFocus" })
        class FOCUS_EVENT_RECORD extends Structure {
            /**
             * It is true iff the focus has been set.
             */
            public boolean bSetFocus;
        }

        /**
         * Defining KEY_EVENT_RECORD struct as explained in https://learn.microsoft.com/en-us/windows/console/key-event-record-str .
         */
        @FieldOrder({ "bKeyDown", "wRepeatCount", "wVirtualKeyCode", "wVirtualScanCode", "uChar", "dwControlKeyState" })
        class KEY_EVENT_RECORD extends Structure {
            /**
             *  If the key is pressed, this member is TRUE. Otherwise, this member is FALSE (the key is released).
             */
            public boolean bKeyDown;

            /**
             *  The repeat count, which indicates that a key is being held down. For example, when a key is held down, you might get five events with this member equal to 1, one event with this member equal to 5, or multiple events with this member greater than or equal to 1.
             */
            public short wRepeatCount;

            /**
             *  A virtual-key code that identifies the given key in a device-independent manner.
             */
            public short wVirtualKeyCode;

            /**
             *  The virtual scan code of the given key that represents the device-dependent value generated by the keyboard hardware.
             */
            public short wVirtualScanCode;

            // In the actual struct there is a union, I'm taking the max here
            /**
             *  A union of the following members: UnicodeChar and AsciiChar.
             */
            public short uChar;


            /**
             *  The state of the control keys. This member can be one or more of the following values.
             */
            public int dwControlKeyState;
        }

        /**
         * Defining MENU_EVENT_RECORD struct as explained in https://learn.microsoft.com/en-us/windows/console/menu-event-record-str .
         */
        @FieldOrder({ "dwCommandId" })
        class MENU_EVENT_RECORD extends Structure {
            /**
             * It is the id of the received menu command.
             */
            public int dwCommandId;
        }

        /**
         * Defining MOUSE_EVENT_RECORD struct as explained in https://learn.microsoft.com/en-us/windows/console/mouse-event-record-str .
         */
        @FieldOrder({ "dwMousePosition", "dwButtonState", "dwControlKeyState", "dwEventFlags" })
        class MOUSE_EVENT_RECORD extends Structure {
            /**
             *  A COORD structure that contains the location of the cursor, in terms of the console screen buffer's character-cell coordinates.
             */
            public COORD dwMousePosition;

            /**
             *  The status of the mouse buttons. The least significant bit corresponds to the leftmost mouse button. The next least significant bit corresponds to the rightmost mouse button. The next bit indicates the next-to-leftmost mouse button. The bits then correspond left to right to the mouse buttons. A bit is 1 if the button was pressed.
             */
            public int dwButtonState;

            /**
             *  The state of the control keys. This member can be one or more of the following values.
             */
            public int dwControlKeyState;

            /**
             *  The type of mouse event. If this value is zero, it indicates a mouse button being pressed or released. Otherwise, this member is one of the following values.
             */
            public int dwEventFlags;
        }

        /**
         * Defining WINDOW_BUFFER_SIZE_RECORD struct as explained in https://learn.microsoft.com/en-us/windows/console/window-buffer-size-record-str .
         */
        @FieldOrder({ "dwSize" })
        class WINDOW_BUFFER_SIZE_RECORD extends Structure {
            /**
             *  A COORD structure that contains the size of the console screen buffer, in character cell columns and rows.
             */
            public COORD dwSize;
        }

        // https://learn.microsoft.com/en-us/windows/console/input-record-str

        /**
         * Event type code for the focus event of the terminal window.
         */
        short FOCUS_EVENT = 0x0010;

        /**
         * Event type code for the key event of the terminal window.
         */
        short KEY_EVENT = 0x0001;

        /**
         * Event type code for the menu event of the terminal window.
         */
        short MENU_EVENT = 0x0008;

        /**
         * Event type code for the mouse event of the terminal window.
         */
        short MOUSE_EVENT = 0x0002;

        /**
         * Event type code for the window buffer size event of the terminal window.
         */
        short WINDOW_BUFFER_SIZE_EVENT = 0x0004;

        /**
         * Defining EVENT_RECORD_UNION struct as explained in https://learn.microsoft.com/en-us/windows/console/input-record-str.
         */
        @FieldOrder({ "KeyEvent", "MouseEvent", "WindowBufferSizeEvent", "MenuEvent", "FocusEvent" })
        class EVENT_RECORD_UNION extends Union {
            /**
             * The Event member contains a FOCUS_EVENT_RECORD structure. These events are used internally and should be ignored.
             */
            public KEY_EVENT_RECORD KeyEvent;

            /**
             * The Event member contains a KEY_EVENT_RECORD structure with information about a keyboard event.
             */
            public MOUSE_EVENT_RECORD MouseEvent;

            /**
             * The Event member contains a MENU_EVENT_RECORD structure. These events are used internally and should be ignored.
             */
            public WINDOW_BUFFER_SIZE_RECORD WindowBufferSizeEvent;

            /**
             * The Event member contains a MOUSE_EVENT_RECORD structure with information about a mouse movement or button press event.
             */
            public MENU_EVENT_RECORD MenuEvent;

            /**
             * The Event member contains a WINDOW_BUFFER_SIZE_RECORD structure with information about the new size of the console screen buffer.
             */
            public FOCUS_EVENT_RECORD FocusEvent;
        }

        /**
         * Defining the INPUT_RECORD struct as explained in https://learn.microsoft.com/en-us/windows/console/input-record-str,
         * according to https://stackoverflow.com/questions/56043874/how-to-map-a-union-in-a-jna-structure .
         */
        @FieldOrder({ "EventType", "Event" })
        class INPUT_RECORD extends Structure {
            /**
             *  A handle to the type of input event and the event record stored in the Event member.
             */
            public short EventType;

            /**
             *  The event information. The format of this member depends on the event type specified by the EventType member.
             */
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

        /**
         * Defining ReadConsoleInputA method as explained in https://learn.microsoft.com/en-us/windows/console/readconsoleinput.
         *
         * @param hConsoleInput  A handle to the console input buffer. The handle must have the GENERIC_READ access right. For more information, see Console Buffer Security and Access Rights.
         * @param lpBuffer  A pointer to an array of INPUT_RECORD structures that receives the input buffer data.
         * @param nLength  The size of the array pointed to by the lpBuffer parameter, in array elements.
         * @param lpNumberOfEventsRead  A pointer to a variable that receives the number of input records read.
         *
         * @return true iff the function succeeds.
         *
         * @throws LastErrorException if an unexpected error occurs.
         */
        boolean ReadConsoleInputA(Pointer hConsoleInput, INPUT_RECORD lpBuffer, int nLength,
                                 IntByReference lpNumberOfEventsRead) throws LastErrorException;

        // https://learn.microsoft.com/en-us/windows/win32/inputdev/virtual-key-codes

        /**
         * Virtual key code for keyboard left arrow.
         */
        short VK_LEFT = 0x25;

        /**
         * Virtual key code for keyboard up arrow.
         */
        short VK_UP = 0x26;

        /**
         * Virtual key code for the keyboard right arrow.
         */
        short VK_RIGHT = 0x27;

        /**
         * Virtual key code for the keyboard down arrow.
         */
        short VK_DOWN = 0x28;
    }

    /**
     * Queue of integers used to simulate the input ANSI control sequence for keyboard
     * arrows that is provided on Unix terminals. In particular when the Windows terminal detects an arrow key press it
     * returns (from the {@link WindowsTerminal#read()} method) the escape character and adds to this queue the
     * integer codes for the remaining characters of the
     * ANSI control sequence corresponding to the arrow in input.
     */
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
