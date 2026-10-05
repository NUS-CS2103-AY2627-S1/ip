package ev;

import javafx.application.Application;

/**
 * Launches E.V.'s graphical user interface, working around a classpath issue.
 */
public class Launcher {
    /**
     * Starts the JavaFX application.
     *
     * @param args command line arguments; not used.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
