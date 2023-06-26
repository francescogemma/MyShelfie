package it.polimi.ingsw.utils;

import com.sun.jna.Platform;

import java.io.*;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * The Logger class provides a flexible logging mechanism for recording events that occur during program execution.
 * It allows messages to be logged at different levels of severity, such as MESSAGE, WARNING, and CRITICAL.
 *
 * In addition to recording messages, the Logger class can also include information about the calling function in the log message.
 * This feature can be useful for debugging and troubleshooting purposes.
 *
 * @author Giacomo Groppi
 */
public class Logger {
    /**
     * Enum represents different severity types of messages.
     */
    public enum Type {
        /**
         * Represents a critical message.
         */
        CRITICAL("Critical"),

        /**
         * Represents a warning message.
         */
        WARNING("Warning"),

        /**
         * Represents a general message.
         */
        MESSAGE("Message");

        /**
         * The severity level of the message
         */
        private final String data;

        /**
         * Private constructor for the enum.
         * @param type Type of severity
         */
        private Type(String type) {
            this.data = type;
        }

        @Override
        public String toString() {
            return this.data;
        }
    }

    /**
     * file writer.
     * null if writeFile is false
     */
    private PrintWriter writer;

    /**
     * all the incoming log messages
     */
    private final List<String> message;

    /**
     * true if you want to print on-screen all log message
     */
    private boolean shouldPrint = true;

    /**
     * true if you want to write log output to a file
     */
    private boolean writeFile = true;

    /**
     * true if you want to append the caller name to the log message
     */
    private boolean printCaller = true;

    /**
     * name of the log file.
     */
    private final String nameLog =
            (
                    LocalDate.now().toString() +
                    LocalTime.now().toString()
            ).replace("-", "_")
            .replace("\\.", "_")
            .replace(":", "_");

    /**
     * folder for log output
     */
    private static String logPosition;

    /**
     * @return The position of the log file.
     */
    private String getLogPosition () {
        return logPosition + "/log_" + nameLog + ".txt";
    }

    /**
     * Private constructor for the logger for implementing the singleton pattern.
     */
    private Logger() {
        message = new ArrayList<>();
    }

    /**
     * @param printCaller Set it to true if you want to add caller function to log message.
     */
    public static void setAddCaller (boolean printCaller) {
        synchronized (INSTANCE) {
            INSTANCE.printCaller = printCaller;
        }
    }

    /**
     * @param write Set it to true iff you want the logger to save log messages to a file.
     */
    public static void setEnableWriteToFile (boolean write) {
        synchronized (INSTANCE) {
            INSTANCE.writeFile = write;

            if (!write) {
                INSTANCE.removeFile(INSTANCE.getLogPosition());
            }
        }
    }

    /**
     * @param position The position of the file to be removed.
     * @return True iff the file has been successfully removed
     */
    private boolean removeFile (String position) {
        return new File(position).delete();
    }

    /**
     * Instance of the logger for implementing the singleton pattern.
     */
    private static final Logger INSTANCE;

    /**
     * @param value set this value to true if you want to print on-screen log message
     * */
    public static void setShouldPrint(boolean value) {
        synchronized (INSTANCE) {
            INSTANCE.shouldPrint = value;
        }
    }

    /**
     * @param newPosition New position of output log.
     * @throws FileNotFoundException if this file already exists
     * */
    public void changePositionLog(String newPosition) throws FileNotFoundException {
        if (newPosition == null)
            throw new NullPointerException();

        if (newPosition.isEmpty())
            throw new IllegalArgumentException("new position is empty");

        if (newPosition.charAt(newPosition.length() - 1) == '/')
            throw new IllegalArgumentException("You should not add / at end of the string");

        synchronized (INSTANCE) {
            writer.close();
            logPosition = newPosition;
            writer = new PrintWriter(logPosition + nameLog);
        }
    }

    static {
        final String nameFolder = (Platform.isWindows() ? "\\" : "/") + "log_MyShelfie";
        try {
            new File(System.getProperty("user.home") + nameFolder).mkdirs();
            logPosition = System.getProperty("user.home") + nameFolder;
            INSTANCE = new Logger();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Write a log with Type::Warning
     * @param message Log message.
     * */
    public static void writeWarning (String message) {
        Logger.write(Type.WARNING, message, INSTANCE.printCaller);
    }

    /**
     * Write a log with Type::Message
     * @param message Log message.
     * */
    public static void writeMessage (String message) {
        Logger.write(Type.MESSAGE, message, INSTANCE.printCaller);
    }

    /**
     * Write a log with Type::Critical
     * @param message Log message.
     * */
    public static void writeCritical(String message) {
        Logger.write(Type.CRITICAL, message, true);
    }

    /**
     * Prints the log message based on the logger configuration.
     * The function is expected to be called from a function that is between
     * {@link Logger#write(Type, String, boolean)} and the caller who wants to print a text string,
     * because the function to print the name of the calling function observes the {@link Thread#getStackTrace()}
     * and needs to know how many functions are between it and the caller.
     *
     * @param type Message type.
     * @param message Message to print.
     * @param printCaller If true, prints the name of the calling function.
     */
    private static void write (Type type, String message, boolean printCaller) {
        String appendMessage;

        StackTraceElement[] res = Thread.currentThread().getStackTrace();
        final String m =
                res[3]
                        .toString()
                        .substring(
                                0,
                                res[3].toString().indexOf("(")
                        );

        appendMessage = (printCaller ? m : "")
                + type
                + " "
                + LocalDate.now().toString()
                + LocalTime.now().toString()
                + " "
                + message;

        appendMessage += "\n";

        synchronized (INSTANCE) {
            INSTANCE.message.add(appendMessage);

            if (INSTANCE.writeFile) {
                if (INSTANCE.writer == null) {
                    try {
                        FileWriter fileWriter = new FileWriter(INSTANCE.getLogPosition(), false);
                        INSTANCE.writer = new PrintWriter(fileWriter);
                    } catch (IOException e) {

                    }
                }
                INSTANCE.writer.print(appendMessage);
                INSTANCE.writer.flush();
            }

            if (INSTANCE.shouldPrint) {
                System.out.print(appendMessage);
            }
        }
    }

    /**
     * This method prints on the screen all the logs recorded so far.
     */
    public static void printAllMessage () {
        synchronized (INSTANCE) {
            INSTANCE.message.forEach(System.out::print);
        }
    }
}