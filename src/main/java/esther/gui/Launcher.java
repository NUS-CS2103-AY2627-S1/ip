package esther.gui;

import javafx.application.Application;

/**
 * Starts the Esther JavaFX application.
 */
public final class Launcher {
    private Launcher() {
    }

    /**
     * Launches the Esther JavaFX application.
     *
     * @param args Application arguments.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
