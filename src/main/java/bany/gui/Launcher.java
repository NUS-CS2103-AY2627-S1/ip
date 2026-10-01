package bany.gui;

import javafx.application.Application;

/** Launches Bany's JavaFX application without JavaFX classpath ambiguity. */
public class Launcher {
    /** Creates the JavaFX launcher. */
    public Launcher() {
    }

    /**
     * Starts the JavaFX application.
     *
     * @param args command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
