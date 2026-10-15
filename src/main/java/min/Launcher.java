package min;

import javafx.application.Application;

/**
 * Launches Min's JavaFX application without JavaFX classpath conflicts.
 */
public class Launcher {
    /**
     * Starts the JavaFX application.
     *
     * @param args Command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
