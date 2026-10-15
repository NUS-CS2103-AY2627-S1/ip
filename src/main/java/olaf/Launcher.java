package olaf;

import javafx.application.Application;

/**
 * Launches the JavaFX application without extending {@link Application}.
 */
public final class Launcher {
    private Launcher() {
    }

    /**
     * Starts the Olaf graphical interface.
     *
     * @param args command-line arguments forwarded to JavaFX
     */
    public static void main(String[] args) {
        Application.launch(OlafApplication.class, args);
    }
}
