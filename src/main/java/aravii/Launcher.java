package aravii;

import javafx.application.Application;

/**
 * Provides the entry point for the JavaFX application.
 */
public final class Launcher {
    private Launcher() {
    }

    /**
     * Starts the Arav II JavaFX application.
     *
     * @param args Command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(AravIIApplication.class, args);
    }
}
