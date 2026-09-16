package serangooner.gui;

import javafx.application.Application;

/**
 * Starts the graphical chatbot.
 * JavaFX refuses to start from a class that extends {@link javafx.application.Application Application}
 * unless the JavaFX modules are on the module path, which they are not here. Launching from a class
 * that does not extend it works around that, so this class exists only to hold the entry point.
 */
public class Launcher {
    /**
     * Opens the chat window.
     *
     * @param args Command line arguments, which are passed on to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
