package oz;

import javafx.application.Application;

/**
 * A launcher class to workaround classpath issues.
 */
public class Launcher {
    /**
     * Starts the JavaFX application by launching {@link Main}.
     *
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
