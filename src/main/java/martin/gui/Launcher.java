package martin.gui;

import javafx.application.Application;

/**
 * A launcher class to work around JavaFX classpath issues.
 */
public class Launcher {

    /**
     * Launches the JavaFX Hello World application.
     *
     * @param args Command-line arguments passed to the application.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
