package magnus;

import javafx.application.Application;

/**
 * Provides a non-JavaFX entry point for launching the application.
 */
public class Launcher {
    private Launcher() {
    }

    /**
     * Starts the JavaFX application.
     *
     * @param args Command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
