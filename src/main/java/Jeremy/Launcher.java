package jeremy;

import javafx.application.Application;

/**
 * Provides a non-Application entry point for running the JavaFX fat JAR.
 */
public final class Launcher {

    private Launcher() {
        // Utility class; do not instantiate.
    }

    /** Starts the Jeremy JavaFX application. */
    public static void main(String[] args) {
        Application.launch(Jeremy.class, args);
    }
}
