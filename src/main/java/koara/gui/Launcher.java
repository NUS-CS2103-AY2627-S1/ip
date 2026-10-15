package koara.gui;

import javafx.application.Application;

/**
 * Launches Koara's JavaFX application from a non-JavaFX entry point.
 */
public class Launcher {

    /**
     * Starts the JavaFX application.
     *
     * @param args Command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
