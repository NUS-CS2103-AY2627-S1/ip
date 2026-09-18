package plana;

import javafx.application.Application;
import plana.gui.Main;

/**
 * Launches Plana's JavaFX application.
 */
public class Launcher {
    /**
     * Starts the JavaFX runtime through Plana's application class.
     *
     * @param args command-line arguments passed to the JavaFX application.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
