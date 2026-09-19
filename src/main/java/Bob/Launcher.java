package bob;

import javafx.application.Application;

/**
 * A launcher class to workaround classpath issues.
 */
public class Launcher {
    /**
     * Launches the JavaFX application.
     *
     * @param args Application arguments.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
