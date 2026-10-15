package wwaffle;

import javafx.application.Application;
import wwaffle.gui.Main;

/**
 * Launches the JavaFX application without extending {@link Application}.
 */
public class Launcher {
    /**
     * Starts the WWaffle graphical interface.
     *
     * @param args Command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
