package arin;

import java.io.IOException;

import javafx.application.Application;

/**
 * Launches the JavaFX application through a separate entry point.
 */
public class Launcher {
    /**
     * Starts the JavaFX application.
     *
     * @param args Command-line arguments passed to JavaFX.
     * @throws IOException If this platform's bundled native libraries cannot be prepared.
     */
    public static void main(String[] args) throws IOException {
        NativeLibraries.prepare();
        Application.launch(Main.class, args);
    }
}
