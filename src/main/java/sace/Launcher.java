package sace;

import javafx.application.Application;

/**
 * Starts the JavaFX application without inheriting from {@link Application}.
 */
public class Launcher {
    /**
     * Launches Sace's graphical user interface.
     *
     * @param args command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
