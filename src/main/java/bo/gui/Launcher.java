package bo.gui;

import javafx.application.Application;

/**
 * Launches the JavaFX application from a plain Java entry point.
 */
public final class Launcher {
    private Launcher() {
    }

    /**
     * Starts Bo's JavaFX application.
     *
     * @param args command-line arguments passed to JavaFX
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
