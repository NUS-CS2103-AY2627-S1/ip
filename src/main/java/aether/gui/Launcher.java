package aether.gui;

import javafx.application.Application;

/** Provides a non-JavaFX entry point so the packaged application starts reliably. */
public class Launcher {
    /**
     * Starts the JavaFX application.
     *
     * @param args command-line arguments forwarded to JavaFX
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
