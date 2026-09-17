package glennon.gui;

import javafx.application.Application;

/**
 * Launches JavaFX through a separate entry point to avoid classpath detection issues.
 */
public class Launcher {
    /**
     * Starts Glennon's graphical interface.
     *
     * @param args application arguments.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
