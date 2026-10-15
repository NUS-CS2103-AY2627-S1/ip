package samantha;

import javafx.application.Application;

/**
 * Launches Samantha while working around the JavaFX classpath issue.
 */
public class Launcher {

    /**
     * Launches the JavaFX application.
     *
     * @param args command-line arguments passed to JavaFX
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
