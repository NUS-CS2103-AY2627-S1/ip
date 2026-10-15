package bingusdingus.gui;

import javafx.application.Application;

/**
 * Launches the graphical application while working around classpath issues.
 */
public class Launcher {
    /**
     * Launches the JavaFX application.
     *
     * @param args command-line arguments passed to the application.
     */
    public static void main(String[] args) {
        Application.launch(BingusDingusGui.class, args);
    }
}
