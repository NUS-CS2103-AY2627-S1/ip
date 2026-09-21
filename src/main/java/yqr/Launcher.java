package yqr;

import javafx.application.Application;
import yqr.gui.Main;

/**
 * Starts the JavaFX application without extending {@link Application} itself.
 */
public final class Launcher {
    /** Prevents creation of this launcher utility class. */
    private Launcher() {
    }

    /**
     * Launches the yqr GUI.
     *
     * @param args command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        JavaFxNativeLibraries.configure();
        Application.launch(Main.class, args);
    }
}
