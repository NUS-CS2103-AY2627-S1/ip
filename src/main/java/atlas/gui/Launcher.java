package atlas.gui;

import javafx.application.Application;

/**
 * Launcher for the Atlas GUI.
 * A separate entry class is required because JavaFX needs an application
 * class that is not the one referenced by the packaged JAR's main class.
 */
public class Launcher {

    /**
     * Starts the Atlas GUI.
     *
     * @param args command-line arguments, which Atlas does not currently use.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
