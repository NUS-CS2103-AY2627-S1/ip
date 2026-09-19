package mochi;

import javafx.application.Application;

/**
 * Launches the JavaFX application outside the {@link Application} subclass.
 */
public class Launcher {
    /**
     * Creates the launcher used as the application entry point.
     */
    public Launcher() {
    }

    /**
     * Starts the Mochi JavaFX application.
     *
     * @param args Command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
