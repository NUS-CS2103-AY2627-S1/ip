package computah;

import javafx.application.Application;

/**
 * Launches the JavaFX version of Computah.
 */
public class Launcher {

    private Launcher() {
    }

    /**
     * Starts the JavaFX Computah application.
     *
     * @param args command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
