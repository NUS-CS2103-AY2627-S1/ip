package nova;

import javafx.application.Application;

/**
 * Launches the JavaFX application without extending Application.
 */
public final class Launcher {

    private Launcher() {
    }

    /**
     * Starts the Nova GUI.
     *
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
