package hermes;

import javafx.application.Application;

/**
 * A launcher class to workaround classpath issues.
 */
public class Launcher {
    /**
     * Starts the application.
     *
     * @param args command line arguments, which Hermes does not use.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}

