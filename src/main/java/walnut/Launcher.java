package walnut;

import javafx.application.Application;

/**
 * A launcher class used to start the JavaFX application.
 *
 * <p>This class provides an entry point that launches the {@link Main}
 * JavaFX application.</p>
 */
public class Launcher {

    /**
     * Launches the JavaFX application.
     *
     * @param args command-line arguments passed to the application
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
