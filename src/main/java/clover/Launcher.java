package clover;

import javafx.application.Application;

/**
 * A launcher class to workaround classpath issues.
 */
public class Launcher {

    /**
     * Launches the JavaFX application from a non-Application entry point.
     *
     * @param args command-line arguments supplied when the application starts
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
