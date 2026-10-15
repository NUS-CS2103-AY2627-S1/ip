package sine;

import javafx.application.Application;

/**
 * Launches the JavaFX application without extending {@link Application}.
 */
public class Launcher {

    /**
     * Starts the JavaFX runtime and opens the HelloWorld window.
     *
     * @param args Command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
