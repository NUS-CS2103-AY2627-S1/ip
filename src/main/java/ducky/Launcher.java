package ducky;

import javafx.application.Application;

/**
 * Launches the JavaFX GUI application.
 * A separate entry point (rather than launching {@link Main} directly) avoids a JavaFX
 * classpath issue that can occur when the application class itself is the main class.
 */
public class Launcher {
    /**
     * Starts the GUI application.
     *
     * @param args command-line arguments, which are currently unused.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
