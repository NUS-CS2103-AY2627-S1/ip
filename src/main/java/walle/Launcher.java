package walle;

import javafx.application.Application;

/**
 * A launcher class to work around a classpath issue when the JavaFX
 * application is run directly from the packaged JAR.
 */
public class Launcher {

    /**
     * Starts the JavaFX GUI.
     *
     * @param args unused command-line arguments.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }

}
