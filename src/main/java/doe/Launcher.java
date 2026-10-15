package doe;

import javafx.application.Application;

/**
 * Starts the JavaFX application without making the application class the JAR entry point.
 */
public class Launcher {
    /**
     * Launches the JavaFX runtime.
     *
     * @param args Command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
