package nori;

import javafx.application.Application;

/**
 * Launches Nori's JavaFX application without extending {@link Application}.
 */
public final class Launcher {
    private Launcher() {
    }

    /**
     * Starts the JavaFX runtime.
     *
     * @param args Command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
