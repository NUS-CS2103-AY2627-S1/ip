package caitlyn;

import javafx.application.Application;

/**
 * Starts Caitlyn's JavaFX application.
 *
 * <p>A separate launcher keeps JavaFX startup out of the application class and avoids
 * initializing JavaFX from a command-line test process.</p>
 */
public final class Launcher {
    /**
     * Prevents construction of this utility class.
     */
    private Launcher() {
    }

    /**
     * Launches Caitlyn's graphical user interface.
     *
     * @param args command-line arguments supplied when the program starts.
     */
    public static void main(String[] args) {
        Application.launch(CaitlynGui.class, args);
    }
}
