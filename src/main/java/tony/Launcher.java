package tony;

import javafx.application.Application;

/**
 * Launches the JavaFX application without classpath conflicts.
 */
public class Launcher {

    /**
     * Starts the Tony application.
     *
     * @param args command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
