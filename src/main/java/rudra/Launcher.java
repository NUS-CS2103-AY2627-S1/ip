package rudra;

import javafx.application.Application;

/**
 * Launches Rudra's JavaFX application without triggering JavaFX classpath handling issues.
 */
public class Launcher {
    /**
     * Starts the JavaFX application.
     *
     * @param args Command-line arguments passed to the application.
     */
    public static void main(String[] args) {
        Application.launch(RudraGui.class, args);
    }
}
