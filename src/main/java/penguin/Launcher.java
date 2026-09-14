package penguin;

import javafx.application.Application;

import penguin.gui.PenguinGui;

/**
 * A launcher class to workaround classpath issues.
 */
public class Launcher {
    /**
     * Prevents instantiation of this application launcher.
     */
    private Launcher() {
    }

    /**
     * Launches the Penguin JavaFX application.
     *
     * @param args command-line arguments.
     */
    public static void main(String[] args) {
        Application.launch(PenguinGui.class, args);
    }
}
