package sandrone;

import javafx.application.Application;

/** Launches the JavaFX application without JavaFX classpath issues. */
public class Launcher {
    /** Starts the JavaFX application. */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
