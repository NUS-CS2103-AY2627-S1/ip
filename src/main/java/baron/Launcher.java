package baron;

import javafx.application.Application;

/**
 * Launches the Baron application while avoiding JavaFX classpath issues.
 */
public class Launcher {
    /**
     * Prevents instantiation of this utility class.
     */
    private Launcher() {
    }

    /**
     * Launches the Baron JavaFX application.
     *
     * @param args Command-line arguments.
     */
    static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
