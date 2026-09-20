package fifi;

import javafx.application.Application;

/**
 * Starts the JavaFX application through a plain Java entry point.
 */
public class Launcher {

    /**
     * Launches the Fifi JavaFX application.
     *
     * @param args command-line arguments passed to JavaFX
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
