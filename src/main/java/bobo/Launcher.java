package bobo;

import javafx.application.Application;

/**
 * Entry point launcher class for Bobo JavaFX application.
 * Workaround for JavaFX classpath issues when launching shaded/fat JAR files.
 */
public class Launcher {

    /**
     * Main entry point launcher.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
