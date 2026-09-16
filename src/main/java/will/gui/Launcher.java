package will.gui;

import javafx.application.Application;

/**
 * A launcher class to work around a JavaFX classpath issue: starting
 * {@link MainApp} (an {@code Application} subclass) directly as the
 * jar's main class can fail with a "module not found" error on some
 * setups, but starting it indirectly through a plain class like this
 * one does not.
 */
public class Launcher {
    /**
     * Starts the GUI.
     *
     * @param args Command-line arguments (unused; JavaFX apps don't
     *             need any).
     */
    public static void main(String[] args) {
        Application.launch(MainApp.class, args);
    }
}
