package bogos;

import javafx.application.Application;

/**
 * Launches the JavaFX application without Java's JavaFX classpath restrictions.
 */
public class Launcher {
    /**
     * Starts the Bogos graphical application.
     *
     * @param args Command-line arguments, which are forwarded to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
