package jassabot;

import javafx.application.Application;

/**
 * A launcher class to workaround classpath issues.
 */
public class Launcher {
    /**
     * Starts the JavaFX interface. Run JassaBot.main for the console interface.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
