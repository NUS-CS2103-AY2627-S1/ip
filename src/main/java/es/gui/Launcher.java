package es.gui;

import javafx.application.Application;

/** Launches Es's JavaFX interface. */
public final class Launcher {
    private Launcher() {
    }

    /** Starts JavaFX. */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
