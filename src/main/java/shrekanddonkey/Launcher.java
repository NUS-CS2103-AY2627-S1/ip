package shrekanddonkey;

import javafx.application.Application;

/**
 * Launches the JavaFX application to prevent classpath and module issues.
 */
public class Launcher {
    /**
     * Starts the JavaFX application.
     *
     * @param args command-line arguments.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
