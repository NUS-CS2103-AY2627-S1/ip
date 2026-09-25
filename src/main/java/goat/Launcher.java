package goat;

import javafx.application.Application;

/**
 * Launches the JavaFX application from a class that does not extend {@link Application}.
 */
public final class Launcher {
    private Launcher() {
    }

    /**
     * Starts the Goat GUI.
     *
     * @param args command-line arguments passed to JavaFX
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
