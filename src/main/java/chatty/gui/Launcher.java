package chatty.gui;

import javafx.application.Application;

/**
 * Launches the JavaFX application without extending {@link Application} itself.
 */
public class Launcher {
    /** Prevents instantiation of this application-launching utility class. */
    private Launcher() {
    }

    /**
     * Starts the Chatty JavaFX application.
     *
     * @param args command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
