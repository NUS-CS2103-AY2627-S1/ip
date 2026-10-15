package bob.gui;

import javafx.application.Application;

/**
 * Launches JavaFX from a class that does not extend {@link Application}.
 */
public class Launcher {
    /**
     * Starts Bob's JavaFX application.
     *
     * @param args command-line arguments passed to JavaFX
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
