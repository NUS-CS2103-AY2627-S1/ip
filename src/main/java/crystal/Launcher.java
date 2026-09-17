package crystal;

import crystal.gui.CrystalApplication;
import javafx.application.Application;

/**
 * Starts Crystal's JavaFX application without extending the JavaFX application class.
 */
public final class Launcher {
    /**
     * Prevents construction because this class provides only the application entry point.
     */
    private Launcher() {
    }

    /**
     * Launches the Crystal JavaFX application.
     *
     * @param args command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(CrystalApplication.class, args);
    }
}
