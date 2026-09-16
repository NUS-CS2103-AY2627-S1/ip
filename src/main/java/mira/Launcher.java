package mira;

import javafx.application.Application;

/**
 * Launches Mira's JavaFX application without extending {@link Application}.
 */
public class Launcher {
    /**
     * Starts the graphical Mira application.
     *
     * @param args Command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
