package jaku;

import javafx.application.Application;

/** Starts Jaku's JavaFX application. */
public final class Launcher {
    private Launcher() {
    }

    /**
     * Launches the graphical interface.
     *
     * @param args command-line arguments, which the launcher does not use
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
