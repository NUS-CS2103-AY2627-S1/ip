package jeff.gui;

import javafx.application.Application;

/**
 * Launches {@link MainApp}. This indirection exists because the JVM refuses
 * to start a JavaFX application whose main class directly extends
 * {@link javafx.application.Application} when run without a module path.
 */
public class Main {
    /**
     * Launches the JavaFX GUI.
     *
     * @param args unused; Jeff takes no command-line arguments.
     */
    public static void main(String[] args) {
        Application.launch(MainApp.class, args);
    }
}
