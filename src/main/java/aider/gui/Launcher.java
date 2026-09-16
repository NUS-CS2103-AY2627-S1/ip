package aider.gui;

import javafx.application.Application;

/** Starts the JavaFX application through a non-Application entry point. */
public final class Launcher {
    private Launcher() {
    }

    /** Launches the Aider graphical user interface. */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
