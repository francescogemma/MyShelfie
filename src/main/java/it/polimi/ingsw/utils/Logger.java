package it.polimi.ingsw.utils;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Logger {
    public enum Type {
        CRITICAL("Critical"),
        WARNING("Warning"),
        MESSAGE("Message");

        private final String data;

        Type(String m) {
            this.data = m;
        }

        @Override
        public String toString() {
            return this.data;
        }
    }

    private PrintWriter writer;
    private final List<String> message;
    private boolean shouldPrint = true;
    private final String nameLog =
            (
                    LocalDate.now().toString() +
                    LocalTime.now().toString()
            ).replace("-", "_")
            .replace("\\.", "_")
            .replace(":", "_");

    private static String logPosition = "log/";

    private Logger() throws IOException {
        String pos = logPosition + "/log_" + nameLog + ".txt";
        FileWriter fileWriter = new FileWriter(pos, false);
        writer = new PrintWriter(fileWriter);
        message = new ArrayList<>();
    }

    private static final Logger INSTANCE;

    public static void setShouldPrint(boolean value) {
        synchronized (INSTANCE) {
            INSTANCE.shouldPrint = value;
        }
    }

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
        try {
            logPosition = System.getProperty("user.dir");
            INSTANCE = new Logger();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeWarning (String message) {
        Logger.write(Type.WARNING, message);
    }

    public static void writeMessage (String message) {
        Logger.write(Type.MESSAGE, message);
    }

    public static void write (Type type, String message) {
        String appendMessage;
        appendMessage = type + " " + LocalDate.now().toString() + LocalTime.now().toString() + " " +message;
        appendMessage += "\n";

        synchronized (INSTANCE) {
            INSTANCE.message.add(appendMessage);
            INSTANCE.writer.print(appendMessage);
            INSTANCE.writer.flush();
            if (INSTANCE.shouldPrint) {
                System.out.print(appendMessage);
            }
        }
    }

    public static void printAllMessage () {
        synchronized (INSTANCE) {
            INSTANCE.message.forEach(System.out::print);
        }
    }
}