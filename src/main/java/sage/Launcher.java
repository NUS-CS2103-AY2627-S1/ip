package sage;

import javafx.application.Application;

import sage.gui.Main;

/**
 * Launches the Sage GUI while avoiding JavaFX class-path issues.
 */
public final class Launcher {
    private Launcher() {
    }

    /**
     * Starts the JavaFX application.
     *
     * @param args Command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
