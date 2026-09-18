package phin;

import javafx.application.Application;

/**
 * Launches JavaFX from a class that does not extend {@link Application}.
 */
public class Launcher {
    /**
     * Starts the graphical application.
     *
     * @param args Command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
