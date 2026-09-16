package alice;

import javafx.application.Application;

/**
 * Launches the JavaFX application.
 */
public final class Launcher {
    private Launcher() {
        // Prevent instantiation.
    }

    /**
     * Starts the Alice JavaFX application.
     *
     * @param args Command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
