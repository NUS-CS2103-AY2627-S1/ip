package mochi;

import javafx.application.Application;

/**
 * Launches the JavaFX application without extending {@code Application} directly.
 */
public class Launcher {
    /**
     * Starts Mochi's graphical interface.
     *
     * @param args command-line arguments passed to JavaFX
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
