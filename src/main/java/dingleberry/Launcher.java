package dingleberry;

import javafx.application.Application;

/**
 * A launcher class to workaround classpath issues.
 */
public final class Launcher {
    private Launcher() {
    }

    /**
     * Launches the JavaFX application.
     *
     * @param args command-line arguments, passed through to JavaFX.
     */
    public static void main(final String[] args) {
        Application.launch(Main.class, args);
    }
}
