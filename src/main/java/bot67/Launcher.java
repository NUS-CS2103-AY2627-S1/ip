package bot67;

import javafx.application.Application;

/**
 * Launches JavaFX separately to avoid classpath problems with executable JARs.
 */
public final class Launcher {
    /** Prevents construction of this utility class. */
    private Launcher() {
        // Utility class; do not instantiate.
    }

    /** Starts the Bot67 JavaFX application. */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
