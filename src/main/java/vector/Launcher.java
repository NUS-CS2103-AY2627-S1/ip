package vector;

import javafx.application.Application;

/**
 * Provides a launcher class to workaround classpath issues.
 */
public class Launcher {

    /**
     * Starts the Launcher and launches the JavaFX application.
     *
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
