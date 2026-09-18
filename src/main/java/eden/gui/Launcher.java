package eden.gui;

import javafx.application.Application;

/**
 * Starts Eden through a non-JavaFX entry point so that packaged applications
 * can load the JavaFX runtime from the classpath.
 */
public final class Launcher {
    /**
     * Prevents instantiation of this launcher class.
     */
    private Launcher() {
    }

    /**
     * Launches Eden's JavaFX application.
     *
     * @param args command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
