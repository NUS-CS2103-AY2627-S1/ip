package zucc;

import javafx.application.Application;

/**
 * Launches JavaFX without making the application entry point a JavaFX subclass.
 */
public final class Launcher {
    /** Prevents creation of this application entry-point class. */
    private Launcher() {
    }

    /**
     * Starts the JavaFX application.
     *
     * @param args command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
