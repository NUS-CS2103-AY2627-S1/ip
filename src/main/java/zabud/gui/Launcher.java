package zabud.gui;

import javafx.application.Application;

/**
 * Launches JavaFX without making the application class the JAR entry point.
 */
public final class Launcher {
    /**
     * Prevents instantiation of this launcher class.
     */
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
