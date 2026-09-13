package alfred;

import javafx.application.Application;

/**
 * Launches the JavaFX application without Java's JavaFX classpath issue.
 */
public class Launcher {

    /**
     * Starts Alfred's JavaFX application.
     *
     * @param args command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
