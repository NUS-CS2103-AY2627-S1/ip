package bob.gui;

import javafx.application.Application;

/**
 * Launches the JavaFX version of Bob.
 */
public final class Launcher {
    private Launcher() {
    }

    /**
     * Starts the JavaFX runtime.
     *
     * @param args command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
