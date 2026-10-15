package milo;

import javafx.application.Application;

/** A launcher class that works around JavaFX classpath issues. */
public class Launcher {
    /** Starts the JavaFX application. */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
