package larry;

import javafx.application.Application;

/**
 * Launches the JavaFX application without triggering JavaFX classpath issues.
 */
public class Launcher {

    /**
     * Starts the JavaFX runtime and opens the EVIL LARRY application.
     *
     * @param args Command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
