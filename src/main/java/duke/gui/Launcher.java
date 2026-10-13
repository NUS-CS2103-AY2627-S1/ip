package duke.gui;

import java.io.IOException;

import javafx.application.Application;

/**
 * Launches the JavaFX application without relying on the Application subclass as the entry point.
 */
public final class Launcher {
    private Launcher() {
    }

    /**
     * Starts Wangsa's JavaFX application.
     *
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        try {
            NativeLibraries.prepare();
        } catch (IOException e) {
            System.err.println("Wangsa could not start: " + e.getMessage());
            System.exit(1);
            return;
        }
        Application.launch(Main.class, args);
    }
}
