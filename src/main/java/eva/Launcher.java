package eva;

import javafx.application.Application;

/**
 * Launches Eva's JavaFX interface without inheriting from Application.
 */
public class Launcher {

    /**
     * Starts the JavaFX application.
     *
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
