package cookie;

import javafx.application.Application;

/** Launches Cookie through a non-JavaFX entry point to avoid classpath issues. */
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
