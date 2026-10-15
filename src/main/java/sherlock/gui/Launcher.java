package sherlock.gui;

import javafx.application.Application;

/**
 * Starts the JavaFX application without making the Application subclass the JAR entry point.
 */
public class Launcher {
    /**
     * Launches Sherlock's graphical user interface.
     *
     * @param args command-line arguments passed to JavaFX
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
