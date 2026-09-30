package kong;

import javafx.application.Application;

/**
 * Launches Kong without requiring JavaFX classes on the initial JVM class path.
 */
public class Launcher {
    /**
     * Starts the JavaFX application.
     *
     * @param args command-line arguments forwarded to JavaFX
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
