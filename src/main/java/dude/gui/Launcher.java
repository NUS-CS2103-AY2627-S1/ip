package dude.gui;

import javafx.application.Application;

/**
 * Starts the JavaFX application.
 */
public class Launcher {
    private Launcher() {
    }

    /**
     * Launches the GUI application.
     *
     * @param args Command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
