package nori;

import javafx.application.Application;

/**
 * Launches Nori's JavaFX application without inheriting from {@link Application}.
 */
public class Launcher {
    /**
     * Starts the JavaFX application.
     *
     * @param args command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
