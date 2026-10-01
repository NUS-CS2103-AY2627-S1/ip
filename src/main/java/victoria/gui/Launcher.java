package victoria.gui;

import javafx.application.Application;

/** Launches the JavaFX version of Victoria without JavaFX classpath restrictions. */
public final class Launcher {
    /** Prevents construction because this class only provides the application entry point. */
    private Launcher() { }

    /** Starts the JavaFX application. */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
