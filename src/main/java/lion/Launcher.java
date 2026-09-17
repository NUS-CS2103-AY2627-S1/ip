package lion;

import javafx.application.Application;

/**
 * A launcher class to workaround classpath issues.
 */
public class Launcher {
    /**
     * Starts the JavaFX application via {@link LionApplication}.
     *
     * @param args command-line arguments, forwarded to {@link Application#launch}.
     */
    public static void main(String[] args) {
        Application.launch(LionApplication.class, args);
    }
}
