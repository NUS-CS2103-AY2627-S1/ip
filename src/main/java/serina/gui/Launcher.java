package serina.gui;

import javafx.application.Application;

/**
 * Launches Serina's JavaFX application without JavaFX classpath conflicts.
 */
public class Launcher {
    /**
     * Prevents instantiation of this application entry-point class.
     */
    private Launcher() {
    }

    /**
     * Starts Serina's graphical user interface.
     *
     * @param args Command line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
