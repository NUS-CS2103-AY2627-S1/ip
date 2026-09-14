package johnny;

import javafx.application.Application;

/**
 * Starts JavaFX through a separate entry point to avoid classpath issues.
 */
public class Launcher {

    /**
     * Launches the Johnny GUI.
     *
     * @param args command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
