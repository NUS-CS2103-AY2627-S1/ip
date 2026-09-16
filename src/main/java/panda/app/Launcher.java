package panda.app;

import javafx.application.Application;

/**
 * Launches the JavaFX application without JavaFX classpath issues.
 */
public class Launcher {
    /**
     * Starts the JavaFX application.
     *
     * @param args command-line arguments passed to JavaFX
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
