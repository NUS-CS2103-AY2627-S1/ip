package tasque;

import javafx.application.Application;

/**
 * Launches the Tasque JavaFX application without extending {@link Application}.
 */
public class Launcher {
    private Launcher() {
    }

    /**
     * Starts the JavaFX application.
     *
     * @param args Command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
