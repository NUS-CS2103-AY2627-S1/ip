package yokohama;

import javafx.application.Application;

/**
 * Launches the JavaFX application.
 *
 * <p>This separate entry point follows the JavaFX tutorial's workaround for
 * classpath handling while preserving the project's configured main class.</p>
 */
public class Yokohama {
    /**
     * Starts the HelloWorld JavaFX application.
     *
     * @param args Command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
