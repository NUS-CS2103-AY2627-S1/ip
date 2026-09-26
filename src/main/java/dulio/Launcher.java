package dulio;

import javafx.application.Application;

/**
 * Starts the Dulio JavaFX application.
 */
public class Launcher {
    private Launcher() {
    }

    /**
     * Launches the JavaFX application.
     *
     * @param args command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(DulioApp.class, args);
    }
}
