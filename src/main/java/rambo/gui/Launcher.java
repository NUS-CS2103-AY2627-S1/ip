package rambo.gui;

import javafx.application.Application;

/**
 * A launcher class to workaround classpath issues.
 */
public class Launcher {
    /**
     * Starts the JavaFX application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
