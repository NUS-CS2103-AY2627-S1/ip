package benjamin;

import benjamin.gui.Main;
import javafx.application.Application;

/**
 * Starts the graphical interface.
 *
 * <p>A class that does not itself extend {@code Application} is needed as the
 * entry point, otherwise the JavaFX runtime refuses to start when the app is
 * launched from a fat JAR.
 */
public class Launcher {
    /**
     * Launches the chat window.
     *
     * @param args passed straight through to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
