package org.example;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EventLogger {
    private static final Logger logger = Logger.getLogger("EventLogger");

    public static void logEvent(String message) {
        logger.log(Level.INFO, message);
    }
}
