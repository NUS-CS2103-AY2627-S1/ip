package duchess.gui;

import javafx.application.Application;

/** Provides the non-Application entry point required by JavaFX on the classpath. */
public final class Launcher {
    /** Prevents creation of a launcher instance. */
    private Launcher() {
    }

    /** Starts the Duchess JavaFX application. */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
