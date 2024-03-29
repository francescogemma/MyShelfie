package it.polimi.ingsw.view.tui.terminal;

import com.sun.jna.*;
import com.sun.jna.Structure.FieldOrder;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

/* Code inspired from https://github.com/marcobehlerjetbrains/text-editor/blob/episode-4/Viewer.java
 * (relative tutorial series: https://www.youtube.com/playlist?list=PLIRBoI92yManB1eHCupZ6iG61qMTA9hWe)
 * and from https://viewsourcecode.org/snaptoken/kilo/.
 * These two sources are related to one another: the latter is an implementation of a command line text editor in C,
 * the former is a reimplementation of the same text editor in Java using the JNA library.
 */

/**
 * Implements basic functionalities to manage a terminal on POSIX compliant systems.
 *
 * @author Cristiano Migali
 */
class UnixTerminal extends Terminal {
    /**
     * It is the instance of the UnixTerminal used to implement a singleton pattern.
     */
    private static UnixTerminal INSTANCE = null;

    /**
     * This is the only way of retrieving a UnixTerminal instance. Indeed this class implements a singleton pattern.
     *
     * @return the instance of the UnixTerminal.
     */
    public static UnixTerminal getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new UnixTerminal();
        }

        return INSTANCE;
    }

    /**
     * It is the original termios struct configuration of the terminal emulator.
     */
    private final CLibrary.termios orig_termios = CLibrary.craft_termios();

    /**
     * Constructor of the class.
     */
    private UnixTerminal() {

    }

    @Override
    protected void enableRawMode() throws TerminalException {
        /* Calling tcgetattr as in
         * https://viewsourcecode.org/snaptoken/kilo/02.enteringRawMode.html#disable-raw-mode-at-exit.
         * As documented in https://man7.org/linux/man-pages/man3/termios.3.html it retrieves the current terminal
         * configuration, stored in a termios struct.
         */
        try {
            CLibrary.INSTANCE.tcgetattr(CLibrary.STDIN_FILENO, orig_termios);
        } catch (LastErrorException e) {
            throw new TerminalException("Got error code " + e.getErrorCode() +
                    " while trying to retrieve original terminal configuration");
        }

        /* Copying the retrieved termios struct in raw. In this way we keep the original unaltered copy,
         * which we can use at the end to restore terminal configuration.
         */
        CLibrary.termios raw = orig_termios.clone();

        /* As documented in https://man7.org/linux/man-pages/man3/termios.3.html cfmakeraw is equivalent to:
         * raw->c_iflag &= ~(IGNBRK | BRKINT | PARMRK | ISTRIP
         *     | INLCR | IGNCR | ICRNL | IXON);
         * raw->c_oflag &= ~OPOST;
         * raw->c_lflag &= ~(ECHO | ECHONL | ICANON | ISIG | IEXTEN);
         * raw->c_cflag &= ~(CSIZE | PARENB);
         * raw->c_cflag |= CS8;
         * which sets the termios struct flags to something like terminal "raw" mode.
         */
        CLibrary.INSTANCE.cfmakeraw(raw);

        /* Calling tcsetattr as in
         * https://viewsourcecode.org/snaptoken/kilo/02.enteringRawMode.html#disable-raw-mode-at-exit
         * (we refer to the call inside enableRawMode).
         * As documented in https://man7.org/linux/man-pages/man3/termios.3.html it sets the current terminal
         * configuration to the one stored in raw.
         */
        try {
            CLibrary.INSTANCE.tcsetattr(CLibrary.STDIN_FILENO, CLibrary.TCSAFLUSH, raw);
        } catch (LastErrorException e) {
            throw new TerminalException("Got error code " + e.getErrorCode() +
                    " while trying to set terminal configuration to raw");
        }
    }

    @Override
    protected void disableRawMode() throws TerminalException {
        // Checks if the terminal has been ever set to raw mode. orig_termios is null if that has not happened.
        if (orig_termios != null) {
            /* Calling tcsetattr as in
             * https://viewsourcecode.org/snaptoken/kilo/02.enteringRawMode.html#disable-raw-mode-at-exit
             * (we refer to the call inside disableRawMode).
             * As documented in https://man7.org/linux/man-pages/man3/termios.3.html it sets the current terminal
             * configuration to the one stored in orig_termios, i.e. it restores terminal configuration
             * to the one in place before enabling raw mode.
             */
            try {
                CLibrary.INSTANCE.tcsetattr(CLibrary.STDIN_FILENO, CLibrary.TCSAFLUSH, orig_termios);
            } catch (LastErrorException e) {
                throw new TerminalException("Got error code " + e.getErrorCode() +
                        " while trying to restore original terminal configuration");
            }
        }
    }

    @Override
    protected TerminalSize getSize() throws TerminalException {
        CLibrary.winsize ws = new CLibrary.winsize();

        if (Platform.isMac()) {
            int lines;
            int columns;
            try {
                Scanner scanner = new Scanner(new InputStreamReader(Runtime.getRuntime().exec(new String[] {
                        "bash", "-c", "tput lines 2> /dev/tty" }
                ).getInputStream()));
                lines = scanner.nextInt();
                scanner.close();

                scanner = new Scanner(new InputStreamReader(Runtime.getRuntime().exec(new String[] {
                        "bash", "-c", "tput cols 2> /dev/tty" }
                ).getInputStream()));
                columns = scanner.nextInt();
                scanner.close();

                return new TerminalSize(lines, columns);
            } catch (Exception e) {
                throw new TerminalException("Got exception while reading TerminalSize: " + e.getMessage());
            }
        }

        /* Calling ioctl as in
         * https://viewsourcecode.org/snaptoken/kilo/03.rawInputAndOutput.html#window-size-the-easy-way.
         * As documented in https://man7.org/linux/man-pages/man2/ioctl_tty.2.html the flag TIOCGWINSZ in
         * a ioctl call specifies that we want to retrieve terminal size.
         */
        try {
            CLibrary.INSTANCE.ioctl(CLibrary.STDOUT_FILENO, CLibrary.TIOCGWINSZ, ws);
        } catch (LastErrorException e) {
            throw new TerminalException("Got error code " + e.getMessage() +
                " while trying to retrieve terminal size");
        }

        try {
            return new TerminalSize((int)ws.ws_row, (int)ws.ws_col);
        } catch (IllegalArgumentException e) {
            throw new TerminalException("Got exception while constructing TerminalSize object: " + e.getMessage());
        }
    }

    /**
     * Name of the C standard library in Unix systems.
     */
    private static final String LIBC_NAME = "c";

    /**
     *  Loading libc as illustrated in https://github.com/java-native-access/jna/blob/master/www/GettingStarted.md.
     * Note that we are using "libc" as the library name instead of just "c" as in the example,
     * on Unix systems they are equivalent.
     */
    public interface CLibrary extends Library {
        /**
         * Instance of libc which allows to invoke system calls through JNA.
         */
        CLibrary INSTANCE = (CLibrary) Native.load(LIBC_NAME, CLibrary.class);

        /**
         * We retreived the value of TCSAFLUSH in libc using: grep -R TCSAFLUSH /usr/include.
         * We got:
         * ...
         * /usr/include/x86_64-linux-gnu/bits/termios-tcflow.h:#define     TCSAFLUSH       2
         * ...
         */
        int TCSAFLUSH = 2;

        /**
         * We retreived the value of STDIN_FILENO in libc using: grep -R STDIN_FILENO /usr/include.
         * We got:
         * ...
         * /usr/include/unistd.h:#define   STDIN_FILENO    0       /* Standard input.  *//*
         * ...
         */
        int STDIN_FILENO = 0;

        /**
         * We retreived the value of STDOUT_FILENO in libc using: grep -R STDOUT_FILENO /usr/include.
         * We got:
         * ...
         * /usr/include/unistd.h:#define   STDOUT_FILENO   1       /* Standard output.  *//*
         * ...
         */
        int STDOUT_FILENO = 1;

        /**
         * We retreived the value of NCCS in libc using: grep -R NCCS /usr/include.
         * We got:
         * ...
         * /usr/include/x86_64-linux-gnu/bits/termios-struct.h:#define NCCS 32
         * ...
         */
        int NCCS = Platform.isMac() ? 20 : 32;

        /**
         * We retreived the value of TIOCGWINSZ in libc using: grep -R TIOCGWINSZ /usr/include.
         * We got:
         * ...
         * /usr/include/asm-generic/ioctls.h:#define TIOCGWINSZ    0x5413
         * ...
         */
        int TIOCGWINSZ = Platform.isMac() ? 0x40087468 : 0x5413;

        /**
         * Represents a termios struct which can be cloned.
         */
        interface termios {
            /**
             * @return a copy of this termios struct.
             */
            termios clone();
        }

        /**
         * @return a termios struct according if the program is running on Mac OS or Linux.
         */
        static termios craft_termios() {
            return Platform.isMac() ? new macos_termios() : new linux_termios();
        }

        /**
         * Definining termios struct as explained in
         * https://github.com/java-native-access/jna/blob/master/www/StructuresAndUnions.md.
         * The original termios struct definition in https://man7.org/linux/man-pages/man3/termios.3.html contains:
         * tcflag_t c_iflag;      /* input modes *//*
         * tcflag_t c_oflag;      /* output modes *//*
         * tcflag_t c_cflag;      /* control modes *//*
         * tcflag_t c_lflag;      /* local modes *//*
         * cc_t     c_cc[NCCS];   /* special characters *//*
         *
         * Adding more fields found with grep -R "struct termios" /usr/include.
         * In particular the additional fields are:
         * cc_t c_line; /* line discipline *//*
         * speed_t c_ispeed; /* input speed *//*
         * speed_t c_ospeed; /* output speed *//*
         *
         * We retrieved the primitive type aliased by tcflag_t using: grep -R tcflag_t /usr/include.
         * We got:
         * ...
         * /usr/include/x86_64-linux-gnu/bits/termios.h:typedef unsigned int       tcflag_t;
         * ...
         *
         * We retrieved the primitive type aliased by cc_t using: grep -R cc_t /usr/include.
         * We got:
         * ...
         * /usr/include/x86_64-linux-gnu/bits/termios.h:typedef unsigned char      cc_t;
         * ...
         *
         * We retrieved the primitive type aliased by speed_t using: grep -R speed_t /usr/include.
         * We got:
         * ...
         * /usr/include/asm-generic/termbits-common.h:typedef unsigned int speed_t;
         * ...
         *
         * By the mappings to primitive types explained in
         * https://github.com/java-native-access/jna/blob/master/www/Mappings.md:
         * tcflag_t = unsigned int -> int
         * cc_t = unsigned char -> byte
         * speed_t = unsigned int -> int
         */
        @FieldOrder({ "c_iflag", "c_oflag", "c_cflag", "c_lflag", "c_line", "c_cc",
                "c_ispeed", "c_ospeed" })
        class linux_termios extends Structure implements termios {
            /**
             * Input modes.
             */
            public int c_iflag;

            /**
             * Output modes.
             */
            public int c_oflag;

            /**
             * Control modes.
             */
            public int c_cflag;

            /**
             * Local modes.
             */
            public int c_lflag;

            /**
             * Special characters.
             */
            public byte c_line;

            /**
             * Special characters.
             */
            public byte[] c_cc;

            /**
             * Input speed.
             */
            public int c_ispeed;

            /**
             * Output speed.
             */
            public int c_ospeed;

            /**
             * Constructor of the class.
             */
            public linux_termios() {
                c_cc = new byte[NCCS];
            }

            @Override
            public termios clone() {
                linux_termios clone = new linux_termios();

                clone.c_iflag = c_iflag;
                clone.c_oflag = c_oflag;
                clone.c_cflag = c_cflag;
                clone.c_lflag = c_lflag;
                clone.c_line = c_line;
                for (int i = 0; i < NCCS; i++) {
                    clone.c_cc[i] = c_cc[i];
                }
                clone.c_ispeed = c_ispeed;
                clone.c_ospeed = c_ospeed;

                return clone;
            }
        }

        /**
         * Defining termios struct according to Max OS documentation. It is analogous to the Linux one but has slightly
         * different field sizes.
         */
        @FieldOrder({ "c_iflag", "c_oflag", "c_cflag", "c_lflag", "c_cc",
            "c_ispeed", "c_ospeed" })
        class macos_termios extends Structure implements termios {
            /**
             * Input flags.
             */
            public long c_iflag;

            /**
             * Output flags.
             */
            public long c_oflag;

            /**
             * Control flags.
             */
            public long c_cflag;

            /**
             * Local flags.
             */
            public long c_lflag;

            /**
             * Control chars.
             */
            public byte[] c_cc;

            /**
             * Input speed.
             */
            public long c_ispeed;

            /**
             * Output speed.
             */
            public long c_ospeed;

            /**
             * Constructor of the class.
             */
            public macos_termios() {
                c_cc = new byte[NCCS];
            }

            @Override
            public termios clone() {
                macos_termios clone = new macos_termios();

                clone.c_iflag = c_iflag;
                clone.c_oflag = c_oflag;
                clone.c_cflag = c_cflag;
                clone.c_lflag = c_lflag;
                for (int i = 0; i < NCCS; i++) {
                    clone.c_cc[i] = c_cc[i];
                }
                clone.c_ispeed = c_ispeed;
                clone.c_ospeed = c_ospeed;

                return clone;
            }
        }

        /**
         * Defining cfmakeraw signature as in https://man7.org/linux/man-pages/man3/termios.3.html.
         *
         * @param termios_p is a pointer to a termios structure.
         */
        void cfmakeraw(termios termios_p);

        /**
         * Defining tcgetattr signature as in https://man7.org/linux/man-pages/man3/termios.3.html.
         *
         * @param fd is a file descriptor to the terminal.
         * @param termios_p is a pointer to a termios structure.
         *
         * @return 0 iff there hasn't been any error.
         *
         * @throws LastErrorException if an unexpected error occurs.
         */
        int tcgetattr(int fd, termios termios_p) throws LastErrorException;

        /**
         * Defining tcsetattr signature as in https://man7.org/linux/man-pages/man3/termios.3.html.
         *
         * @param fd is a file descriptor to the terminal.
         * @param optional_actions allows to specify optional actions.
         * @param termios_p is a pointer to a termios structure.
         *
         * @return 0 iff there hasn't been any error.
         *
         * @throws LastErrorException if an unexpected error occurs.
         */
        int tcsetattr(int fd, int optional_actions, termios termios_p) throws LastErrorException;

        /**
         * Definining winsize struct as explained in
         * https://github.com/java-native-access/jna/blob/master/www/StructuresAndUnions.md.
         * The original winsize struct definition in https://man7.org/linux/man-pages/man2/ioctl_tty.2.html is:
         * struct winsize {
         *     unsigned short ws_row;
         *     unsigned short ws_col;
         *     unsigned short ws_xpixel;   /* unused *//*
         *     unsigned short ws_ypixel;   /* unused *//*
         * };
         *
         * By the mappings to primitive types explained in
         * https://github.com/java-native-access/jna/blob/master/www/Mappings.md:
         * unsigned short -> short
         */
        @FieldOrder({ "ws_row", "ws_col", "ws_xpixel", "ws_ypixel" })
        class winsize extends Structure {
            /**
             * It is the number of rows of the terminal window.
             */
            public short ws_row;

            /**
             * It is the number of columns of the terminal window.
             */
            public short ws_col;

            /**
             * It is the number of pixel of the terminal window, in the x direction.
             */
            public short ws_xpixel;

            /**
             * It is the number of pixel of the terminal window, in the y direction.
             */
            public short ws_ypixel;

            /**
             * Constructor of the class.
             */
            public winsize () {
                ws_row = 0;
                ws_col = 0;
                ws_xpixel = 0;
                ws_ypixel = 0;
            }
        }

        /**
         * Defining ioctl signature as in https://man7.org/linux/man-pages/man2/ioctl_tty.2.html.
         * The original signature has variadic arguments, we instead only declare a winsize parameter since
         * it's the only that we are going to use.
         *
         * @param fd is a file descriptor.
         * @param cmd is the command which specifies what the system call should do.
         * @param ws allows to retrieve the size of the terminal.
         *
         * @return 0 iff there hasn't been any error.
         *
         * @throws LastErrorException if an unexpected error occurs.
         */
        int ioctl(int fd, int cmd, winsize ws) throws LastErrorException;
    }

    @Override
    public int read() throws IOException {
        int readByte = System.in.read();

        if (readByte == 127) {
            // We always use backspace instead of delete
            return 8;
        }

        return readByte;
    }
}
