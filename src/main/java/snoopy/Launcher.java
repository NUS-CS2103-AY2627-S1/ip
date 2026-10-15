package snoopy;

import javafx.application.Application;
import snoopy.gui.Main;

/**
 * Launches the JavaFX application while avoiding classpath issues.
 */
public class Launcher {

    /**
     * Starts Snoopy's graphical user interface.
     *
     * @param args Command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
