package nelson;

import javafx.application.Application;

/** Starts the JavaFX application through a non-Application launcher class. */
public final class Launcher {
    private Launcher() {
        // Prevent instantiation.
    }

    /**
     * Launches Nelson's JavaFX interface.
     *
     * @param args command-line arguments passed to JavaFX
     */
    public static void main(String[] args) {
        Application.launch(NelsonGui.class, args);
    }
}
