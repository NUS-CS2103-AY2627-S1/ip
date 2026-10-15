package peanutbuttercat;

import javafx.application.Application;

/**
 * Launches the JavaFX application without triggering JavaFX classpath issues.
 */
public class Launcher {

    /**
     * Starts the graphical application.
     *
     * @param args Command-line arguments passed to the application.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
