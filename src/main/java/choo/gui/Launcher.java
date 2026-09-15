package choo.gui;

import javafx.application.Application;

/**
 * Launches the JavaFX application without extending {@link Application}.
 */
public class Launcher {
    /**
     * Starts the CHOO JavaFX application.
     *
     * @param args Command-line arguments forwarded to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
