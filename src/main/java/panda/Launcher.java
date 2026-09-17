package panda;

import javafx.application.Application;
import panda.gui.Main;

/**
 * Launches Panda without directly extending the JavaFX application class.
 */
public final class Launcher {
    private Launcher() {
        // Entry point class: prevent instantiation.
    }

    /**
     * Starts the JavaFX application.
     *
     * @param args Command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
