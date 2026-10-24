package clara.gui;

import javafx.application.Application;

/**
 * A launcher class to workaround classpath issues with JavaFX runtime.
 */
public class Launcher {

    /**
     * Entry point of the application launching the JavaFX GUI.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
