package nico;

import javafx.application.Application;

/**
 * Launches Nico without directly using the JavaFX application class as the entry point.
 */
public class Launcher {
    /**
     * Launches the JavaFX application through a classpath-safe entry point.
     *
     * @param args command-line arguments passed to the application.
     */
    public static void main(String[] args) {
        Application.launch(Ui.class, args);
    }
}
