package bo.gui;

import javafx.application.Application;

/** Launches Bo's JavaFX application. */
public final class Launcher {
    /** Prevents instantiation of this launcher utility class. */
    private Launcher() {
    }

    /**
     * Starts the JavaFX application without directly extending {@link Application}.
     *
     * @param args command-line arguments passed to the JavaFX application.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
