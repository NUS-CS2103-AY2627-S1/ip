package kelore;

import javafx.application.Application;

/** Starts the JavaFX application. */
public final class Launcher {
    private Launcher() {
    }

    /**
     * Launches the Kelore GUI.
     *
     * @param args Command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
