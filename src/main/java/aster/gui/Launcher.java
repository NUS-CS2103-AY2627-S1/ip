package aster.gui;

import javafx.application.Application;

/**
 * Launches the JavaFX application through a small classpath-safe entry point.
 */
public final class Launcher {
    private Launcher() {
        // Prevent instantiation.
    }

    /**
     * Starts the JavaFX application.
     *
     * @param args command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
