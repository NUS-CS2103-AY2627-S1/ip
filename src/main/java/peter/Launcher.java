package peter;

import javafx.application.Application;

/**
 * Launches Peter's JavaFX application from a plain class, avoiding classpath
 * issues that occur when a class extending {@link javafx.application.Application}
 * is used directly as the runnable main class.
 */
public class Launcher {
    /**
     * Starts Peter's graphical interface.
     *
     * @param args command-line arguments, which are not used
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
