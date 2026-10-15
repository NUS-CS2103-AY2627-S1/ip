package lenzabot;

import javafx.application.Application;
import lenzabot.gui.Main;

/**
 * Launches LenZaBot without extending JavaFX's Application class.
 */
public class Launcher {
    /**
     * Starts the JavaFX application.
     *
     * @param args Command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
