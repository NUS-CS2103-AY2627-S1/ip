package kafka.javafx;

import javafx.application.Application;

/**
 * Launches JavaFX through a separate entry point to support running from the classpath.
 */
public class Launcher {
    /**
     * Starts the JavaFX application with the supplied command-line arguments.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}

