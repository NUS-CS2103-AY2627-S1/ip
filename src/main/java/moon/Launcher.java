package moon;

import javafx.application.Application;

/** Starts Moon's JavaFX application through a separate launcher class. */
public final class Launcher {
    private Launcher() {
    }

    /** Launches the JavaFX application. */
    public static void main(String[] args) {
        Application.launch(MoonApplication.class, args);
    }
}
