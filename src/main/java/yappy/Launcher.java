package yappy;

import javafx.application.Application;
import yappy.gui.Main;

/**
 * Starts the JavaFX application without extending {@link Application}.
 * This separate entry point avoids classpath issues in packaged JavaFX applications.
 */
public class Launcher {
    /**
     * Launches Yappy's JavaFX application.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
