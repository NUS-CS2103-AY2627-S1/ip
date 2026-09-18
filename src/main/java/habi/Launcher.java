package habi;

import javafx.application.Application;

/** Starts HABI through a non-JavaFX launcher to avoid classpath issues. */
public class Launcher {
    /**
     * Launches the JavaFX application.
     *
     * @param args Command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
