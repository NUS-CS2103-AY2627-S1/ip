package bob.gui;

import javafx.application.Application;

/**
 * Launches Bob's JavaFX application without extending {@code Application}.
 */
public class Launcher {
    /**
     * Starts the JavaFX runtime.
     *
     * @param args Command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
