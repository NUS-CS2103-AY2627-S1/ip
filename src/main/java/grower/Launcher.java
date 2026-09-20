package grower;

import grower.gui.Main;
import javafx.application.Application;

/**
 * Launches JavaFX through a class that does not extend {@link Application}.
 * This avoids JavaFX runtime classpath issues when starting the packaged application.
 */
public class Launcher {
    /**
     * Launches the JavaFX application.
     *
     * @param args Command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
