package zeus.gui;

import javafx.application.Application;

/** Launches the JavaFX application without extending {@link Application}. */
public final class Launcher {
    /** Prevents instantiation of the launcher utility class. */
    private Launcher() {
    }

    /**
     * Starts the Zeus graphical interface.
     *
     * @param args Command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
