package ekud;

import javafx.application.Application;

/**
 * A launcher class to workaround JavaFX classpath issues.
 */
public class Launcher {
    /**
     * Starts the JavaFX GUI.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
