package echo.gui;

import javafx.application.Application;

/**
 * Launches Echo's GUI via a separate class from {@link Main}, working
 * around a JavaFX classpath issue that occurs when the Application
 * subclass is also the entry point.
 */
public class Launcher {
    /**
     * Starts the GUI.
     *
     * @param args Command-line arguments (unused).
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
