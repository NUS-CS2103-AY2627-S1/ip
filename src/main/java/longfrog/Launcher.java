package longfrog;

import javafx.application.Application;

/**
 * Starts the JavaFX application through a separate entry point to work around classpath issues.
 */
public final class Launcher {
    private Launcher() {
    }

    /**
     * Launches the JavaFX application.
     *
     * @param args command-line arguments passed to the JavaFX application
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
