package bobby;

import javafx.application.Application;

/** A launcher class to work around classpath issues. */
public class Launcher {
    /** Launches the JavaFX application. */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
