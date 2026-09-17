package kia;

import javafx.application.Application;

/**
 * Provides the executable entry point for the JavaFX application.
 */
public class Launcher {
    /**
     * Starts the JavaFX runtime and opens the Hello World window.
     *
     * @param args command-line arguments passed to JavaFX
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
