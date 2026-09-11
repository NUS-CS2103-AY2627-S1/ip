package lynn;

import java.awt.GraphicsEnvironment;
import java.io.IOException;

import javafx.application.Application;

/** Launches Lynn's JavaFX application. */
public final class Launcher {
    private Launcher() {
    }

    /**
     * Starts the JavaFX runtime.
     *
     * @param args command-line arguments, which are not used
     */
    public static void main(String[] args) {
        if (GraphicsEnvironment.isHeadless() || hasPipedInput()) {
            new Lynn().run();
        } else {
            Application.launch(LynnGui.class, args);
        }
    }

    /** Returns whether commands are already available from a piped standard input. */
    private static boolean hasPipedInput() {
        try {
            return System.in.available() > 0;
        } catch (IOException e) {
            return false;
        }
    }
}
