package greenchonk.gui;

import javafx.application.Application;

/**
 * Launches the JavaFX application without extending {@link Application}.
 */
public final class Launcher {
    private Launcher() {
    }

    /**
     * Starts Green Chonk's graphical interface.
     *
     * @param args command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
