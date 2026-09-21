package quackie;

import javafx.application.Application;
import quackie.gui.Main;

/**
 * Launches Quackie's JavaFX interface without extending {@link Application}.
 */
public class Launcher {
    /**
     * Starts the JavaFX application.
     *
     * @param args command-line arguments passed to JavaFX
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
