package ev.gui;

import javafx.application.Application;

/**
 * Starts the window.
 *
 * <p>The entry point is this class rather than {@link Main} because JavaFX is on the
 * class path rather than the module path: a main class that extends {@code Application}
 * would have to be resolved before the toolkit is running, and would be rejected.
 */
public class Launcher {

    /**
     * Starts the chatbot in a window.
     *
     * @param args command line arguments, which are not used.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
