package coco;

import javafx.application.Application;

/**
 * Launches the JavaFX GUI via a class that does not itself extend
 * Application. Launching Main (an Application subclass) directly as the
 * jar's entry point fails with a "JavaFX runtime components are missing"
 * error once the app has no module-info.java of its own; going through
 * this separate class avoids that.
 */
public class Launcher {
    /**
     * Starts the GUI.
     *
     * @param args Passed through to Application.launch.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
