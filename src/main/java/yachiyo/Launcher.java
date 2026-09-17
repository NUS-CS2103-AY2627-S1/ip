package yachiyo;

import javafx.application.Application;

/**
 * Launches the JavaFX application without relying on a JavaFX application class as the entry point.
 */
public class Launcher {
    /**
     * Starts the JavaFX application.
     *
     * @param args command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
