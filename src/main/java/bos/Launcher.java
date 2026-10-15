package bos;

import javafx.application.Application;

/**
 * Launches the application while working around JavaFX classpath issues.
 */
public class Launcher {
    /**
     * Launches the JavaFX application.
     *
     * @param args command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
