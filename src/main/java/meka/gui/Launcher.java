package meka.gui;

import javafx.application.Application;

/**
 * Starts the JavaFX application without extending {@link Application} directly.
 */
public final class Launcher {
    private Launcher() {
    }

    /**
     * Launches the MEKA graphical interface.
     *
     * @param args command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
