package kibo.gui;

import javafx.application.Application;

/**
 * Launches Kibo's JavaFX application without extending {@link Application}.
 */
public final class Launcher {
    private Launcher() {
    }

    /**
     * Starts the JavaFX runtime and opens Kibo's main window.
     *
     * @param args command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
