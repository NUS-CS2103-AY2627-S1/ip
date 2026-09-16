package turtley.gui;

import javafx.application.Application;

/**
 * Launches Turtley's JavaFX application.
 */
public class Launcher {

    /**
     * Creates a launcher.
     */
    public Launcher() {
    }

    /**
     * Launches the JavaFX runtime.
     *
     * @param args command-line arguments, which are passed to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
