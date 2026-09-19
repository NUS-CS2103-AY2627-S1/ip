package nova;

import javafx.application.Application;

/**
 * A launcher class to work around classpath issues.
 */
public class Launcher {
    /**
     * Launches the JavaFX application.
     *
     * @param args unused
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
