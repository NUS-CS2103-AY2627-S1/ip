package unicorn;

import javafx.application.Application;

/**
 * Starts the JavaFX application without extending {@link Application}.
 */
public class Launcher {
    /**
     * Launches the Unicorn JavaFX application.
     *
     * @param args command-line arguments passed to JavaFX
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
