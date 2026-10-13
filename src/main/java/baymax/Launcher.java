package baymax;

import javafx.application.Application;

/**
 * Launches the JavaFX application through a non-Application entry point.
 */
public final class Launcher {
    private Launcher() {
        // Utility class.
    }

    /**
     * Starts the JavaFX application.
     *
     * @param args command-line arguments passed to JavaFX
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
