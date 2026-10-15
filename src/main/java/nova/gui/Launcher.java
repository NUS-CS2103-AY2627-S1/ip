package nova.gui;

import javafx.application.Application;

/** Launches the JavaFX application through a dedicated entry point. */
public final class Launcher {
    private Launcher() {
    }

    /** Launches the Nova JavaFX application. */
    public static void main(String... args) {
        Application.launch(Main.class, args);
    }
}
