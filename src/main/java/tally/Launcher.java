package tally;

import javafx.application.Application;
import tally.gui.Main;

/**
 * Starts the graphical Tally.
 *
 * <p>Launching is done from here rather than from the Application subclass itself,
 * because a packaged jar whose main class extends Application fails to start with a
 * missing JavaFX runtime error. Going through a class that merely calls launch
 * avoids it.
 */
public class Launcher {
    /**
     * Starts the chatbot's window.
     *
     * @param args command-line arguments, which Tally does not use.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
