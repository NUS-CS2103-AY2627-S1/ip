package bob;

import bob.ui.Main;
import javafx.application.Application;

/**
 * Provides an entry point for launching JavaFX from the bundled JAR.
 */
public class Launcher {
    /**
     * Starts Bob's graphical interface using the bundled JavaFX runtime.
     *
     * @param args Command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
