package toothless;

import javafx.application.Application;

/**
 * Launches the JavaFX application without extending {@link Application} itself.
 */
public final class Launcher {
    private Launcher() {
    }

    /**
     * Starts the Toothless JavaFX application.
     *
     * @param args command-line arguments forwarded to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
