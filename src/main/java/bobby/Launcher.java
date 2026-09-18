package bobby;

import bobby.gui.Main;
import javafx.application.Application;

/**
 * Launches Bobby's JavaFX application without extending {@link Application}.
 */
public class Launcher {
    /**
     * Starts the JavaFX application.
     *
     * @param args command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
