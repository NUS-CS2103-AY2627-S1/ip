package student.project.bob.util;

import javafx.application.Application;

/**
 * A launcher class to work around classpath issues.
 */
public class Launcher {
    /**
     * Starts the JavaFX application through the launcher class.
     *
     * @param args command-line arguments passed to JavaFX
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
