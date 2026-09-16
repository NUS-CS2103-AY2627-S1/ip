package brucli;

import javafx.application.Application;

/**
 * Launches the JavaFX application without module-path class-loading issues.
 */
public class Launcher {
    /**
     * Starts the BruCLI JavaFX application.
     *
     * @param args Command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
