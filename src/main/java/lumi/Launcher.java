package lumi;

import javafx.application.Application;

/**
 * Launches Lumi through a separate entry point to work around JavaFX classpath issues.
 */
public class Launcher {

    /**
     * Starts the JavaFX application.
     *
     * @param args Command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
