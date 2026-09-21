package friday;

import javafx.application.Application;

/** Launches the JavaFX application without triggering JavaFX classpath handling issues. */
public class Launcher {
    /** Starts Friday's JavaFX application. */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
