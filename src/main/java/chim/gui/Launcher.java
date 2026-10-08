package chim.gui;

import javafx.application.Application;

/**
 * A launcher class to workaround classpath issues.
 */
public class Launcher {

    /**
     * Launches the JavaFX application via the Main class.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
