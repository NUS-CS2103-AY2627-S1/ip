package floppy;

import javafx.application.Application;

/**
 * Launches the JavaFX application without extending {@link Application} itself.
 */
public class Launcher {
    /**
     * Starts the Floppy GUI.
     *
     * @param args Command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
