package remy.gui;

import javafx.application.Application;

/**
 * Launches Remy without extending JavaFX's {@code Application}, avoiding classpath issues.
 */
public class Launcher {
    /** Starts the JavaFX application. */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
