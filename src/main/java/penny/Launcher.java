package penny;

import javafx.application.Application;

/**
 * Serves as the main entry point to launch the JavaFX application.
 * Workaround for classpath and module loading issues.
 */
public class Launcher {

    /**
     * Launches the JavaFX application.
     *
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
