package augustus;

import javafx.application.Application;

/**
 * Launches the JavaFX application.
 */
public class Launcher {

    /**
     * Prevents instantiation of the launcher class.
     */
    private Launcher() {
    }
    /**
     * Starts the JavaFX application.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
