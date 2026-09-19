package nori;

import javafx.application.Application;

/**
 * Starts Nori without directly launching a JavaFX {@link Application} subclass.
 */
public final class Launcher {
    /** Prevents instantiation of the application entry point. */
    private Launcher() {
    }

    /**
     * Launches Nori's JavaFX application.
     *
     * @param args command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
