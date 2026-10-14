package milo;

import javafx.application.Application;
import milo.gui.Main;

/**
 * Launches Milo's JavaFX application without extending {@link Application}.
 */
public class Launcher {
    /**
     * Starts the graphical application.
     *
     * @param args command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
