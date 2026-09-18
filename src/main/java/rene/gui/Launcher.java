package rene.gui;

import javafx.application.Application;

/**
 * Launches Rene's JavaFX application without extending {@link Application}.
 */
public class Launcher {
    /**
     * Starts the JavaFX runtime and opens Rene's main window.
     *
     * @param args command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
