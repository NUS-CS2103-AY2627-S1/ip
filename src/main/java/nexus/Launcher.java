package nexus;

import javafx.application.Application;

/** Launches Nexus through JavaFX to avoid classpath issues. */
public class Launcher {
    /** Starts the JavaFX application. */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
