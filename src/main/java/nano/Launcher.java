package nano;

import javafx.application.Application;

/**
 * Launches the Nano JavaFX application.
 */
public final class Launcher {
    private Launcher() {
    }

    /**
     * Starts JavaFX using the main window class.
     *
     * @param args The command-line arguments.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
