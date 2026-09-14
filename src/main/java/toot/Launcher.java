package toot;

import javafx.application.Application;

/**
 * Launches the JavaFX application without inheriting from {@link Application}.
 */
public final class Launcher {
    private Launcher() {
    }

    /**
     * Starts Toot's graphical interface.
     *
     * @param args Command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
