package gary.gui;

import java.io.IOException;

import javafx.application.Application;

/**
 * Launches Gary through a non-JavaFX entry point.
 */
public class Launcher {
    /**
     * Starts the Gary JavaFX application.
     *
     * @param args Command-line arguments passed to JavaFX.
     * @throws IOException If native libraries cannot be prepared.
     */
    public static void main(String[] args) throws IOException {
        MacNativeLibraries.prepare();
        Application.launch(Main.class, args);
    }
}
