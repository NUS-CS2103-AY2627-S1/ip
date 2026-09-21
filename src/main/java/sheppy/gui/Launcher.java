package sheppy.gui;

import java.io.IOException;

import javafx.application.Application;

/** Launches the JavaFX application without extending the chatbot's core class. */
public class Launcher {
    /** Prevents instantiation of this application launcher. */
    private Launcher() {
    }

    /**
     * Starts Sheppy's JavaFX application.
     *
     * @param args command-line arguments passed to JavaFX
     * @throws IOException if packaged native libraries cannot be prepared
     */
    public static void main(String[] args) throws IOException {
        MacNativeLibraries.prepare();
        Application.launch(Main.class, args);
    }
}
