package woofer.gui;

import javafx.application.Application;

/**
 * Provides the non-Application entry point required to launch the JavaFX GUI.
 */
public class Launcher {
    private Launcher() {
    }

    /**
     * Starts the Woofer JavaFX application.
     *
     * @param args command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
