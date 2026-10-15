package bern;

import javafx.application.Application;

/**
 * Launches Bern through a separate entry point for JavaFX startup.
 */
public class Launcher {
    /**
     * Creates a launcher instance; application startup uses the static {@link #main(String[])} method.
     */
    public Launcher() {
    }

    /**
     * Starts the Bern JavaFX application.
     *
     * @param args The command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Bern.class, args);
    }
}
