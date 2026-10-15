package et;

import et.gui.MainWindow;

import javafx.application.Application;

/**
 * Launches ET's JavaFX application without extending the JavaFX Application class.
 */
public final class Launcher {
    /** Prevents instantiation of the application launcher. */
    private Launcher() {
    }

    /**
     * Starts the JavaFX runtime and opens ET's main window.
     *
     * @param args command-line arguments passed to JavaFX
     */
    public static void main(String[] args) {
        Application.launch(MainWindow.class, args);
    }
}
