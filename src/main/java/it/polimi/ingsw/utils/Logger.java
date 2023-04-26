package it.polimi.ingsw.utils;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class Logger {
    enum Type {
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
    private List<String> message;
    private boolean shouldPrint = false;
    private final String nameLog =
            (
                    LocalDate.now().toString() +
                    LocalTime.now().toString()
            ).replace("-", "_")
            .replace("\\.", "_")
            .replace(":", "_");

    private String logPosition = "log/";

    private Logger() throws FileNotFoundException, UnsupportedEncodingException {
        writer = new PrintWriter(logPosition + nameLog, "UTF-8");
    }

    private static final Logger INSTANCE;

    public static void setShouldPrint(boolean value) {
        synchronized (INSTANCE) {
            INSTANCE.shouldPrint = value;
        }
    }

    public void changePositionLog(String newPosition) throws FileNotFoundException {
        synchronized (INSTANCE) {
            writer.close();
            logPosition = newPosition;
            writer = new PrintWriter(logPosition + nameLog);
        }
    }

    static {
        try {
            INSTANCE = new Logger();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static void write (Type type, String message) {
        String appendMessage;
        appendMessage = "\n";
        appendMessage += type + LocalDate.now().toString() + LocalTime.now().toString() + message;

        synchronized (INSTANCE) {
            INSTANCE.message.add(appendMessage);
            INSTANCE.writer.print(appendMessage);

            if (INSTANCE.shouldPrint) {
                System.out.print(appendMessage);
            }
        }
    }


}