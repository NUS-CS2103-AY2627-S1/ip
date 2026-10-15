package jeff;

import javafx.application.Application;

/**
 * Launches the JavaFX application.
 */
public class Launcher {
    private Launcher() {
    }
    /**
     * Starts the JavaFX application.
     *
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
