package miku;

import javafx.application.Application;

/** Launches Miku through a non-JavaFX entry point to avoid JavaFX classpath issues. */
public final class Launcher {
    private Launcher() {
    }

    /** Starts the JavaFX application. */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
