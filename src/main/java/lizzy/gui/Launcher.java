package lizzy.gui;

import javafx.application.Application;

/**
 * Starts the JavaFX application without extending {@link Application} at the executable entry point.
 */
public class Launcher {
    /**
     * Launches Lizzy's graphical interface.
     *
     * @param args command-line arguments passed to JavaFX
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
