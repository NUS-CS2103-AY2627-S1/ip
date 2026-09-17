package orbit;

import javafx.application.Application;

/**
 * Starts the JavaFX application without inheriting from {@link Application}.
 */
public class Launcher {
    /**
     * Launches Orbit's graphical interface.
     *
     * @param args Command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
