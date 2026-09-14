package kaya;

import javafx.application.Application;

/**
 * Starts Kaya through a non-JavaFX entry point to avoid classpath issues.
 */
public class Launcher {
    private Launcher() {
        // This utility class should not be instantiated.
    }

    /**
     * Launches Kaya's JavaFX application.
     *
     * @param args command-line arguments passed to JavaFX
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
