package geek.gui;

import javafx.application.Application;

/**
 * Starts the JavaFX application without extending {@link Application} itself.
 */
public final class Launcher {
    private Launcher() {
    }

    /**
     * Launches the Geek graphical interface.
     *
     * @param args Command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
