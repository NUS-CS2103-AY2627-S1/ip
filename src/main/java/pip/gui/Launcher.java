package pip.gui;

import javafx.application.Application;

/** Launches the JavaFX application without classpath issues. */
public class Launcher {
    /** Prevents instantiation of this entry-point utility. */
    private Launcher() {
    }

    /**
     * Starts the JavaFX application through a separate classpath entry point.
     *
     * @param args Command-line arguments passed to JavaFX.
     */
    public static void main(String... args) {
        Application.launch(Main.class, args);
    }
}
