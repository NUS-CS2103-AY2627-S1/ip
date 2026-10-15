package trackie.gui;

import javafx.application.Application;

/** Launches Trackie's JavaFX application without classpath issues. */
public class Launcher {
    /**
     * Starts the JavaFX runtime.
     *
     * @param args command-line arguments passed to JavaFX
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
