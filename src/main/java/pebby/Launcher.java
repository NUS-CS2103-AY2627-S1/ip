package pebby;

import javafx.application.Application;

/**
 * Launches Pebby's JavaFX application without JavaFX classpath issues.
 */
public class Launcher {

    /**
     * Launches the JavaFX application.
     *
     * @param args command-line arguments passed to the application
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
