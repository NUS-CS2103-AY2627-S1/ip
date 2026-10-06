package duke;

import javafx.application.Application;

/**
 * A launcher class to workaround classpath issues when running fat JARs.
 */
public class Launcher {
    /**
     * Prevents instantiation of this utility launcher class.
     */
    private Launcher() {
    }

    /**
     * Launches the JavaFX application.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        Application.launch(MainApp.class, args);
    }
}
