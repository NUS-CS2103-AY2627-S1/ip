package alexa;

import javafx.application.Application;

/** Launches the JavaFX application to avoid JavaFX classpath issues. */
public class Launcher {
    /** Starts the JavaFX application. */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}