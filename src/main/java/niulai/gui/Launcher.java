package niulai.gui;

import javafx.application.Application;

/** Provides the JavaFX entry point required by the application plugin. */
public final class Launcher {
    private Launcher() {
    }

    /** Launches the NiuLai JavaFX application. */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
